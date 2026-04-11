package com.project.test.controller;

import com.project.test.DTO.LoginRequest;
import com.project.test.common.annotation.RequireAuthority;
import com.project.test.common.exception.CommonException;
import com.project.test.common.result.Result;
import com.project.test.common.util.SecurityUtil;
import com.project.test.entity.Authority;
import com.project.test.mapper.AuthorityMapper;
import com.project.test.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthorityController {
    private final UserMapper userMapper;
    private final AuthorityMapper authorityMapper;

    @RequireAuthority({Authority.ADMIN})
    @PostMapping("/authority/modify")
    public Result modifyAuthority(@RequestParam String username,
                                  @RequestParam String authority) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new CommonException("用户名不能为空");
            }
            if (authority == null || authority.trim().isEmpty()) {
                throw new CommonException("权限不能为空");
            }

            Authority targetAuthority;
            try {
                targetAuthority = Authority.fromValue(authority.trim().toLowerCase());
            } catch (IllegalArgumentException e) {
                throw new CommonException("无效的权限类型，只能是 user、teacher 或 admin");
            }

            LoginRequest targetUser = authorityMapper.findByUsernameWithAuthority(username);
            if (targetUser == null) {
                throw new CommonException("要修改的用户不存在");
            }

            if ("admin".equalsIgnoreCase(targetUser.getAuthority())) {
                throw new CommonException("无法修改 admin 用户的权限");
            }

            int result = authorityMapper.updateAuthorityByUsername(username, targetAuthority.getValue());
            if (result <= 0) {
                throw new CommonException("修改权限失败");
            }

            log.info("用户 {} 的权限被修改为 {}", username, targetAuthority.getValue());
            return Result.success("权限修改成功", null);
        } catch (CommonException exception) {
            log.error("修改权限失败：{}", exception.getMessage());
            return Result.error(exception.getMessage());
        } catch (Exception exception) {
            log.error("修改权限失败，未知错误：{}", exception.getMessage());
            return Result.error("修改权限失败：" + exception.getMessage());
        }
    }

    @RequireAuthority({Authority.TEACHER, Authority.ADMIN})
    @PostMapping("/authority/modifyPS")
    public Result modifyPassword(@RequestParam String username,
                                 @RequestParam String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new CommonException("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new CommonException("密码不能为空");
            }

            if (password.length() < 6) {
                throw new CommonException("密码长度不能少于 6 位");
            }

            LoginRequest targetUser = authorityMapper.findByUsernameWithAuthority(username);
            if (targetUser == null) {
                throw new CommonException("要修改的用户不存在");
            }

            String targetAuthority = targetUser.getAuthority();

            if ("admin".equalsIgnoreCase(targetAuthority)) {
                throw new CommonException("无法修改 admin 用户的密码");
            }

            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId == null) {
                throw new CommonException("未登录或登录已过期");
            }

            LoginRequest currentUser = authorityMapper.findByIdWithAuthority(currentUserId.intValue());
            if (currentUser == null) {
                throw new CommonException("当前用户不存在");
            }

            if ("teacher".equalsIgnoreCase(currentUser.getAuthority()) && "teacher".equalsIgnoreCase(targetAuthority)) {
                throw new CommonException("teacher 用户只能修改 user 用户的密码");
            }

            String encryptedPassword = SecurityUtil.encrypt(password);
            int result = authorityMapper.updatePasswordByUsername(username, encryptedPassword);
            if (result <= 0) {
                throw new CommonException("修改密码失败");
            }

            log.info("用户 {} 的密码修改成功", username);
            return Result.success("密码修改成功", null);
        } catch (CommonException exception) {
            log.error("修改密码失败：{}", exception.getMessage());
            return Result.error(exception.getMessage());
        } catch (Exception exception) {
            log.error("修改密码失败，未知错误：{}", exception.getMessage());
            return Result.error("修改密码失败：" + exception.getMessage());
        }
    }
}
