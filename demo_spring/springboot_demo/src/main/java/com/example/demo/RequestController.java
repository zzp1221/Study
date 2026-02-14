package com.example.demo;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/res")
@Controller
public class RequestController {
    @RequestMapping("/r1")
    public String web(){
        return "/test.html";
    }

    @ResponseBody
    @RequestMapping("/r2")
    public String method(){
        return "SSS";
    }

    @ResponseBody
    @RequestMapping("/3")
    public UserInfo methodJson(HttpServletResponse response){
        response.setStatus(200);
        UserInfo userInfo = new UserInfo("aa",1,"1");
        return userInfo;
    }

    @ResponseBody
    @RequestMapping("/r4")
    public String setHeader(HttpServletResponse response){
        response.setHeader("aa","1");
        return "111";
    }


}
