package com.library.config;

import com.library.interceptor.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭csrf
                .csrf().disable()
                // 关闭session（使用JWT）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 允许跨域
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 显式放行所有 OPTIONS
                // 放行登录和注册接口
                .antMatchers("/user/login", "/user/register").permitAll()
                // 其他接口需要认证
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                // 认证失败处理器：专门处理匿名用户访问受保护资源（即 401 状态）
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("认证失败: {}", authException.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 明确设置 401
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"msg\":\"Token无效或已过期，请重新登录\",\"data\":null}");
                })
                // 权限不足处理器：处理已登录但角色不对的情况（即 403 状态）
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("权限不足: {}", accessDeniedException.getMessage());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 明确设置 403
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"msg\":\"权限不足，无法访问\",\"data\":null}");
                })
                .and()
                // 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}