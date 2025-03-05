package com.example.demo.interceptor;

import com.example.demo.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class DemoInterceptor implements HandlerInterceptor {
    // 目标资源方法执行前执行。 返回true：放行    返回false：不放行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 获取请求url。
        String url = request.getRequestURL().toString();

        // 2. 判断请求url中是否包含login，如果包含，说明是登录操作，放行。
        if (url.contains("login")) { //登录请求
            log.info("登录请求 , 直接放行");
            return true;
        }

        // 3. 获取请求头中的令牌（token）。
        String jwt = request.getHeader("token");

        // 4. 判断令牌是否存在，如果不存在，返回错误结果（未登录）。
        if (!StringUtils.hasLength(jwt)) { //jwt为空
            log.info("获取到jwt令牌为空, 返回错误结果");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // 5. 解析token，如果解析失败，返回错误结果（未登录）。
        try {
            JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            log.info("解析令牌失败, 返回错误结果");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // 6. 放行。
        log.info("令牌合法, 放行");
        return true; // true表示放行
    }

    // 目标资源方法执行后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    // 视图渲染完毕后执行，最后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
