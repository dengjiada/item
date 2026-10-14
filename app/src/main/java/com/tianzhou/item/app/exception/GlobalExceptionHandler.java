package com.tianzhou.item.app.exception;

import com.tianzhou.item.module.common.Response;
import com.tianzhou.item.module.enums.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //兜底---捕获所有未处理的异常
    @ExceptionHandler(Exception.class)
    public Response<String> handleAllException(Exception e) {
        log.error("global unknown exception", e);
        return Response.fail(ResponseCodeEnum.NETWORK_BUSY);
    }
}
