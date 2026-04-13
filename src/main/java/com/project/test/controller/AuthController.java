package com.project.test.controller;

import com.project.test.DTO.LoginRequest;
import com.project.test.DTO.RegisterUser;
import com.project.test.common.exception.CommonException;
import com.project.test.common.result.Result;
import com.project.test.common.security.AuthTokenService;
import com.project.test.common.util.SecurityUtil;
import com.project.test.mapper.LevelUserMapper;
import com.project.test.mapper.RegisterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterMapper registerMapper;
    private final LevelUserMapper userMapper;
    private final AuthTokenService authTokenService;

    @PostMapping("/register")
    public Result register(@RequestParam String username, 
                           @RequestParam String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new CommonException("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new CommonException("密码不能为空");
            }
            
            LoginRequest existingUser = userMapper.findByUsername(username);
            if (existingUser != null) {
                throw new CommonException("用户名已存在，请使用其他用户名");
            }
            
            RegisterUser registerUser = new RegisterUser();
            registerUser.setUsername(username);
            registerUser.setPassword(SecurityUtil.encrypt(password));
            int result = registerMapper.insert(registerUser);
            if (result <= 0) {
                throw new CommonException("注册失败，用户信息插入失败");
            }
            registerUser.setPassword("");
            log.info("注册成功：{}", registerUser.toString());
            return Result.success(null);
        } catch (CommonException exception) {
            log.error("注册失败：{}", exception.getMessage());
            return Result.error(exception.getMessage());
        } catch (Exception exception) {
            log.error("注册失败，未知错误：{}", exception.getMessage());
            return Result.error("注册失败：" + exception.getMessage());
        }
    }

    @PostMapping("/login")
    public Result login(@RequestParam String username, 
                        @RequestParam String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new CommonException("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new CommonException("密码不能为空");
            }
            
            LoginRequest loginUser = userMapper.findByUsername(username);
            if (loginUser == null) {
                throw new CommonException("用户名或密码错误");
            }
            
            if (!SecurityUtil.verify(password, loginUser.getPassword())) {
                throw new CommonException("用户名或密码错误");
            }
            
            String token = authTokenService.generateToken((long)loginUser.getId());
            
            log.info("登录成功：{}", username);
            
            return Result.success("SUCCESS", token);
        } catch (CommonException exception) {
            log.error("登录失败：{}", exception.getMessage());
            return Result.error(exception.getMessage());
        } catch (Exception exception) {
            log.error("登录失败，未知错误：{}", exception.getMessage());
            return Result.error("登录失败：" + exception.getMessage());
        }
    }
    

    

}
