package com.example.demo.config;

import com.example.demo.interceptor.DemoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DemoInterceptor demoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义拦截器对象
        // 多个拦截器的执行顺序取决于registry.addInterceptor的顺序
        registry.addInterceptor(demoInterceptor)
                // 设置拦截器拦截的请求路径（ /** 表示拦截所有请求）
                // /*	 一级路径	    能匹配/depts，/emps，/login，不能匹配 /depts/1
                // /**	 任意级路径	能匹配/depts，/depts/1，/depts/1/2
                // /depts/*	    /depts下的一级路径	能匹配/depts/1，不能匹配/depts/1/2，/depts
                // /depts/**	/depts下的任意级路径	能匹配/depts，/depts/1，/depts/1/2，不能匹配/emps/1
                .addPathPatterns("/**")
                // 设置不拦截的请求路径
                .excludePathPatterns("/login");
    }
}
