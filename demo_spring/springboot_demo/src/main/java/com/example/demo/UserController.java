package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/user")//类路径
@RestController
public class UserController {

    @RequestMapping("/m1")//方法路径
    public String m1(){
        return "hello";
    }
}
