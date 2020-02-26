package com.wanghongfei.springboot.starter.nettyweb.network.inject.adapter;

import com.wanghongfei.springboot.starter.nettyweb.network.inject.InjectHeaders;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

/**
 * Created by wanghongfei on 2020/1/22.
 */
public class RequestHeaderVO implements InjectHeaders {
    @JSONField(serialize = false, deserialize = false)
    private Map<String, String> headerMap;

    @Override
    public Map<String, String> headerMap() {
        return headerMap;
    }

    @Override
    public void setHeaderMap(Map<String, String> map) {
        this.headerMap = map;
    }
}
