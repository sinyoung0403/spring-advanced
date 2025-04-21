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
public class AdminInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(AdminInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String role = (String) request.getAttribute("userRole");

        // role 이 비어있는 경우도 있을 경우 401 에러 반환합니다. Unauthorized
        if (role == null || role.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User role is missing.");
            return false;
        }

        // 관리자 권한이 없는 경우 403을 반환합니다.
        if (!UserRole.ADMIN.equals(UserRole.of(role))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
            return false;
        }

        log.info("Request URL: {} | Time : {}", request.getRequestURI(), LocalDateTime.now());
        return true;
    }

    public void setLogger(Logger logger) {
        log = logger;
    }
}
