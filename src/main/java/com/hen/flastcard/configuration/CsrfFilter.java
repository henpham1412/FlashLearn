package com.hen.flastcard.configuration;

import com.hen.flastcard.dto.response.ApiResponse;
import com.hen.flastcard.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Arrays;

@Component
public class CsrfFilter extends OncePerRequestFilter {
    private static final String CSRF_COOKIE = "XSRF-TOKEN";
    private static final String CSRF_HEADER = "X-XSRF-TOKEN";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        boolean csrfProtectedEndpoint = path.endsWith("/auth/refresh")
                || path.endsWith("/auth/logout");
        boolean stateChangingMethod = switch (request.getMethod()) {
            case "POST", "PUT", "PATCH", "DELETE" -> true;
            default -> false;
        };
        if (csrfProtectedEndpoint && stateChangingMethod) {
            String csrfCookie = getCsrfCookie(request);
            String csrfHeader = request.getHeader(CSRF_HEADER);

            if (csrfCookie == null || csrfHeader == null || !csrfCookie.equals(csrfHeader)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ErrorCode errorCode = ErrorCode.CSRF_TOKEN_INVALID;
                ApiResponse<?> apiResponse = ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build();

                ObjectMapper objectMapper = JsonMapper.builder().build();

                response.getWriter().write(
                        objectMapper.writeValueAsString(apiResponse)
                );

                response.flushBuffer();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    
    private String getCsrfCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> CSRF_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }
}
