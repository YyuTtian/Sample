package com.example.demo.test;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelector implements ImportSelector {
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 返回值字符串数组（数组中封装了全限定名称的类）
        return new String[]{"com.example.demo.test.TestConfig"};
    }
}