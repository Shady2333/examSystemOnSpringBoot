package com.exam.controller.teacherAuthorityController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.ApiResult;
import com.exam.entity.Student;
import com.exam.serviceimpl.StudentServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("student")
public class StudentController {

    @Autowired
    private StudentServiceImpl studentService;

//    @GetMapping("/students/{page}/{size}")
//    public ApiResult findAll(@PathVariable Integer page, @PathVariable Integer size) {
//        Page<Student> studentPage = new Page<>(page,size);
//        IPage<Student> res = studentService.findAll(studentPage);
//        return  ApiResultHandler.buildApiResult(200,"分页查询所有学生",res);
//    }
//    @GetMapping("/students")
//    public ApiResult findAll(Model model) {
//        Page<Student> studentPage = new Page<>(1,100);
//        IPage<Student> res = studentService.findAll(studentPage);
//
//        return ApiResultHandler.buildApiResult(200,"分页查询所有学生",res);
//    }
    @GetMapping("/students")
    public ApiResult findAll() {
        List<Student> res = studentService.findAll();
        return ApiResultHandler.buildApiResult(0,"查询所有学生",res);
    }


    @GetMapping("/student/{studentId}")
    public String findById(@PathVariable("studentId") Integer studentId,Model model) {
        Student res = studentService.findById(studentId);
        if(res!=null){
            model.addAttribute("student",res);
            model.addAttribute("ApiResult",ApiResultHandler.buildApiResult(200,"请求成功",res));
        }else{
            model.addAttribute("ApiResult",ApiResultHandler.buildApiResult(404,"查询的用户不存在",null));
        }
        return "student/profile/profile";
    }
//    @GetMapping("/student/{studentId}")
//    public ApiResult findById(@PathVariable("studentId") Integer studentId) {
//        Student res = studentService.findById(studentId);
//        if (res != null) {
//            return ApiResultHandler.buildApiResult(200,"请求成功",res);
//        } else {
//            return ApiResultHandler.buildApiResult(404,"查询的用户不存在",null);
//        }
//    }

//    @DeleteMapping("/student/{studentId}")
//    public String deleteById(@PathVariable("studentId") Integer studentId,Model model) {
//        int res = studentService.deleteById(studentId);
//        if(res!=0){
//            model.addAttribute("ApiResult",ApiResultHandler.buildApiResult(200,"删除成功",studentService.deleteById(studentId)));
//        }
//        return "redirect:/teacher/student/list";
//    }
    @RequestMapping(method = RequestMethod.GET,value = "/delete/{studentId}")
    public ApiResult deleteById(@PathVariable("studentId") Integer studentId) {
        int res = studentService.deleteById(studentId);
        return ApiResultHandler.buildApiResult(200,"删除成功",studentService.deleteById(studentId));
    }
//
//    @PutMapping("/studentPWD")
//    public ApiResult updatePwd(@RequestBody Student student) {
//        studentService.updatePwd(student);
//        return ApiResultHandler.buildApiResult(200,"密码更新成功",null);
//    }

    @GetMapping("/students/{studentId}")
    public String toUpdateStudent(@PathVariable("studentId")Integer studentId,Model model){
        Student student = studentService.findById(studentId);
        if(student!=null){
            model.addAttribute("student",student);
            model.addAttribute("ApiResult",ApiResultHandler.buildApiResult(200,"查询成功",student));
        }
        return "teacher/student/update";
    }

    @PostMapping("/student/update")
    public ApiResult update(Student student, HttpSession session) {
        Object id = session.getAttribute("id");
        student.setStudentId((Integer) id);
        int res = studentService.update(student);
        if (res != 0) {
            return ApiResultHandler.buildApiResult(400,"更新成功",res);
        }
        return ApiResultHandler.buildApiResult(500,"更新失败",res);
    }
//    @PutMapping("/student")
//    public ApiResult update(@RequestBody Student student) {
//        int res = studentService.update(student);
//        if (res != 0) {
//            return ApiResultHandler.buildApiResult(200,"更新成功",res);
//        }
//        return ApiResultHandler.buildApiResult(400,"更新失败",res);
//    }
//
//    @PostMapping("/student")
//    public ApiResult add(@RequestBody Student student) {
//        int res = studentService.add(student);
//        if (res == 1) {
//            return ApiResultHandler.buildApiResult(200,"添加成功",null);
//        }else {
//            return ApiResultHandler.buildApiResult(400,"添加失败",null);
//        }
//    }
}
