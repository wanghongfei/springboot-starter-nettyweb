package cn.wanghongfei.springboot.starter.nettyweb.network.inject.adapter;

import cn.wanghongfei.springboot.starter.nettyweb.network.inject.InjectLoginToken;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wanghongfei on 2020/1/9.
 */
public class RequestIdTokenVO extends RequestIdVO implements InjectLoginToken {
    @JSONField(serialize = false, deserialize = false)
    private String loginToken;

    @Override
    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    @Override
    public String getLoginToken() {
        return loginToken;
    }
}
