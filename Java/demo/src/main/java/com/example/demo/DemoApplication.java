package com.example.demo;

import com.example.demo.test.EnableTestConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@MapperScan("com.example.demo.mapper")
@ServletComponentScan
@EnableTestConfig  // 使用第三方依赖提供的Enable开头的注解
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        log.info("项目启动了");
    }
}
