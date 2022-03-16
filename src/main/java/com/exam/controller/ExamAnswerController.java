package com.exam.controller;

import com.exam.entity.*;
import com.exam.serviceimpl.*;
import com.exam.util.ApiResultHandler;
import com.exam.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/10/26
 */
@RestController
@RequestMapping("/exam")
public class ExamAnswerController {

    private final MultiQuestionServiceImpl multiQuestionService;

    private final FillQuestionServiceImpl fillQuestionService;

    private final ExamManageServiceImpl examManageService;

    private final ScoreServiceImpl scoreService;

    private final AnswerServiceImpl answerService;

    @Autowired
    public ExamAnswerController(MultiQuestionServiceImpl multiQuestionService, FillQuestionServiceImpl fillQuestionService, ExamManageServiceImpl examManageService, ScoreServiceImpl scoreService, AnswerServiceImpl answerService) {
        this.multiQuestionService = multiQuestionService;
        this.fillQuestionService = fillQuestionService;
        this.examManageService = examManageService;
        this.scoreService = scoreService;
        this.answerService = answerService;
    }

    @PostMapping("/submitAnswer")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult submitAnswer(@RequestParam("answerList") List<String> answerList, @RequestParam("questionIdList") List<String> questionIdList, @RequestParam("questionTypeList") List<String> questionTypeList, @RequestParam("examCode")int examCode, HttpSession session) {

        for (int i = 0; i < answerList.size(); i++) {
            if(answerList.get(i)==null||"".equals(answerList.get(i))){
                return ApiResultHandler.buildApiResult(411,"有答案为空，请重新填写提交",null);
            }
        }
        //定义事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();

        try {
            //获取考试人的id
            String studentId = String.valueOf(session.getAttribute("id"));
            //String studentId = CookieUtil.getCookieValue(request,"id");
            //获取考试信息
            ExamManage examManage = examManageService.findById(examCode);

            for (int i = 0; i < answerList.size(); i++) {
                Answer answer = new Answer();
                //如果题目类型是 选择题或者填空题，就自动批改
                if ("1".equals(questionTypeList.get(i)) || "2".equals(questionTypeList.get(i))) {
                    Question question = null;
                    if ("1".equals(questionTypeList.get(i))) {
                        question = multiQuestionService.findById(questionIdList.get(i));
                    }
                    if ("2".equals(questionTypeList.get(i))) {
                        question = fillQuestionService.findById(questionIdList.get(i));
                    }
                    //题目被删除了，可能，应该不会
                    if (question == null) {
                        System.out.println("题目不存在");
                        continue;
                    }
                    if (answerList.get(i).equals(question.getAnswer())) {
                        //答案对就满分
                        answer.setFinalScore(String.valueOf(question.getScore()));
                    } else {
                        //错了给0分
                        answer.setFinalScore("0");
                    }
                }
                answer.setStudentId(studentId);
                answer.setExamCode(String.valueOf(examCode));
                answer.setQuestionId(questionIdList.get(i));
                answer.setQuestionType(questionTypeList.get(i));
                answer.setComment("");
                answer.setStudentAnswer(answerList.get(i));


                answerService.add(answer);

            }

            //录入Score信息，但是未打分
            Score score = new Score();
            score.setStudentId(Integer.valueOf(studentId));
            score.setAnswerDate("");
            score.setExamCode(Integer.valueOf(examCode));
            //获取科目
            score.setSubject(examManage.getSource());
            //设置为还未被打分
            score.setIs_marked(false);
            //日期
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            score.setAnswerDate(simpleDateFormat.format(date));
            scoreService.add(score);

            return ApiResultHandler.buildApiResult(200, "提交试卷成功",null);
        }catch (Exception e){
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(400,"提交试卷失败",null);
        }

    }
}
