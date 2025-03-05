package com.example.demo.controller;


import com.example.demo.service.impl.TbUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2025-03-02
 */
@RestController
@RequestMapping("/tb-user")
public class TbUserController {

    @Value("${my-data.db-name}")
    private String dbName;


    @PostMapping("/upload")
    public void upload(MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            file.transferTo(new File("D:/images/" + newFileName));
        }
    }

    @Autowired
    TbUserServiceImpl tbUserService;

    @GetMapping("/test")
    public void test() {
        System.out.println("进入了test");
        tbUserService.test();
    }
}
