package com.exam.config.shiroconfig;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/12/18
 */


@Configuration
public class ShiroConfig {


    /**
     * ShiroFilterFactoryBean  3
     * @param defaultWebSecurityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();

        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

        //添加过滤器
        /**
         * anon ：无需认证
         * authc: 必须认证
         * user: 必须拥有 记住我功能
         * perms: 拥有对某个资源的权限
         * roles：拥有某个权限的角色
         */
        //拦截
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        //授权,授权失败应该跳转到未授权页面

        //无用权限
        filterChainDefinitionMap.put("/admin/*","authc,roles[TEACHER]");
        //只有老师能够获取所有题目信息
        filterChainDefinitionMap.put("/question/*","authc,roles[TEACHER]");
        //考试的获取学生可以，其他学生无权限
        filterChainDefinitionMap.put("/exam/all","authc,roles[STUDENT]");
        filterChainDefinitionMap.put("/exam/submitAnswer","authc,roles[STUDENT]");
        filterChainDefinitionMap.put("/exam/*","authc,roles[TEACHER]");
        //分数查看权限
        filterChainDefinitionMap.put("/score/myAll","authc,roles[STUDENT]");
        filterChainDefinitionMap.put("/score/*","authc,roles[TEACHER]");
        //试卷查看和批改
        //filterChainDefinitionMap.put("/paper/{examCode}","authc,roles[STUDENT]");
        filterChainDefinitionMap.put("/paper/RandomGenerate","authc,roles[TEACHER]");
        filterChainDefinitionMap.put("/paper/candidateQuestions","authc,roles[TEACHER]");
        filterChainDefinitionMap.put("/paper/handChoose","authc,roles[TEACHER]");
        //学生管理
        filterChainDefinitionMap.put("/student/student/update","authc,roles[STUDENT]");
        filterChainDefinitionMap.put("/student/student/*","authc,roles[TEACHER]");
        filterChainDefinitionMap.put("/student/students","authc,roles[TEACHER]");
        //老师管理
        filterChainDefinitionMap.put("/teacher/*","authc,roles[TEACHER]");

        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        //登录请求
        bean.setLoginUrl("/toLogin");
        //未授权页面
        bean.setUnauthorizedUrl("/toUnauthorized");


        return bean;
    }


    /**
     * DefaultWebSecurityManager  2
     * @param userRealm
     * @return
     */
    @Bean(name = "defaultWebSecurityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){

        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();

        defaultWebSecurityManager.setRealm(userRealm);

        return defaultWebSecurityManager;
    }

    /**
     * 创建 realm 对象 ，自定义   1
     * @return
     */
    @Bean(name = "userRealm")
    public UserRealm getUserRealm(){
        return new UserRealm();
    }


    /**
     * 整合 ShiroDialect: 用来整合thymeleaf shiro
     * @return
     */
    @Bean
    public ShiroDialect getShiroDialect(){
        return  new ShiroDialect();
    }




    /**
     *  开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启aop注解支持
     * @param defaultWebSecurityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("defaultWebSecurityManager")DefaultWebSecurityManager defaultWebSecurityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
