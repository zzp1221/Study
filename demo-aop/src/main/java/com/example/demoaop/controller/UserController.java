package com.example.demoaop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/u1")
    public String t1(){
        return "t1";
    }

    @RequestMapping("/u2")
    public Integer t2(){
        return 1;
    }
}
