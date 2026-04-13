package com.project.test.common.aspect;

import com.project.test.common.annotation.RequireLevel;
import com.project.test.common.exception.CommonException;
import com.project.test.common.exception.ErrorCode;
import com.project.test.DTO.LoginRequest;
import com.project.test.mapper.LevelUserMapper;
import com.project.test.mapper.InterfaceLevelMapper;
import com.project.test.entity.InterfaceLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LevelPermissionAspect {

    private final LevelUserMapper levelUserMapper;
    private final InterfaceLevelMapper interfaceLevelMapper;

    @Pointcut("@annotation(com.project.test.common.annotation.RequireLevel)")
    public void levelPointcut() {
    }

    @Around("levelPointcut()")
    public Object checkLevel(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequireLevel requireLevel = method.getAnnotation(RequireLevel.class);
        if (requireLevel == null) {
            return joinPoint.proceed();
        }

        Long currentUserId = com.project.test.common.util.SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new CommonException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }

        LoginRequest currentUser = levelUserMapper.findById(currentUserId.intValue());
        if (currentUser == null) {
            throw new CommonException(ErrorCode.FORBIDDEN, "当前用户不存在");
        }

        Integer userLevel = currentUser.getLevel();
        if (userLevel == null) {
            throw new CommonException(ErrorCode.FORBIDDEN, "用户等级未设置");
        }

        int requiredLevel = requireLevel.value();
        
        if (userLevel > requiredLevel) {
            log.warn("用户 {} 等级不足，当前等级：{}, 需要等级：{}", 
                    currentUser.getUsername(), userLevel, requiredLevel);
            throw new CommonException(ErrorCode.FORBIDDEN, 
                    String.format("等级不足，当前等级%d，需要等级%d", userLevel, requiredLevel));
        }

        return joinPoint.proceed();
    }
}
