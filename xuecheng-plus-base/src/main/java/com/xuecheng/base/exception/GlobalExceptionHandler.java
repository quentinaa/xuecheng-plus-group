package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/4 11:39:04
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e){
        //记录异常
        log.error("异常信息{}",e.getErrMessage(),e);
        //解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e){
        //记录异常
        log.error("异常信息{}",e.getMessage(),e);
        if (e.getMessage().equals("不允许访问")){
            return new RestErrorResponse("您没有权限操作此功能");
        }
        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> msgList = new ArrayList<>();
        //将错误信息放在msgList
        bindingResult.getFieldErrors().stream().forEach(item->msgList.add(item.getDefaultMessage()));
        //拼接错误信息
        String msg = StringUtils.join(msgList, ",");
        log.error("【系统异常】{}",msg);
        return new RestErrorResponse(msg);
    }
}
