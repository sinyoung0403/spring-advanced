package org.example.expert.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class LoggerInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 로깅만 남기는 건데? 서비스 딴에서 하는 request 검증을 여기서 하라는 거 !

        String role = (String) request.getAttribute("userRole");

        // 관리자 권한이 없는 경우 403을 반환합니다.
        if (!UserRole.ADMIN.equals(UserRole.of(role))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
            return false;
        }

        log.info("Request URL: {} | Time : {}", request.getRequestURI(), LocalDateTime.now());
        return true;
    }
}
