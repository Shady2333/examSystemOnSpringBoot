package com.exam.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 系统身份验证拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
//        System.out.println("进入拦截器");
//        String uri = request.getRequestURI();
//        if(request.getSession().getAttribute("user")==null)
//        {
//            //response.sendRedirect("/toLogin");   //这句话不知道为什么不生效,按道理会跳到登录页面,我知道了，是因为ajax的原因
//            //胡文涛测试
//            return false;
//        }else if(uri.startsWith("/teacher")){
//            if(request.getSession().getAttribute("user").equals("teacher")){
//                return true;
//            }
//            response.sendRedirect("/");
//            return false;
//
//        }else if(uri.startsWith("/student")){
//            if(request.getSession().getAttribute("user").equals("student")){
//                return true;
//            }
//            response.sendRedirect("/");
//            return false;
//        }
//
        if(request.getSession().getAttribute("id")==null){
            response.sendRedirect("/");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
