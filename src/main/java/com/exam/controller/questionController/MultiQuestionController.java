package com.exam.controller.questionController;

import com.exam.entity.ApiResult;
import com.exam.entity.MultiQuestion;
import com.exam.serviceimpl.MultiQuestionServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class MultiQuestionController {

    @Autowired
    private MultiQuestionServiceImpl multiQuestionService;

    @GetMapping("/multiQuestionId")
    public ApiResult findOnlyQuestion() {
        MultiQuestion res = multiQuestionService.findOnlyQuestionId();
        return ApiResultHandler.buildApiResult(200,"查询成功",res);
    }
    @GetMapping("/MultiQuestions")
    public ApiResult findAll(){
        List<MultiQuestion> res = multiQuestionService.findAll();
        return ApiResultHandler.buildApiResult(0,"查询成功",res);
    }

    @GetMapping("/MultiQuestion/delete/{id}")
    public ApiResult delete(@PathVariable("id")Integer id){
        int res = multiQuestionService.deleteById(id);
        if(res!=0){
            return ApiResultHandler.buildApiResult(200,"删除成功",res);
        }
        return ApiResultHandler.buildApiResult(500,"删除失败",res);

    }
    @PostMapping("/MultiQuestion")
    public ApiResult add(MultiQuestion multiQuestion) {
        int res = multiQuestionService.add(multiQuestion);
        if (res != 0) {
            return ApiResultHandler.buildApiResult(200,"添加成功",res);
        }
        return ApiResultHandler.buildApiResult(400,"添加失败",res);
    }

    //2020-8-12
    @GetMapping("/multiQuestions/section/{section}")
    public ApiResult findBySection(@PathVariable("section")String section){
        List<MultiQuestion> res = multiQuestionService.findBySection(section);
        return ApiResultHandler.buildApiResult(200,"查询成功",res);
    }
}
