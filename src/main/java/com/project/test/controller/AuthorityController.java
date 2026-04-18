package com.project.test.controller;

import com.project.test.DTO.LoginRequest;
import com.project.test.common.exception.CommonException;
import com.project.test.common.result.Result;
import com.project.test.common.util.SecurityUtil;
import com.project.test.mapper.LevelUserMapper;
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
    private final LevelUserMapper levelUserMapper;

    @PostMapping("/level/modify")
    public Result modifyLevel(@RequestParam String username,
                              @RequestParam Integer level) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new CommonException("用户名不能为空");
            }
            if (level == null || level <= 0) {
                throw new CommonException("等级必须为正整数");
            }

            LoginRequest targetUser = levelUserMapper.findByUsername(username);
            if (targetUser == null) {
                throw new CommonException("要修改的用户不存在");
            }

            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId == null) {
                throw new CommonException("未登录或登录已过期");
            }

            LoginRequest currentUser = levelUserMapper.findById(currentUserId.intValue());
            if (currentUser == null) {
                throw new CommonException("当前用户不存在");
            }

            // level 1 用户可以修改任何用户（超级管理员特权）
            if (currentUser.getLevel() != 1) {
                // 其他等级不能修改 level 1 用户
                if (targetUser.getLevel() == 1) {
                    throw new CommonException("无法修改超级管理员");
                }
            }

            int result = levelUserMapper.updateLevelById(targetUser.getId(), level);
            if (result <= 0) {
                throw new CommonException("修改等级失败");
            }

            log.info("用户 {} 的等级被修改为 {}", username, level);
            return Result.success("等级修改成功", null);
        } catch (CommonException exception) {
            log.error("修改等级失败：{}", exception.getMessage());
            return Result.error(exception.getMessage());
        } catch (Exception exception) {
            log.error("修改等级失败，未知错误：{}", exception.getMessage());
            return Result.error("修改等级失败：" + exception.getMessage());
        }
    }

    @PostMapping("/password/modify")
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

            LoginRequest targetUser = levelUserMapper.findByUsername(username);
            if (targetUser == null) {
                throw new CommonException("要修改的用户不存在");
            }

            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId == null) {
                throw new CommonException("未登录或登录已过期");
            }

            LoginRequest currentUser = levelUserMapper.findById(currentUserId.intValue());
            if (currentUser == null) {
                throw new CommonException("当前用户不存在");
            }

            // level 1 用户可以修改任何用户的密码（超级管理员特权）
            if (currentUser.getLevel() != 1) {
                // 其他等级不能修改 level 1 用户的密码
                if (targetUser.getLevel() == 1) {
                    throw new CommonException("无法修改超级管理员的密码");
                }
            }

            String encryptedPassword = SecurityUtil.encrypt(password);
            
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
