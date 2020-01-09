package cn.fh.springboot.starter.nettyweb.network.inject;

/**
 * Created by wanghongfei on 2020/1/9.
 */
public interface InjectRequestId {
    Long getRequestId();
    void setRequestId(Long reqId);
}
