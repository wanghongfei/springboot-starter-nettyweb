package cn.fh.springboot.starter.nettyweb.network;

import cn.fh.springboot.starter.nettyweb.autoconfig.NettyWebProp;
import cn.fh.springboot.starter.nettyweb.error.NettyWebStartException;
import cn.fh.springboot.starter.nettyweb.network.handler.NettyWebHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghongfei on 2019/11/4.
 */
@Slf4j
public class NettyWebServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    @Value("${server.port}")
    private int port;

    @Autowired
    private NettyWebHandler handler;

    @Autowired
    private NettyWebProp prop;

    private volatile boolean isClosing = false;

    public void start() {
        log.info("Starting NettyWeb");

        CountDownLatch webStartLatch = new CountDownLatch(1);
        MutableObject<RuntimeException> excaptionBox = new MutableObject<>();

        Runnable webStartLogic = () -> {
            initEventLoop();

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workGroup)
                        .channel(determineServerSocketChannel())
                        //.option(ChannelOption.SO_BACKLOG, 100)
                        //.handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast(new HttpServerCodec());
                                p.addLast("httpAggregator",new HttpObjectAggregator(prop.getHttpObjectMaxSize()));
                                p.addLast(handler);
                            }
                        });

                ChannelFuture f = bootstrap.bind(port).sync();
                log.info("NettyWeb started at {}", port);

                webStartLatch.countDown();

                f.channel().closeFuture().sync();
                stop();
                log.info("NettyWeb stopped");

            } catch (Throwable e) {
                log.error("", e);

                RuntimeException newExp = new NettyWebStartException("failed to start NettyWeb", e);
                excaptionBox.setValue(newExp);

                stop();
                webStartLatch.countDown();
            }

        };

        Thread webThread  = new Thread(webStartLogic);
        webThread.start();

        try {
            webStartLatch.await(30, TimeUnit.SECONDS);
            if (null != excaptionBox.getValue()) {
                throw excaptionBox.getValue();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        if (!isClosing) {
            isClosing = true;

            log.info("shutting down all event loop");

            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            handler.close();
        }
    }

    private Class determineServerSocketChannel() {
        if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        }

        return NioServerSocketChannel.class;
    }

    private void initEventLoop() {
        int bossCount = prop.getBossGroupThreadCount();
        int workCount = prop.getWorkGroupThreadCount();

        if (Epoll.isAvailable()) {
            log.info("epoll enabled");
            bossGroup = new EpollEventLoopGroup(bossCount);
            workGroup = new EpollEventLoopGroup(workCount);

        } else {
            bossGroup = new NioEventLoopGroup(bossCount);
            workGroup = new NioEventLoopGroup(workCount);
        }
    }
}
