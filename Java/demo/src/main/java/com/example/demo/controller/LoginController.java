package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.LoginDTO;
import com.example.demo.util.JwtUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", loginDTO.getUsername());
        map.put("password", loginDTO.getPassword());
        return Result.success(JwtUtils.generateJwt(map));
    }

    @GetMapping("/login2")
    public Result login2() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "aaa");
        map.put("password", "vvv");
        return Result.success(JwtUtils.generateJwt(map));
    }
}
