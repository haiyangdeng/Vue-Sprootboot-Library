package com.library.interceptor;

import com.library.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 获取请求头中的 token
        String token = request.getHeader("Authorization");

        // 2. 如果没有 token，直接放行，交给 Spring Security 后续的权限校验
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 处理前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 4. 验证 token 是否有效
            if (!jwtUtil.validateToken(token)) {
                // 如果是需要授权的接口，但 token 校验失败
                // 这里我们选择直接放行，但不存入 SecurityContextHolder
                // 这样如果是登录接口(permitAll)，可以正常访问；如果是受保护接口，Security 会拦截并报 403/401
                filterChain.doFilter(request, response);
                return;
            }

            // 5. 解析 token
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.valueOf(claims.get("userId").toString());
            String username = claims.get("username").toString();
            String role = claims.get("role").toString();

            // 6. 构建权限并存入 SecurityContextHolder
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 7. 存入 request 方便业务使用
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("role", role);

        } catch (Exception e) {
            log.error("JWT 解析异常: {}", e.getMessage());
            // 异常情况下同样选择放行，由 Security 决定最终命运
        }

        filterChain.doFilter(request, response);
    }
}