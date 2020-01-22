package cn.wanghongfei.springboot.starter.nettyweb.error;

/**
 * Created by wanghongfei on 2020/1/16.
 */
public class ValidationException extends WebException {
    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, boolean recordStackTrace) {
        super(msg, recordStackTrace);
    }

    public ValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
