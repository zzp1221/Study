package com.example.book_demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/user")
public class UserInfo {
    @RequestMapping("/login")
    public boolean login(String username, String password, HttpSession session) {
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return false;
        }
        if ("admin".equals(username) && "123456".equals(password)) {
            session.setAttribute("username", username);

            return true;
        }
        return false;
    }
}
