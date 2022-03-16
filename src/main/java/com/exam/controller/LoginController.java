package com.exam.controller;

import com.exam.entity.*;
import com.exam.serviceimpl.LoginServiceImpl;
import com.exam.util.ApiResultHandler;
import com.exam.util.CookieUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Session;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    private LoginServiceImpl loginService;


    @RequestMapping({"/toLogin","/"})
    public String toLogin(){
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,HttpSession session,HttpServletRequest request,HttpServletResponse response){

        if(session.getAttribute("verifyCode")==null||!session.getAttribute("verifyCode").equals(verifyCode)){
            session.setAttribute("errorMsg","验证码错误");
            return "login";
        }
        Subject subject = SecurityUtils.getSubject();
        //封装登录数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        //登录
        try{
            subject.login(token);
            //获取用户
            User user = (User) subject.getPrincipal();
            if("1".equals(user.getRole())){
                Teacher teacher = (Teacher) user;
                session.setAttribute("id",teacher.getTeacherId());
                //重定向cookie传不过去
                CookieUtil.setCookie(request,response,"id",String.valueOf(teacher.getTeacherId()));
                return "redirect:teacherIndex";
            }else if("2".equals(user.getRole())){
                Student student = (Student) user;
                //在session存储一些信息,shiro也有他自己的session,都可以用，这里用的是httpSession，因为旧的代码都是httpSession
                session.setAttribute("id",student.getStudentId());
                CookieUtil.setCookie(request,response,"id",String.valueOf(student.getStudentId()));
                return "redirect:studentIndex";
            }
            return "login";
        }catch (UnknownAccountException e){ //用户名不存在
            session.setAttribute("errorMsg","用户名不存在");
            return "login";
        }catch (IncorrectCredentialsException e){
            session.setAttribute("errorMsg","密码错误");
            return "login";
        }

    }

    /**
     * 生成验证码并放到session中
     */
    @RequestMapping(value = "/login/captcha", method = RequestMethod.GET)
    @ResponseBody
    public void verifyCode(HttpSession session,HttpServletResponse response) {

        //设置请求头为输出图片的类型
        response.setContentType("image/png");
        response.setHeader("Param", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入session
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        System.out.println(captcha.text());
        session.setAttribute("verifyCode",captcha.text());
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }

    @RequestMapping("/quit")
    public String logout(HttpSession session){
        session.removeAttribute("id");
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return "login";
    }

    @ResponseBody
    @RequestMapping("/toUnauthorized")
    public ApiResult unauthorized(){
        return ApiResultHandler.buildApiResult(403,"没有权限",null);
    }


}
