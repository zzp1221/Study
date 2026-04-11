package com.project.test.common.aspect;

import com.project.test.common.annotation.RequireAuthority;
import com.project.test.common.exception.CommonException;
import com.project.test.common.exception.ErrorCode;
import com.project.test.entity.Authority;
import com.project.test.mapper.AuthorityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorityAspect {

    private final AuthorityMapper authorityMapper;

    @Pointcut("@annotation(com.project.test.common.annotation.RequireAuthority)")
    public void authorityPointcut() {
    }

    @Around("authorityPointcut()")
    public Object checkAuthority(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequireAuthority requireAuthority = method.getAnnotation(RequireAuthority.class);
        if (requireAuthority == null) {
            return joinPoint.proceed();
        }

        Long currentUserId = com.project.test.common.util.SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new CommonException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }

        com.project.test.DTO.LoginRequest currentUser = authorityMapper.findByIdWithAuthority(currentUserId.intValue());
        if (currentUser == null) {
            throw new CommonException(ErrorCode.FORBIDDEN, "当前用户不存在");
        }

        Authority currentAuthority;
        try {
            currentAuthority = Authority.fromValue(currentUser.getAuthority().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.FORBIDDEN, "用户权限类型错误");
        }

        Authority[] requiredAuthorities = requireAuthority.value();
        boolean hasPermission = false;

        for (Authority requiredAuthority : requiredAuthorities) {
            if (requireAuthority.hierarchical()) {
                if (currentAuthority.hasPermission(requiredAuthority)) {
                    hasPermission = true;
                    break;
                }
            } else {
                if (currentAuthority == requiredAuthority) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            log.warn("用户 {} 权限不足，当前权限：{}, 需要权限：{}", 
                    currentUser.getUsername(), currentAuthority.getValue(), 
                    java.util.Arrays.toString(requiredAuthorities));
            throw new CommonException(ErrorCode.FORBIDDEN, "权限不足，无法执行此操作");
        }

        return joinPoint.proceed();
    }
}
