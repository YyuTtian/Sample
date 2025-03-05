package com.example.demo.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    public Test1 test1() {
        return new Test1();
    }

    @Bean
    public Test2 test2() {
        return new Test2();
    }
}
