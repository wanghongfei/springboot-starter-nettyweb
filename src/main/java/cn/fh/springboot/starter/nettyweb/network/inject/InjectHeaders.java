package cn.fh.springboot.starter.nettyweb.network.inject;

import java.util.Map;

/**
 * Created by wanghongfei on 2020/1/7.
 */
public interface InjectHeaders {
    Map<String, String> headerMap();
    void setHeaderMap(Map<String, String> map);
}
