package com.tianzhou.item.console.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //兜底---捕获所有未处理的异常
    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception e) {
        log.error("global unknown exception", e);
        return "网络繁忙";
    }
}
