package com.tianzhou.item.module.common;

import com.tianzhou.item.module.enums.ResponseCodeEnum;
import lombok.Data;

@Data
public class Response<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(ResponseCodeEnum.SUCCESS.getCode());
        response.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
        response.setData(data);
        return response;
    }

    public static <T> Response<T> success() {
        return success(null);
    }

    public static <T> Response<T> fail(ResponseCodeEnum responseCodeEnum) {
        Response<T> response = new Response<>();
        response.setCode(responseCodeEnum.getCode());
        response.setMsg(responseCodeEnum.getMsg());
        return response;
    }
}
