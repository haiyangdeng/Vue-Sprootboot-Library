package com.library.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.annotation.Log;
import com.library.entity.SysLog;
import com.library.service.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(com.library.common.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行目标方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        // 执行完成后异步或同步保存日志
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog sysLog = new SysLog();

        // 1. 获取注解上的描述
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            sysLog.setOperation(logAnnotation.value());
        }

        // 2. 获取类名和方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        // 3. 获取并序列化请求参数
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            try {
                // 通常只记录第一个参数（如 DTO 或 Entity）
                String params = objectMapper.writeValueAsString(args[0]);
                sysLog.setParams(params);
            } catch (Exception e) {
                sysLog.setParams("参数序列化失败");
            }
        }

        // 4. 获取 Request 环境
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 设置 IP
            sysLog.setIp(request.getRemoteAddr());

            // --- 获取操作人核心逻辑 ---
            // 优先从 Header 获取（方案C），如果没有，再从 Attribute 拿（方案B）
            String username = request.getHeader("Log-User");
            if (StrUtil.isBlank(username)) {
                // 这里假设你的拦截器中存入的名字叫 "username"
                username = (String) request.getAttribute("username");
            }

            sysLog.setUsername(StrUtil.isNotBlank(username) ? username : "匿名用户");
        }

        sysLog.setTime(time);
        sysLog.setCreateDate(new Date());

        // 5. 保存到数据库
        sysLogService.save(sysLog);
    }
}