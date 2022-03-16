package com.exam.controller.teacherAuthorityController;

import com.exam.entity.*;
import com.exam.serviceimpl.*;
import com.exam.util.ApiResultHandler;
import com.exam.vo.MarkVO;
import com.exam.vo.PaperVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 老师批改试卷
 *
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/12/20
 */

@Controller
@Component
public class MarkController {


    private final ScoreServiceImpl scoreService;
    private final StudentServiceImpl studentService;
    private final ExamManageServiceImpl examManageService;
    private final AnswerServiceImpl answerService;
    private final MultiQuestionServiceImpl multiQuestionService;
    private final FillQuestionServiceImpl fillQuestionService;
    private final CalQuestionServiceImpl calQuestionService;
    private final ProveQuestionServiceImpl proveQuestionService;

    /**
     * 构造器注入
     * @param scoreService
     * @param studentService
     * @param examManageService
     * @param answerService
     * @param multiQuestionService
     * @param fillQuestionService
     * @param calQuestionService
     * @param proveQuestionService
     */
    @Autowired
    public MarkController(ScoreServiceImpl scoreService, StudentServiceImpl studentService, ExamManageServiceImpl examManageService, AnswerServiceImpl answerService, MultiQuestionServiceImpl multiQuestionService, FillQuestionServiceImpl fillQuestionService, CalQuestionServiceImpl calQuestionService, ProveQuestionServiceImpl proveQuestionService) {
        this.scoreService = scoreService;
        this.studentService = studentService;
        this.examManageService = examManageService;
        this.answerService = answerService;
        this.multiQuestionService = multiQuestionService;
        this.fillQuestionService = fillQuestionService;
        this.calQuestionService = calQuestionService;
        this.proveQuestionService = proveQuestionService;
    }

    @GetMapping("/toMarkPage/{examCode}/{studentId}")
    public String toMarkPage(@PathVariable("examCode") String examCode, @PathVariable("studentId") String studentId, Model model) {
        model.addAttribute("examCode", examCode);
        model.addAttribute("studentId", studentId);

        Student student = studentService.findById(Integer.valueOf(studentId));
        ExamManage examManage = examManageService.findById(Integer.valueOf(examCode));

        model.addAttribute("studentName", student.getStudentName());
        model.addAttribute("examName", examManage.getSource());

        return "teacher/exam/mark";
    }

    @GetMapping("/toPaperPage")
    public String toPaperPage() {
        return "teacher/exam/paper";
    }

    /**
     * 获取需要批改的试卷
     * @return
     */
    @GetMapping("/paper/marks")
    @ResponseBody
    public ApiResult toBeMarkedPapers() {

        //获取需要打分的
        List<Score> toBeMarkedScore = scoreService.getToBeMarkedScore();
        List<PaperVO> list = new LinkedList<>();
        for (Score score : toBeMarkedScore) {
            PaperVO paperVO = new PaperVO();

            //获取学生名称
            Student student = studentService.findById(score.getStudentId());
            //获取考试名称
            ExamManage exam = examManageService.findById(score.getExamCode());
            if(student==null||exam==null){
                //如果学生或考试已经被删除了,就直接跳过,并逻辑删除这个成绩
                scoreService.deleteScoreById(score.getScoreId().toString());
                continue;
            }
            String studentName = student.getStudentName();

            String subject = exam.getSource();
            paperVO.setAnswerDate(score.getAnswerDate());
            paperVO.setExamCode(String.valueOf(score.getExamCode()));
            paperVO.setStudentId(String.valueOf(score.getStudentId()));
            paperVO.setStudentName(studentName);
            paperVO.setSubject(subject);

            list.add(paperVO);
        }


        return ApiResultHandler.buildApiResult(0, "请求成功！", list);
    }

    /**
     * 获取待批改试卷的题目
     * @param examCode
     * @param studentId
     * @return
     */
    @GetMapping("/paper/questions")
    @ResponseBody
    public ApiResult getQuestionsToBeMarked(@RequestParam("examCode") String examCode, @RequestParam("studentId") String studentId) {

        //这次考试这个学生需要批改的题目
        List<Answer> answerList = answerService.getAnswerByExamCodeAndStudentId(examCode, studentId);

        List<MarkVO> markVOList = new LinkedList<>();


        for (Answer answer : answerList) {
            MarkVO markVO = new MarkVO();

            Question question = new Question();
            //如果是选择题
            if ("1".equals(answer.getQuestionType())) {
                question = multiQuestionService.findById(answer.getQuestionId());
            } else if ("2".equals(answer.getQuestionType())) {
                question = fillQuestionService.findById(answer.getQuestionId());
            } else if ("3".equals(answer.getQuestionType())) {
                question = calQuestionService.findById(answer.getQuestionId());
            } else if ("4".equals(answer.getQuestionType())) {
                question = proveQuestionService.findById(answer.getQuestionId());
            }

            markVO.setQuestion(question.getQuestion());
            markVO.setQuestionId(answer.getQuestionId());
            markVO.setQuestionType(answer.getQuestionType());
            markVO.setRightAnswer(question.getAnswer());
            markVO.setStudentAnswer(answer.getStudentAnswer());
            //选择题分数会有
            markVO.setScore(answer.getFinalScore());
            //总分为多少，这题的
            markVO.setFullScore(String.valueOf(question.getScore()));

            markVOList.add(markVO);
        }

        return ApiResultHandler.buildApiResult(0, "请求成功！", markVOList);
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 老师提交批改结果并写入数据库
     * @param questionIdList
     * @param questionTypeList
     * @param finalScoreList
     * @param examCode
     * @param studentId
     * @return
     */
    @PostMapping("/paper/marked")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public ApiResult marked(@RequestParam("questionIdList") List<String> questionIdList,
                            @RequestParam("questionTypeList") List<String> questionTypeList,
                            @RequestParam("finalScoreList") List<String> finalScoreList,
                            @RequestParam("examCode") String examCode,
                            @RequestParam("studentId") String studentId) {

        for (int i = 0; i < finalScoreList.size(); i++) {
            if(finalScoreList.get(i)==null||"".equals(finalScoreList.get(i))){
                return ApiResultHandler.buildApiResult(412,"有些题目没有打分,请打完分后提交",null);
            }
            if(!isNumeric(finalScoreList.get(i))){
                return ApiResultHandler.buildApiResult(413,"分数不是数字，请重新输入",null);
            }
        }

        //定义事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();

        try{
            for (int i = 0; i < questionIdList.size(); i++) {
                //更新所有分数
                answerService.updateFinalScoreBy(finalScoreList.get(i),studentId,examCode,questionIdList.get(i),questionTypeList.get(i));
            }
            int totalScore=0;
            //更新完成后统计分数，给到分数表中
            for (int i = 0; i < finalScoreList.size(); i++) {
                totalScore += Integer.parseInt(finalScoreList.get(i));
            }
            scoreService.updateScoreByExamCodeAndStudentId(String.valueOf(totalScore),examCode,studentId);
            scoreService.setIsMarked(examCode,studentId);
            return ApiResultHandler.buildApiResult(200, "批阅成功！", null);
        }catch (Exception e){
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(500, "批阅失败！", null);
        }
    }

}
