package com.exam.controller;

import com.exam.entity.*;
import com.exam.service.PaperService;
import com.exam.serviceimpl.*;
import com.exam.util.ApiResultHandler;
import com.exam.vo.ExamAndPaper;
import com.exam.vo.RandomGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/10/20
 */
@Controller
public class  PaperController {
    @Autowired
    private PaperServiceImpl paperService;

    @Autowired
    private MultiQuestionServiceImpl multiQuestionService;

    @Autowired
    private FillQuestionServiceImpl fillQuestionService;

    @Autowired
    private CalQuestionServiceImpl calQuestionService;

    @Autowired
    private ProveQuestionServiceImpl proveQuestionService;

    @Autowired
    private ExamManageServiceImpl examManageService;

    /**
     * 通过考试ID获取paperID,通过paperID获取题目
     *
     * @param examCode
     * @return
     */
    @GetMapping("/paper/{examCode}")
    @ResponseBody
    public ApiResult findById(@PathVariable("examCode") Integer examCode) {

        if (examCode == null) return ApiResultHandler.buildApiResult(400, "考试ID错误！", null);

        Map<Integer, List<?>> paperByExam = getPaperByExamCode(examCode);

        if (paperByExam == null) return ApiResultHandler.buildApiResult(400, "试卷为空！", null);

        return ApiResultHandler.buildApiResult(0, "请求成功！", paperByExam);

    }

    public Map<Integer, List<?>> getPaperByExamCode(Integer examCode) {

        if (examCode == null) {
            return null;
        }
        //获取
        ExamManage examManage = examManageService.findById(examCode);

        Integer paperId = examManage.getPaperId();
        //选择题题库 1
        List<MultiQuestion> multiQuestionRes = multiQuestionService.findByIdAndType(paperId);
        //填空题题库 2
        List<FillQuestion> fillQuestionsRes = fillQuestionService.findByIdAndType(paperId);
        List<CalQuestion> calQuestionsRes = calQuestionService.findByIdAndType(paperId);
        List<ProveQuestion> proveQuestionsRes = proveQuestionService.findByIdAndType(paperId);
        Map<Integer, List<?>> map = new HashMap<>(8);
        map.put(1, multiQuestionRes);
        map.put(2, fillQuestionsRes);
        map.put(3, calQuestionsRes);
        map.put(4, proveQuestionsRes);
        return map;
    }

    @GetMapping(name = "跳转到手动生成试卷页面", value = "/toHandChoosePage/{examCode}")
    public String toHandChoosePage(@PathVariable("examCode") Integer examCode, Model model) {
        model.addAttribute("examCode", examCode);
        return "teacher/paper/handChoose";
    }

    @PostMapping(name = "提交要添加到试卷中的题目", value = "/paper/handChoose")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public ApiResult handChoose(@RequestParam("questionTypeList") List<String> questionType,
                                @RequestParam("questionIdList") List<String> questionId,
                                @RequestParam("examCode") int examCode) {
        //事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        ExamManage examManage = examManageService.findById(examCode);
        Integer paperId = examManage.getPaperId();
        try {
            //添加试题到paper表中
            for (int i = 0; i < questionId.size(); i++) {
                paperService.add(new PaperManage(paperId, Integer.valueOf(questionType.get(i)), Integer.valueOf(questionId.get(i))));
            }
            return ApiResultHandler.buildApiResult(200, "试题添加成功！", null);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(500, "试卷生成成功失败,服务器出现未知异常！", null);
        }


    }

    @GetMapping(name = "获取待选择题目", value = "/paper/candidateQuestions")
    @ResponseBody
    public ApiResult getCandidateQuestions(@RequestParam("section") String section,
                                           @RequestParam("subject") String subject,
                                           @RequestParam("examCode") int examCode) {
        ExamManage examManage = examManageService.findById(examCode);
        //如果没有paperID 说明没有生成过试卷，直接生成一个最新的给他,之后就直接往这个paperID里面加题目就行了
        if (examManage.getPaperId() == null) {
            int onlyPaperId = paperService.findOnlyPaperId();
            examManage.setPaperId(onlyPaperId + 1);
            //更新到数据库中
            examManageService.update(examManage);
        }
        //通过科目和单元获取全部试题
        List<MultiQuestion> multiQuestions = multiQuestionService.findBySectionAndSubject(section, subject);
        List<FillQuestion> fillQuestions = fillQuestionService.findBySectionAndSubject(section, subject);
        List<CalQuestion> calQuestions = calQuestionService.findBySectionAndSubject(section, subject);
        List<ProveQuestion> proveQuestions = proveQuestionService.findBySectionAndSubject(section, subject);

        Map<Integer, List<?>> map = new HashMap<>(8);
        map.put(1, multiQuestions);
        map.put(2, fillQuestions);
        map.put(3, calQuestions);
        map.put(4, proveQuestions);

        return ApiResultHandler.buildApiResult(0, "请求成功！", map);
    }

    @GetMapping(name = "跳转到随机生成试卷页面", value = "/toRandomGeneratePage/{examCode}")
    public String toRandomGeneratePage(@PathVariable("examCode") Integer examCode, Model model) {
        model.addAttribute("examCode", examCode);
        return "teacher/paper/random";
    }

    /**
     * @param size1    选择题题目个数
     * @param size2    填空题题目个数
     * @param size3    计算题题目个数
     * @param size4    证明题题目个数
     * @param section  单元
     * @param subject  科目
     * @param examCode 考试ID
     * @return
     */
    @PostMapping(name = "随机生成试卷", value = "/paper/RandomGenerate")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public ApiResult RandomGenerate(@RequestParam("size1") int size1,
                                    @RequestParam("size2") int size2,
                                    @RequestParam("size3") int size3,
                                    @RequestParam("size4") int size4,
                                    @RequestParam("section") String section,
                                    @RequestParam("subject") String subject,
                                    @RequestParam("examCode") int examCode) {

        //事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            //通过ID获取exam
            ExamManage examManage = examManageService.findById(examCode);
            //然后开始生成试卷
            //通过科目和单元获取全部试题
            List<MultiQuestion> multiQuestions = multiQuestionService.findBySectionAndSubject(section, subject);
            List<FillQuestion> fillQuestions = fillQuestionService.findBySectionAndSubject(section, subject);
            List<CalQuestion> calQuestions = calQuestionService.findBySectionAndSubject(section, subject);
            List<ProveQuestion> proveQuestions = proveQuestionService.findBySectionAndSubject(section, subject);
            //如果题目数量不足
            if (multiQuestions.size() < size1 ||
                    fillQuestions.size() < size2 ||
                    calQuestions.size() < size3 ||
                    proveQuestions.size() < size4) {
                return ApiResultHandler.buildApiResult(415, "题库题目数量不足" +
                                "选择题个数为" + multiQuestions.size() +
                                "填空题个数为" + fillQuestions.size() +
                                "计算题个数为" + calQuestions.size() +
                                "证明题个数为" + proveQuestions.size() +
                                "。" + "请检查题目数量并重新提交。"
                        , null);
            }
            List<MultiQuestion> multiQuestion = pickQuestionRandomly1(multiQuestions.size(), size1, multiQuestions);
            List<FillQuestion> fillQuestion = pickQuestionRandomly2(fillQuestions.size(), size2, fillQuestions);
            List<CalQuestion> calQuestion = pickQuestionRandomly3(calQuestions.size(), size3, calQuestions);
            List<ProveQuestion> proveQuestion = pickQuestionRandomly4(proveQuestions.size(), size4, proveQuestions);
            int totalScore = 0;

            int paperId;
            if (examManage.getPaperId() == null) {
                //获取最近一次试卷编号
                int onlyPaperId = paperService.findOnlyPaperId();
                //计算出新的试卷编号
                paperId = onlyPaperId + 1;
                //设置paperID
                examManage.setPaperId(paperId);
            } else {
                paperId = examManage.getPaperId();
            }
            for (int i = 0; i < multiQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 1, multiQuestion.get(i).getQuestionId()));
                totalScore += multiQuestion.get(i).getScore();
            }
            for (int i = 0; i < fillQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 2, fillQuestion.get(i).getQuestionId()));
                totalScore += fillQuestion.get(i).getScore();
            }
            for (int i = 0; i < calQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 3, calQuestion.get(i).getQuestionId()));
                totalScore += calQuestion.get(i).getScore();
            }
            for (int i = 0; i < proveQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 4, proveQuestion.get(i).getQuestionId()));
                totalScore += proveQuestion.get(i).getScore();
            }

            examManage.setTotalScore(totalScore);
            examManageService.update(examManage);
            return ApiResultHandler.buildApiResult(200, "试卷生成成功！", null);
        } catch (Exception e) {
            ///出现异常回滚
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(500, "试卷生成成功失败,服务器出现未知异常！", null);
        }
    }


    @PostMapping(name = "添加考试并且生成试卷", value = "/paper/addExamAndPaper")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ApiResult addExamAndPaper(@RequestBody ExamAndPaper examAndPaper) {
        //TODO 一些校验工作，前端已经做了，所以这里没有做
        //定义事务锚点

        return ApiResultHandler.buildApiResult(0, "接口暂停开发！", null);

        /**
         Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
         try{
         ExamManage examManage = examAndPaper.getExamManage();
         int paperId;
         synchronized (PaperService.class){
         //获取最近一次试卷编号
         int onlyPaperId = paperService.findOnlyPaperId ();
         //计算出新的试卷编号
         paperId = onlyPaperId+1;
         examManage.setPaperId (paperId);
         }
         //设置好试卷编号后加入数据库
         examManageService.add(examManage);
         String section = examAndPaper.getSection();
         String subject = examAndPaper.getSubject();
         //然后开始生成试卷
         List<MultiQuestion> multiQuestions = multiQuestionService.findBySectionAndSubject(section, subject);
         List<FillQuestion> fillQuestions = fillQuestionService.findBySectionAndSubject(section, subject);
         List<CalQuestion> calQuestions = calQuestionService.findBySectionAndSubject(section, subject);
         List<ProveQuestion> proveQuestions = proveQuestionService.findBySectionAndSubject(section, subject);
         //如果题目数量不足
         if(multiQuestions.size()< examAndPaper.getSize1()||
         fillQuestions.size() < examAndPaper.getSize2()||
         calQuestions.size()  < examAndPaper.getSize3()||
         proveQuestions.size()< examAndPaper.getSize4()){
         return ApiResultHandler.buildApiResult(415,"题库题目数量不足"+
         "选择题个数为"+examAndPaper.getSize1()+
         "填空题个数为"+examAndPaper.getSize2()+
         "计算题个数为"+examAndPaper.getSize3()+
         "证明题个数为"+examAndPaper.getSize4()+
         "。"+"请检查题目数量并重新提交。"
         ,null);
         }
         pickQuestionRandomly(multiQuestions.size(), examAndPaper.getSize1(), multiQuestions);
         }catch (Exception e){
         ///出现异常回滚
         TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
         return ApiResultHandler.buildApiResult(500, "服务器出现未知异常！", null);
         }
         return ApiResultHandler.buildApiResult(0, "考试添加成功！", null);
         */
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    public static int[] randomCommon(int min, int max, int n) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count + 1 < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    /**
     * 选择题随机抽
     *
     * @param questionSize 题目总数
     * @param pickSize     抽几个
     * @param questions    题目list
     * @return
     */
    public List<MultiQuestion> pickQuestionRandomly1(int questionSize, int pickSize, List<MultiQuestion> questions) {
        int[] randomNums = randomCommon(0, questionSize - 1, pickSize);
        List<MultiQuestion> pickedQuestions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < randomNums.length; i++) {
            pickedQuestions.add(questions.get(randomNums[i]));
        }
        return pickedQuestions;
    }

    public List<FillQuestion> pickQuestionRandomly2(int questionSize, int pickSize, List<FillQuestion> questions) {
        int[] randomNums = randomCommon(0, questionSize - 1, pickSize);
        List<FillQuestion> pickedQuestions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < randomNums.length; i++) {
            pickedQuestions.add(questions.get(randomNums[i]));
        }
        return pickedQuestions;
    }

    public List<CalQuestion> pickQuestionRandomly3(int questionSize, int pickSize, List<CalQuestion> questions) {
        int[] randomNums = randomCommon(0, questionSize - 1, pickSize);
        List<CalQuestion> pickedQuestions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < randomNums.length; i++) {
            pickedQuestions.add(questions.get(randomNums[i]));
        }
        return pickedQuestions;
    }

    public List<ProveQuestion> pickQuestionRandomly4(int questionSize, int pickSize, List<ProveQuestion> questions) {
        int[] randomNums = randomCommon(0, questionSize - 1, pickSize);
        List<ProveQuestion> pickedQuestions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < randomNums.length; i++) {
            pickedQuestions.add(questions.get(randomNums[i]));
        }
        return pickedQuestions;
    }

}
