package cn.wanghongfei.springboot.starter.nettyweb.network.inject.adapter;

import cn.wanghongfei.springboot.starter.nettyweb.network.inject.InjectRequestId;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wanghongfei on 2020/1/9.
 */
public class RequestIdVO implements InjectRequestId {
    @JSONField(serialize = false, deserialize = false)
    private Long reqId;

    @Override
    public Long getRequestId() {
        return this.reqId;
    }

    @Override
    public void setRequestId(Long reqId) {
        this.reqId = reqId;
    }
}
