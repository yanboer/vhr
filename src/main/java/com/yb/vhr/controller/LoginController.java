package com.yb.vhr.controller;

import com.yb.vhr.model.RespBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

//    @CrossOrigin("*")       //解决跨域问题
    @GetMapping("/login")
    public RespBean login(){
        return RespBean.error("尚未登录，请登录!");
    }
}
