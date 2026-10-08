package com.tianzhou.item.console.controller;

import com.tianzhou.item.module.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserController {
    @Autowired
    private UserService userService;


}