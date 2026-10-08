package com.tianzhou.item.console.config;

import com.tianzhou.item.console.interceptor.ConsoleLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private ConsoleLoginInterceptor consoleLoginInterceptor;
    @Value("${file.upload.root-path}")
    private String rootPath;

    //静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /upload/** 到磁盘目录
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + rootPath + "/upload");
    }

    //注册登录拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(consoleLoginInterceptor)
                .addPathPatterns("/**")//拦截所有请求
                .excludePathPatterns("/user/login", "/upload/**");//放行白名单
    }
}
