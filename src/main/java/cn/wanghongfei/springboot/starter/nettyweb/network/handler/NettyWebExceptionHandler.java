package cn.wanghongfei.springboot.starter.nettyweb.network.handler;

import cn.wanghongfei.springboot.starter.nettyweb.error.WebException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wanghongfei on 2020/1/15.
 */
public class NettyWebExceptionHandler {
    public CodeAndMessage handleException(Throwable cause) {
        CodeAndMessage codeAndMessage = new CodeAndMessage();

        if (cause instanceof WebException) {
            // 业务异常
            codeAndMessage.setCode(400);
            codeAndMessage.setMessage(cause.getMessage());

        } else {
            codeAndMessage.setCode(500);
            codeAndMessage.setMessage("internal error");
        }

        return codeAndMessage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeAndMessage {
        private int code;
        private String message;
    }
}
