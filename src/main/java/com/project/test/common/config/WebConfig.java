package com.project.test.common.config;

import com.project.test.common.interceptor.LevelInterceptor;
import com.project.test.common.security.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    @Autowired
    LevelInterceptor levelInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器（检查 token 是否存在）
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/register",
                    "/auth/login",
                    "/**/register",
                    "/**/login"
                );

        // Level 等级拦截器（检查用户等级）
        registry.addInterceptor(levelInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/register",
                    "/auth/login",
                    "/**/register",
                    "/**/login"
                );
    }

}
