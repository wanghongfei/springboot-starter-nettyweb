package cn.fh.springboot.starter.nettyweb.network;

/**
 * 所有业务类都要实现此接口
 *
 * @param <T> 请求参数类型
 */
public interface RequestHandler<T, R> {
    R serveRequest(T request);
}
