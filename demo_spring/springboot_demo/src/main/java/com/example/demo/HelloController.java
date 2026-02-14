package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    //@RequestMapping对路径的映射
    //即支持get,又支持post
    public String hello() {
        return "hello";
    }

    @RequestMapping(value = "/m2",method = RequestMethod.GET)
    public String hello2() {
        return "hello2";
    }
}
