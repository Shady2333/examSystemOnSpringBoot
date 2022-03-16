package com.exam.controller;

import com.exam.entity.ApiResult;
import com.exam.entity.Score;
import com.exam.entity.Student;
import com.exam.service.StudentService;
import com.exam.serviceimpl.ScoreServiceImpl;
import com.exam.util.ApiResultHandler;
import com.exam.vo.ExamScoreVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.LinkedList;
import java.util.List;

@RestController
public class ScoreController {
    @Autowired
    private ScoreServiceImpl scoreService;
    @Autowired
    private StudentService studentService;


    /**
     * 查询出我的考试成绩信息
     * @param session
     * @return
     */
    @GetMapping("/score/myAll")
    public ApiResult findScores(HttpSession session){

        Integer studentId = (Integer) session.getAttribute("id");

        List<Score> res = scoreService.findById(studentId);
        if(res==null){
            return ApiResultHandler.buildApiResult(0, "请求失败！", null);
        }else{
            return ApiResultHandler.buildApiResult(0, "请求成功！", res);
        }
    }

    @RequestMapping(name = "获取某场考试的所有成绩",value = "/scores/{examCode}",method = RequestMethod.GET)
    public ApiResult getScoresByExamCode(@PathVariable("examCode") String examCode){
        List<Score> scores = scoreService.selectScoresByExamCode(examCode);
        List<ExamScoreVo> scoreVoList = new LinkedList<>();
        for (Score score:scores){
            ExamScoreVo examScoreVo = new ExamScoreVo();
            BeanUtils.copyProperties(score,examScoreVo);
            Student student = studentService.findById(Integer.valueOf(score.getStudentId()));
            if(student==null){
                //学生被删除了
                //todo 这里应该把这条分数逻辑删除
                scoreService.deleteScoreById(score.getScoreId().toString());
                continue;
            }
            examScoreVo.setStudentName(student.getStudentName());

            scoreVoList.add(examScoreVo);
        }
        return ApiResultHandler.buildApiResult(0, "请求成功！", scoreVoList);
    }


    @GetMapping("/score/all")
    public ApiResult findAll() {
        List<Score> res = scoreService.findAll();
        return ApiResultHandler.buildApiResult(200,"查询所有学生成绩",res);
    }

    @GetMapping("/score/{studentId}")
    public ApiResult findById(@PathVariable("studentId") Integer studentId) {
        List<Score> res = scoreService.findById(studentId);
        if (!res.isEmpty()) {
            return ApiResultHandler.buildApiResult(200,"根据ID查询成绩",res);
        }else {
            return ApiResultHandler.buildApiResult(400,"ID不存在",res);
        }
    }

    @PostMapping("/score")
    public ApiResult add(@RequestBody Score score) {
        int res = scoreService.add(score);
        if (res == 0) {
            return ApiResultHandler.buildApiResult(400,"成绩添加失败",res);
        }else {
            return ApiResultHandler.buildApiResult(200,"成绩添加成功",res);
        }
    }
//
//    @GetMapping("/scores/{examCode}")
//    public ApiResult findByExamCode(@PathVariable("examCode") Integer examCode) {
//        List<Score> scores = scoreService.findByExamCode(examCode);
//        return ApiResultHandler.buildApiResult(200,"查询成功",scores);
//    }
}
