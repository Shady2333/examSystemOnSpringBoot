package com.exam.controller.questionController;


import com.exam.entity.ApiResult;
import com.exam.entity.CalQuestion;
import com.exam.serviceimpl.CalQuestionServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class CalQuestionController{

    @Autowired
    private CalQuestionServiceImpl calQuestionService;

    @PostMapping("/calQuestion")
    public ApiResult add(CalQuestion calQuestion) {
        int res = calQuestionService.add(calQuestion);
        if (res != 0) {
            return ApiResultHandler.buildApiResult(200,"添加成功",res);
        }
        return ApiResultHandler.buildApiResult(400,"添加失败",res);
    }

    @GetMapping("/calQuestionId")
    public ApiResult findOnlyQuestionId() {
        CalQuestion res = calQuestionService.findOnlyQuestionId();
        return ApiResultHandler.buildApiResult(200,"查询成功",res);
    }
    @GetMapping("/calQuestion/delete/{id}")
    public ApiResult delete(@PathVariable("id")Integer id){
        int res = calQuestionService.delete(id);
        if(res!=0){
            return ApiResultHandler.buildApiResult(200,"删除成功",res);
        }
        return ApiResultHandler.buildApiResult(500,"删除失败",res);
    }
    @GetMapping("/calQuestions")
    public ApiResult findAll(){
        List<CalQuestion> res = calQuestionService.findAll();
        return ApiResultHandler.buildApiResult(0,"查询成功",res);
    }
    //2020-8-12
    @GetMapping("/calQuestions/section/{section}")
    public ApiResult findBySection(@PathVariable("section")String section){
        List<CalQuestion> res = calQuestionService.findBySection(section);
        return ApiResultHandler.buildApiResult(200,"查询成功",res);
    }
}
