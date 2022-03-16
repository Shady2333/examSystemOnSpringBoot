package com.exam.config;

import com.exam.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/7/18  测试git
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("login");
        //registry.addViewController("/toExams").setViewName("teacher/exam/list");
        //registry.addViewController("/toStudentExams").setViewName("student/exam/list");
        //registry.addViewController("/toAddExam").setViewName("teacher/exam/add");
        //registry.addViewController("/test").setViewName("student/exam/join");
        //registry.addViewController("/toStudentDashboard").setViewName("student/dashboard");
        //registry.addViewController("/toTeacherDashboard").setViewName("teacher/dashboard");
        //registry.addViewController("/toAddQuestion").setViewName("teacher/question/add");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").
                excludePathPatterns("/admin/**","/js/**","/layui/**","/login","/login/**","/");
    }
}
