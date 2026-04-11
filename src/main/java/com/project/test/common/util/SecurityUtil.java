package com.project.test.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SecurityUtil {
    public static String encrypt(String password) {
        String salt = UUID.randomUUID().toString().replace("-", "");
        String securityPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
        return salt+securityPassword;
    }

    public static boolean verify(String inputPassword, String sqlPassword) {
        if(!StringUtils.hasLength(inputPassword)){
            return false;
        }
        if (sqlPassword==null || sqlPassword.length()!=64){
            return false;
        }
        String salt = sqlPassword.substring(0,32);
        String securityPassword = DigestUtils.md5DigestAsHex((inputPassword + salt).getBytes(StandardCharsets.UTF_8));
        String storedHash = sqlPassword.substring(32);
        return securityPassword.equals(storedHash);
    }
    
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return null;
        }
        try {
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                return Long.parseLong((String) userIdObj);
            } else {
                return Long.parseLong(userIdObj.toString());
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
