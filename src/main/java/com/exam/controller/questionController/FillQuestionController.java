package com.exam.controller.questionController;

import com.exam.entity.ApiResult;
import com.exam.entity.FillQuestion;
import com.exam.serviceimpl.FillQuestionServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class FillQuestionController {

    @Autowired
    private FillQuestionServiceImpl fillQuestionService;

    @PostMapping("/fillQuestion")
    public ApiResult add(FillQuestion fillQuestion) {
        int res = fillQuestionService.add(fillQuestion);
        if (res != 0) {
            return ApiResultHandler.buildApiResult(200,"添加成功",res);
        }
        return ApiResultHandler.buildApiResult(400,"添加失败",res);
    }


    @GetMapping("/fillQuestions")
    public ApiResult findAll(){
        List<FillQuestion>  res= fillQuestionService.findAll();
        return ApiResultHandler.buildApiResult(0,"查询成功",res);
    }

    @GetMapping("/fillQuestion/delete/{id}")
    public ApiResult delete(@PathVariable("id")Integer id){
        int res = fillQuestionService.delete(id);
        if(res!=0){
            return ApiResultHandler.buildApiResult(200,"删除成功",res);
        }
        return ApiResultHandler.buildApiResult(500,"删除失败",res);
    }
    @GetMapping("/fillQuestionId")
    public ApiResult findOnlyQuestionId() {
        FillQuestion res = fillQuestionService.findOnlyQuestionId();
        return ApiResultHandler.buildApiResult(200,"查询成功",res);
    }
    //通过section查询
    @GetMapping("/fillQuestions/section/{section}")
    public ApiResult findBySection(@PathVariable("section")String section){
        List<FillQuestion> res = fillQuestionService.findBySection(section);
        return ApiResultHandler.buildApiResult(200,"查询成功",res);
    }
}
