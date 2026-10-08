package com.tianzhou.item.app.controller;

import com.tianzhou.item.module.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author test
 * @since 2026-10-06
 */
@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    //注册
    @RequestMapping("/user/register")
    public String register(@RequestParam(value = "phone") String phone,
                           @RequestParam(value = "password") String password) {
        String sign = null;
        try {
            sign = userService.register(phone, password);
        } catch (Exception e) {
            log.error("an error occurred", e);
            return "注册失败";
        }
        return sign;
    }

    //登录
    @RequestMapping("/user/login")
    public String login(@RequestParam(value = "phone") String phone,
                        @RequestParam(value = "password") String password) {

        String sign = null;
        try {
            sign = userService.appLogin(phone, password);
        } catch (Exception e) {
            log.error("an error occurred", e);
            return "登录失败";
        }
        return sign;
    }
}