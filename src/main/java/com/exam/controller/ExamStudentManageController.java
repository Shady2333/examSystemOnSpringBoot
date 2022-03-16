package com.exam.controller;


import com.exam.entity.ApiResult;
import com.exam.serviceimpl.ExamManageServiceImpl;
import com.exam.serviceimpl.ScoreServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class ExamStudentManageController {

    @Autowired
    private ExamManageServiceImpl examManageService;

    @Autowired
    ScoreServiceImpl scoreService;

    /**
     * 查询出所有考试信息
     * @return
     */
    @GetMapping("/exam/all")
    public ApiResult findAll(){
        ApiResult apiResult;
        apiResult = ApiResultHandler.buildApiResult(0, "请求成功！", examManageService.findAll());
        return apiResult;
    }

}
