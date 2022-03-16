package com.exam.controller.questionController;


import com.exam.entity.ApiResult;
import com.exam.entity.ProveQuestion;
import com.exam.service.ProveQuestionService;
import com.exam.serviceimpl.ProveQuestionServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class ProveQuestionController {

    @Autowired
    ProveQuestionServiceImpl proveQuestionService;
    @GetMapping("/proveQuestionId")
    public ApiResult findOnlyQuestion(){
        ProveQuestion res = proveQuestionService.findOnlyQuestionId();
        return  ApiResultHandler.buildApiResult(200,"查询成功",res);
    }
    @PostMapping("/ProveQuestion")
    public ApiResult add(ProveQuestion proveQuestion){
        int res = proveQuestionService.add(proveQuestion);
        if (res != 0) {
            return ApiResultHandler.buildApiResult(200,"添加成功",res);
        }
        return ApiResultHandler.buildApiResult(400,"添加失败",res);
    }
    @GetMapping("/ProveQuestions")
    public ApiResult findAll(){
        List<ProveQuestion> res = proveQuestionService.findAll();
        return ApiResultHandler.buildApiResult(0,"查询成功",res);
    }

    //2020-8-12
    @GetMapping("/proveQuestions/section/{section}")
    public ApiResult findBySection(@PathVariable("section")String section){
        List<ProveQuestion> res = proveQuestionService.findBySection(section);
        return ApiResultHandler.buildApiResult(200,"查询成功",res);
    }

    @GetMapping("/proveQuestion/delete/{id}")
    public ApiResult delete(@PathVariable("id")Integer id){
        int res = proveQuestionService.delete(id);
        if(res!=0){
            return ApiResultHandler.buildApiResult(200,"删除成功",res);
        }
        return ApiResultHandler.buildApiResult(500,"删除失败",res);
    }
}
