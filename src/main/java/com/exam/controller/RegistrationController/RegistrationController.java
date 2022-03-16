package com.exam.controller.RegistrationController;


import com.exam.entity.ApiResult;
import com.exam.entity.Student;
import com.exam.service.LoginService;
import com.exam.service.StudentService;
import com.exam.service.TeacherService;
import com.exam.serviceimpl.LoginServiceImpl;
import com.exam.util.ApiResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;

import java.util.List;

@Controller
@Slf4j
public class RegistrationController {
    @Autowired
    StudentService studentService;

    @Autowired
    TeacherService teacherService;

    @GetMapping("/registration")
    public String registration(){
        log.info("跳转到注册页面了");
        return "Register";
    }

    @RequestMapping("/registration_check")
    @ResponseBody
    public int Check(@RequestParam("username")String username,@RequestParam("pwd")String pwd){

        Student student = new Student();
        student.setUsername(username);
        student.setPwd(pwd);
        if(studentService.findByUsername(student.getUsername())!=null || teacherService.findByUserName(student.getUsername())!=null){
            return 0;
        }
        student.setRole("2");  
        List<Student> students=studentService.findAll();
        if(students.size()==0){
            student.setStudentId(1);
        }
        else{
            int lastStudentId=studentService.findOnlyStudentId();
            student.setStudentId(lastStudentId+1);
        }
        studentService.add(student);
        return 1;
    }

}
