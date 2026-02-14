package com.example.demo;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;

@RequestMapping("/user")
@RestController
public class User {
    @RequestMapping("/login")
    public boolean login(String userName, String password, HttpSession session) {
        if (!StringUtils.isEmpty(userName) || !StringUtils.isEmpty(password)) {
            return false;
        }

        if ("admin".equals(userName) && "123456".equals(password)) {
            session.setAttribute("userName", "admin");
            return true;
        }else {
            return false;
        }
    }
    @RequestMapping("/getLoginUser")
    public String getLoginUser(HttpSession session) {
        return (String) session.getAttribute("userName");
    }
}
