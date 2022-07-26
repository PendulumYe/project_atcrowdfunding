package com.atguigu.crowd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrowdWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 跳转至会员注册页面
        registry.addViewController("/auth/member/to/reg/page").setViewName("/member-reg");
        // 跳转至登录页面
        registry.addViewController("/auth/member/to/login/page").setViewName("member-login");
        // 跳转个人主页
        registry.addViewController("/auth/member/to/center/page").setViewName("member-center");
        // 跳转筹集页面
        registry.addViewController("/member/my/crowd").setViewName("member-crowd");
    }
}
