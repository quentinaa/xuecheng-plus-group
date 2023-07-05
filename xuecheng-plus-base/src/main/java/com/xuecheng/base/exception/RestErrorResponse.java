package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * @author xiong
 * @version 1.0
 * @description 错误响应参数的包装
 * @date 2023/7/4 11:34:08
 */
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
