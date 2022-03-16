package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.ApiResult;
import com.exam.entity.Teacher;
import com.exam.serviceimpl.TeacherServiceImpl;
import com.exam.util.ApiResultHandler;
import com.exam.vo.AnswerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TeacherController {

    private TeacherServiceImpl teacherService;

    @Autowired
    public TeacherController(TeacherServiceImpl teacherService){
        this.teacherService = teacherService;
    }


    @GetMapping("/teacher/{teacherId}")
    public String toUpdateTeacher(@PathVariable("teacherId")Integer teacherId, Model model){
        Teacher teacher  = teacherService.findById(teacherId);
        if(teacher!=null){
            model.addAttribute("teacher",teacher);
            model.addAttribute("ApiResult",ApiResultHandler.buildApiResult(200,"查询教师信息",teacher));
        }
        return "teacher/profile/profile";
    }
//    @GetMapping("/teachers/{page}/{size}")
//    public ApiResult findAll(@PathVariable Integer page, @PathVariable Integer size){
//        Page<Teacher> teacherPage = new Page<>(page,size);
//        IPage<Teacher> teacherIPage = teacherService.findAll(teacherPage);
//
//        return ApiResultHandler.buildApiResult(200,"查询所有教师",teacherIPage);
//    }
//
//    @GetMapping("/teacher/{teacherId}")
//    public ApiResult findById(@PathVariable("teacherId") Integer teacherId){
//        return ApiResultHandler.success(teacherService.findById(teacherId));
//    }
//
//    @DeleteMapping("/teacher/{teacherId}")
//    public ApiResult deleteById(@PathVariable("teacherId") Integer teacherId){
//        return ApiResultHandler.success(teacherService.deleteById(teacherId));
//    }
//
    @PostMapping("/teacher")
    public String update(Teacher teacher,Model model){
        int res = teacherService.update(teacher);
        if(res!=0){
            model.addAttribute(ApiResultHandler.success(res));
            model.addAttribute("teacher",teacher);
        }
        return "/teacher/profile/profile";
    }
//
//    @PostMapping("/teacher")
//    public ApiResult add(@RequestBody Teacher teacher){
//        return ApiResultHandler.success(teacherService.add(teacher));
//    }
}
