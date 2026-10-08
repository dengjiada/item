package com.tianzhou.item.console.controller;

import com.tianzhou.item.module.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/user/login")
    public String login(@RequestParam(value = "phone") String phone,
                        @RequestParam(value = "password") String password,
                        HttpServletResponse response) {
        try {
            String jwt = userService.consoleLogin(phone, password);

            //将jwt写入cookie中
            Cookie consoleToken = new Cookie("console_token", jwt);
            consoleToken.setPath("/");
            consoleToken.setHttpOnly(true);
            consoleToken.setMaxAge(2 * 3600);

            response.addCookie(consoleToken);
            return "登录成功";
        } catch (Exception e) {
            log.error("an error occurred", e);
            return "登录失败";
        }
    }
}