package com.example.demoaop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/t1")
    public String t1(){
        return "t1";
    }

    @RequestMapping("/t2")
    public Integer t2(){
        return 1;
    }

    @RequestMapping("/t3")
    public Boolean t3(){
        return true;
    }
}
