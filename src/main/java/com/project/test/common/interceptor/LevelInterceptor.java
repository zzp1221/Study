package com.project.test.common.interceptor;

import com.project.test.DTO.LoginRequest;
import com.project.test.common.exception.CommonException;
import com.project.test.common.exception.ErrorCode;
import com.project.test.common.util.SecurityUtil;
import com.project.test.entity.InterfaceLevel;
import com.project.test.mapper.InterfaceLevelMapper;
import com.project.test.mapper.LevelUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LevelInterceptor implements HandlerInterceptor {

    private final InterfaceLevelMapper interfaceLevelMapper;
    private final LevelUserMapper levelUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径和方法
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.debug("拦截请求：{} {}", method, requestURI);

        // 获取当前用户 ID
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 如果没有登录，直接拒绝（登录和注册接口除外）
        if (currentUserId == null) {
            if (isPublicInterface(requestURI, method)) {
                return true;
            }
            throw new CommonException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }

        // 查询当前用户的等级
        LoginRequest currentUser = levelUserMapper.findById(currentUserId.intValue());
        if (currentUser == null) {
            throw new CommonException(ErrorCode.FORBIDDEN, "当前用户不存在");
        }

        Integer userLevel = currentUser.getLevel();
        if (userLevel == null) {
            throw new CommonException(ErrorCode.FORBIDDEN, "用户等级未设置");
        }

        log.debug("用户 {} 的等级为：{}", currentUser.getUsername(), userLevel);

        // 查询接口需要的等级
        InterfaceLevel interfaceLevel = findInterfaceLevel(requestURI, method);
        
        // 如果没有找到接口配置，允许访问（默认开放）
        if (interfaceLevel == null) {
            log.debug("未找到接口 {} {} 的等级配置，允许访问", method, requestURI);
            return true;
        }

        Integer requiredLevel = interfaceLevel.getRequiredLevel();
        if (requiredLevel == null) {
            log.warn("接口 {} {} 的等级配置为空，允许访问", method, requestURI);
            return true;
        }

        log.debug("接口 {} {} 需要的等级为：{}", method, requestURI, requiredLevel);

        // 权限验证：用户等级 <= 接口要求等级
        if (userLevel > requiredLevel) {
            log.warn("用户 {} 等级不足，当前等级：{}, 需要等级：{}, 接口：{} {}", 
                    currentUser.getUsername(), userLevel, requiredLevel, method, requestURI);
            throw new CommonException(ErrorCode.FORBIDDEN, 
                    String.format("等级不足，当前等级%d，需要等级%d", userLevel, requiredLevel));
        }

        log.debug("用户 {} 权限验证通过", currentUser.getUsername());
        return true;
    }

    /**
     * 判断是否是公开接口（不需要登录）
     */
    private boolean isPublicInterface(String requestURI, String method) {
        // 登录、注册、CORS 预检请求等公开接口
        return requestURI.equals("/auth/login") ||
               requestURI.equals("/auth/register") ||
               "OPTIONS".equals(method);
    }

    /**
     * 查找接口的等级配置
     * 支持精确匹配和通配符匹配
     */
    private InterfaceLevel findInterfaceLevel(String requestURI, String method) {
        // 1. 先尝试精确匹配
        InterfaceLevel exactMatch = interfaceLevelMapper.findByPathAndMethod(requestURI, method);
        if (exactMatch != null) {
            log.debug("精确匹配接口配置：{} {}", method, requestURI);
            return exactMatch;
        }

        // 2. 尝试通配符匹配
        // 获取所有该方法的接口配置
        java.util.List<InterfaceLevel> allLevels = interfaceLevelMapper.findAll();
        
        for (InterfaceLevel level : allLevels) {
            if (!level.getInterfaceMethod().equals(method)) {
                continue;
            }
            
            String pattern = level.getInterfacePath();
            if (matchPath(pattern, requestURI)) {
                log.debug("通配符匹配接口配置：{} {} -> {}", method, requestURI, pattern);
                return level;
            }
        }

        return null;
    }

    /**
     * 路径匹配（支持 ** 通配符）
     */
    private boolean matchPath(String pattern, String path) {
        if (pattern.equals(path)) {
            return true;
        }
        
        // 处理 ** 通配符
        if (pattern.contains("/**")) {
            String prefix = pattern.substring(0, pattern.indexOf("/**"));
            return path.startsWith(prefix + "/") || path.equals(prefix);
        }
        
        // 处理 * 通配符（简单匹配）
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return path.matches(regex);
        }
        
        return false;
    }
}
