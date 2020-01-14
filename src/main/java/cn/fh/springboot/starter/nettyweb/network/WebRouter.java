package cn.fh.springboot.starter.nettyweb.network;

import cn.fh.springboot.starter.nettyweb.error.NettyWebStartException;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghongfei on 2020/1/14.
 */
@Slf4j
public class WebRouter {
    private RouteInfo postRouteInfo = new RouteInfo();
    private RouteInfo getRouteInfo = new RouteInfo();


    /**
     * 注册路由
     * @param method 请求方法
     * @param path 严格路径
     * @param voType 参数对象类型
     * @param service 请求处理器
     */
    public void registerService(HttpMethod method, String path, Class voType, RequestHandler service) {
        log.info("register service: {} {} -> {}, param type: {}", method, path, service.getClass(), voType);

        RouteInfo targetRoute = switchRouteInfo(method);

        targetRoute.pathTypeMap.put(path, voType);
        targetRoute.pathServiceMap.put(path, service);
    }

    public RequestHandler matchHandler(HttpMethod method, String path) {
        RouteInfo targetRoute = switchRouteInfo(method);
        String rawPath = trimPathIfNecessary(method, path);

        return targetRoute.getPathServiceMap().get(rawPath);
    }

    public Class matchArgType(HttpMethod method, String path) {
        RouteInfo targetRoute = switchRouteInfo(method);
        String rawPath = trimPathIfNecessary(method, path);

        return targetRoute.getPathTypeMap().get(rawPath);
    }

    private String trimPathIfNecessary(HttpMethod method, String path) {
        if (method != HttpMethod.GET) {
            return path;
        }

        int paramIndex = path.indexOf("?");
        if (-1 == paramIndex) {
            return path;
        }

        return path.substring(0, paramIndex);
    }

    private RouteInfo switchRouteInfo(HttpMethod method) {
        if (method == HttpMethod.GET) {
            return getRouteInfo;

        } else if (method == HttpMethod.POST) {
            return postRouteInfo;

        } else {
            throw new NettyWebStartException("unsupported method " + method);
        }
    }

    @Data
    private static class RouteInfo {
        private Map<String, Class> pathTypeMap = new HashMap<>();
        private Map<String, RequestHandler> pathServiceMap = new HashMap<>();
    }
}
