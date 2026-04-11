package com.project.test.common.security;

import com.project.test.common.exception.CommonException;
import com.project.test.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String USER_ID_ATTRIBUTE = "userId";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private AuthTokenService authTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/auth/register") || requestURI.contains("/auth/login")) {
            return true;
        }
        
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new CommonException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }

        String token;
        if (authorizationHeader.startsWith(BEARER_PREFIX)) {
            token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        } else {
            token = authorizationHeader.trim();
        }

        Long userId = authTokenService.parseAndValidateUserId(token);
        
        if (userId == null || userId <= 0) {
            throw new CommonException(ErrorCode.UNAUTHORIZED, "无效用户身份");
        }
        
        request.setAttribute(USER_ID_ATTRIBUTE, userId);
        return true;
    }
}
