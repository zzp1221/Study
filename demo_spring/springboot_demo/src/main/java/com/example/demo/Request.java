package com.example.demo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/request")
public class Request {
    @RequestMapping("/r1")
    public String r1(String keyword) {
        return keyword;
    }

    @RequestMapping("/r2")
    public String r2(String keyword,String password) {
        return keyword +" "+ password;
    }

    @RequestMapping("/r3")
    public String r3(int x) {
        return String.valueOf(x);
    }

    @RequestMapping("/r4")
    public String r4(Integer x) {
        return String.valueOf(x);

    }
    @RequestMapping(value = "/r5",method = RequestMethod.POST)
    public String r5(UserInfo userInfo) {
        return userInfo.toString();
    }

    @RequestMapping("/r6")
    public String r6(@RequestParam(value = "q",required = false) String keyword) {
        return keyword;
    }

    @RequestMapping("/r7")
    public String r7(@RequestBody UserInfo userInfo) {
        return userInfo.toString();
    }

    @RequestMapping("/article/{articleID}/{articleName}")
    public String r8(@PathVariable Integer articleID,@PathVariable String articleName) {
        return String.valueOf(articleID)+" "+articleName;
    }

    @RequestMapping("/r9")
    public String r9(MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        //文件上传
        file.transferTo(new File("D:\\测试\\"+file.getOriginalFilename()));
        return "获取成功";
    }
    @RequestMapping("/r10/111")
    public String r10(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName()+":"+cookie.getValue());
            }
        }
        return "返回成功";
    }
    @RequestMapping("/session")
    public String setSession(HttpServletRequest request) {
        //从cookie中获取sessionID
        HttpSession session = request.getSession();
        //默认存在内存中
        session.setAttribute("username","zhangsan");
        session.setAttribute("password","123456");
        return "success";
    }
    @RequestMapping("/getsession")
    public String getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return "登录用户为"+session.getAttribute("username").toString();
        }else {
            return "未登录";
        }
    }
    @RequestMapping("/session2")
    public String getSession(HttpSession session) {
        return (String) session.getAttribute("username");
    }
    @RequestMapping("/session3")
    public String getSession(@SessionAttribute("zhang") String username) {
        return username;
    }

    @RequestMapping("/header")
    public String getHeader(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
