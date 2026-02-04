package com.example.captcha_demo;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/captcha")
public class CaptchaTest {
    @Autowired
    CaptchaYml captchaYml;
    static long TIME = 60*1000;
    @RequestMapping("/getCaptcha")
    public void getCaptchaYml(HttpServletResponse response, HttpSession session) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        ShearCaptcha shearCaptcha = new ShearCaptcha(captchaYml.getWidth(), captchaYml.getHeight());
        String code = shearCaptcha.getCode();
        session.setAttribute(captchaYml.getSession().key, code);
        session.setAttribute(captchaYml.getSession().date, new Date());
        shearCaptcha.write(response.getOutputStream());

    }
    @RequestMapping("/check")
    public Boolean check(String captcha,HttpSession session) {
        if (!StringUtils.hasLength(captcha)) {
            return false;
        }

        String code = (String) session.getAttribute(captchaYml.getSession().key);
        Date date = (Date) session.getAttribute(captchaYml.getSession().date);
        if (captcha.equalsIgnoreCase(code)&&System.currentTimeMillis() - date.getTime() < TIME) {
            return true;
        }
        return false;
    }


}
