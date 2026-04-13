package com.project.test.common.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 密码加密工具 - 用于生成初始密码
 */
public class PasswordEncryptor {
    
    public static void main(String[] args) {
        String password = "123456";
        
        // 生成一个固定的 salt（为了可重复性，使用固定的 salt）
        String salt = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6";
        
        // 加密
        String encryptedPassword = salt + DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
        
        System.out.println("原始密码：" + password);
        System.out.println("Salt: " + salt);
        System.out.println("加密后的密码：" + encryptedPassword);
        System.out.println("总长度：" + encryptedPassword.length());
        
        // 验证
        String storedHash = encryptedPassword.substring(32);
        String inputPassword = "123456";
        String verifyHash = DigestUtils.md5DigestAsHex((inputPassword + salt).getBytes(StandardCharsets.UTF_8));
        
        System.out.println("\n验证：");
        System.out.println("存储的 hash: " + storedHash);
        System.out.println("输入的 hash: " + verifyHash);
        System.out.println("验证结果：" + storedHash.equals(verifyHash));
    }
}
