package com.yb.vhr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")       //处理 /hello 请求
    public String hello(){
        return "hello";
    }
}
