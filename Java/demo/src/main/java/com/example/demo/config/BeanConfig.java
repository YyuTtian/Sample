package com.example.demo.config;

import org.dom4j.io.SAXReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 配置类  (在配置类当中对第三方bean进行集中的配置管理)
public class BeanConfig {

    // 声明第三方bean
    // 将当前方法的返回值对象交给IOC容器管理, 成为IOC容器bean
    // 通过@Bean注解的name/value属性指定bean名称, 如果未指定, 默认是方法名
    // 这个方法的参数也会自动交给IOC容器管理
    @Bean
    public SAXReader reader() {
        return new SAXReader();
    }
}
