package com.wanghongfei.springboot.starter.nettyweb.network;

import com.wanghongfei.springboot.starter.nettyweb.autoconfig.NettyWebProp;
import com.wanghongfei.springboot.starter.nettyweb.error.NettyWebStartException;
import com.wanghongfei.springboot.starter.nettyweb.network.handler.NettyWebHandler;
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

import javax.annotation.PreDestroy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by wanghongfei on 2019/11/4.
 */
@Slf4j
public class NettyWebServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    @Autowired
    private NettyWebHandler handler;

    @Autowired
    private NettyWebProp prop;

    private volatile boolean isClosing = false;

    public void start() {
        start(null);
    }

    public void startWithCallback(Consumer<Throwable> callback) {
        if (null == callback) {
            throw new NettyWebStartException("callback cannot be null");
        }

        start(callback);
    }

    private void start(Consumer<Throwable> callback) {
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

                ChannelFuture f = bootstrap.bind(prop.getPort()).sync();
                log.info("NettyWeb started at {}", prop.getPort());

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
            webStartLatch.await(10, TimeUnit.SECONDS);
            if (null != excaptionBox.getValue()) {
                invokeCallback(callback, excaptionBox.getValue());
                throw excaptionBox.getValue();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            callback.accept(e);
        }

        invokeCallback(callback, null);
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

    private void invokeCallback(Consumer<Throwable> callback, Throwable err) {
        if (null != callback) {
            callback.accept(err);
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
