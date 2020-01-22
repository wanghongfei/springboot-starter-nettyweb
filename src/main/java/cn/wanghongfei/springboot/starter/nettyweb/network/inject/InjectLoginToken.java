package cn.wanghongfei.springboot.starter.nettyweb.network.inject;

/**
 * Created by wanghongfei on 2019/12/20.
 */
public interface InjectLoginToken {
    void setLoginToken(String loginToken);
    String getLoginToken();
}
