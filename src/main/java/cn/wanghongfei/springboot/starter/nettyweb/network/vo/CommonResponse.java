package cn.wanghongfei.springboot.starter.nettyweb.network.vo;

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
    private int code = CODE_OK;

    private String message;

    private Object data;

    public static transient final int CODE_OK = 0;
    public static transient final String MESSAGE_OK = "ok";
}
