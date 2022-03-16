package com.exam.controller.teacherAuthorityController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.ApiResult;
import com.exam.entity.ExamManage;
import com.exam.serviceimpl.ExamManageServiceImpl;
import com.exam.serviceimpl.PaperServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author nondeterminacy
 */
@RestController
@RequestMapping("exam")
public class ExamManageController {

    @Autowired
    private ExamManageServiceImpl examManageService;

    @Autowired
    private PaperServiceImpl paperService;


    @GetMapping("/exams")
    public ApiResult findAll(){
        System.out.println("不分页查询所有试卷");
        ApiResult apiResult;
        apiResult = ApiResultHandler.buildApiResult(0, "请求成功！", examManageService.findAll());
        return apiResult;
    }

    @GetMapping("/exams/{page}/{size}")
    public ApiResult findAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        System.out.println("分页查询所有试卷");
        ApiResult apiResult;
        Page<ExamManage> examManage = new Page<>(page,size);
        IPage<ExamManage> all = examManageService.findAll(examManage);
        apiResult = ApiResultHandler.buildApiResult(200, "请求成功！", all);
        return apiResult;
    }

    @GetMapping("/exam/{examCode}")
    public ApiResult findById(@PathVariable("examCode") Integer examCode){
        System.out.println("根据ID查找");
        ExamManage res = examManageService.findById(examCode);
        if(res == null) {
            return ApiResultHandler.buildApiResult(10000,"考试编号不存在",null);
        }
        return ApiResultHandler.buildApiResult(200,"请求成功！",res);
    }

    @RequestMapping(value = "/exam/{examCode}",method = RequestMethod.DELETE)
    public ApiResult delete(@PathVariable("examCode") Integer examCode){
        int res = examManageService.delete(examCode);
        return ApiResultHandler.buildApiResult(200,"删除成功",res);
    }

    @RequestMapping(value = "/exam",method = RequestMethod.PUT)
    public ApiResult update(ExamManage exammanage){
        int res = examManageService.update(exammanage);
        System.out.print("更新操作执行---");
        return ApiResultHandler.buildApiResult(200,"更新成功",res);
    }

    /**
     * 添加一场考试
     * @param exammanage
     * @return
     */
    @PostMapping("/exam/add")
    public ApiResult add(ExamManage exammanage){

        int res = examManageService.add(exammanage);
        if (res == 1) {
            return ApiResultHandler.buildApiResult(200, "考试添加成功，请去考试页面生成试题", res);
        } else {
            return  ApiResultHandler.buildApiResult(500,"添加失败",res);
        }
    }

    @GetMapping("/examManagePaperId")
    public ApiResult findOnlyPaperId() {
        ExamManage res = examManageService.findOnlyPaperId();
        if (res != null) {
            return ApiResultHandler.buildApiResult(200,"请求成功",res);
        }
        return ApiResultHandler.buildApiResult(400,"请求失败", null);
    }
}
