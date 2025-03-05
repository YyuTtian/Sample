package com.example.demo.exception;

import com.example.demo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //处理异常
    @ExceptionHandler
    public Result ex(Throwable e) { // 方法形参中指定能够处理的异常类型
        // 堆栈保存到日志文件中  在日志中搜索 出现异常了 定位
        log.error("出现异常了", e);
        // 捕获到异常之后，响应一个标准的Result
        return Result.error("出错了 " + e.getMessage());
    }
}
