package com.exam.exam_web.config;

import com.exam.exam_web.config.interceptor.SidebarInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SidebarInterceptor sidebarInterceptor;

    public WebConfig(SidebarInterceptor sidebarInterceptor) {
        this.sidebarInterceptor = sidebarInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sidebarInterceptor)
                .addPathPatterns("/**");
    }
}