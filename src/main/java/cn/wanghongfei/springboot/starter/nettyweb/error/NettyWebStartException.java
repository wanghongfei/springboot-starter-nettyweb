package cn.wanghongfei.springboot.starter.nettyweb.error;

/**
 * Created by wanghongfei on 2020/1/7.
 */
public class NettyWebStartException extends WebException {
    public NettyWebStartException(String msg) {
        super(msg);
    }

    public NettyWebStartException(String msg, boolean recordStackTrace) {
        super(msg, recordStackTrace);
    }

    public NettyWebStartException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
