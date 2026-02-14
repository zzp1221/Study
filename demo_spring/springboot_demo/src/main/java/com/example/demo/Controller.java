package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/calc")
@RestController
public class Controller {

    @RequestMapping("/sum")
    public String sum(Integer a,Integer b){
        if (a==null||b==null){
            return "error";
        }else {
            Integer sum=a+b;
            return "<h1>"+sum+"</h1>";
        }
    }
    @RequestMapping("/ride")
    public String ride(Integer a,Integer b){
        if (a==null||b==null){
            return "error";
        }else{
            Integer ride=a*b;
            return "<h1>"+ride+"</h1>";
        }
    }
}
