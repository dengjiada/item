package com.tianzhou.item.module.exception;

import com.tianzhou.item.module.enums.ResponseCodeEnum;
import lombok.Getter;

/**
 * 自定义业务异常
 */

@Getter
public class BizException extends RuntimeException {
    private final ResponseCodeEnum responseCodeEnum;

    public BizException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getLogMsg());
        this.responseCodeEnum = responseCodeEnum;
    }
}
