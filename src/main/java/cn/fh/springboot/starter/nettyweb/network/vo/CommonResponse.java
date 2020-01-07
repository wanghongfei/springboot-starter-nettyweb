package cn.fh.springboot.starter.nettyweb.network.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wanghongfei on 2019/11/4.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private int code;

    private String message;

    private Object data;
}
