package cn.fh.springboot.starter.nettyweb.error;

/**
 * Created by wanghongfei on 2019/12/29.
 */
public class ReadableException extends BizException {
    public ReadableException(String msg) {
        super(msg);
    }

    public ReadableException(String msg, boolean recordStackTrace) {
        super(msg, recordStackTrace);
    }

    public ReadableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
