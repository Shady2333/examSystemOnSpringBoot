package com.exam.controller.frameController;

import com.exam.entity.Score;
import com.exam.entity.Student;
import com.exam.service.StudentService;
import com.exam.serviceimpl.ScoreServiceImpl;
import com.exam.util.CookieUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 控制视图跳转的一些方法
 *
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/6/18
 */
@Controller
public class FrameController {


    @Autowired
    private ScoreServiceImpl scoreService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/studentsPage")
    public String studentsPage() {
        return "teacher/student/main";
    }

    @GetMapping("/multiQuestionsPage")
    public String multiQuestionsPage() {
        return "teacher/question/MultiQuestion";
    }

    @GetMapping("/fillQuestionsPage")
    public String fillQuestionsPage() {
        return "teacher/question/FillQuestion";
    }

    @GetMapping("/calQuestionsPage")
    public String calQuestionsPage() {
        return "teacher/question/CalQuestion";
    }

    @GetMapping("/proveQuestionsPage")
    public String proveQuestionsPage() {
        return "teacher/question/ProveQuestion";
    }

    @GetMapping("/addMultiQuestionPage")
    public String addMultiQuestionPage() {
        return "teacher/addQuestion/addMultiQuestion";
    }

    @GetMapping("/addFillQuestionPage")
    public String addFillQuestionPage() {
        return "teacher/addQuestion/addFillQuestion";
    }

    @GetMapping("/addCalQuestionPage")
    public String addCalQuestionPage() {
        return "teacher/addQuestion/addCalQuestion";
    }
    @GetMapping("/addProveQuestionPage")
    public String addProveQuestionPage() {
        return "teacher/addQuestion/addProveQuestion";
    }

    @GetMapping("/teacherIndex")
    public String toTeacherDashboardPage() {
        return "teacher/dashboard";
    }

    @GetMapping("/studentIndex")
    public String toStudentDashboardPage() {
        return "student/dashboard";
    }

    @GetMapping("/toExamPage")
    public String toExamPage() {
        return "teacher/exam/main";
    }

    @GetMapping("/toStudentExamPage")
    public String toStudentExamPage() {
        return "student/exam/list";
    }


    @GetMapping("/toStudentExamGrade")
    public String toStudentExamGrade() {
        return "student/exam/grade";
    }

    @GetMapping("/toExamingPage/{id}")
    public String toExamingPage(@PathVariable("id") String examCode, HttpSession session, Model model) {

        if(session.getAttribute("id")==null){
            model.addAttribute("errorMsg","session过期请重新登录");
            return "login";
        }

        //session中保存在正在考试的id
        session.setAttribute("examCode", examCode);
        //取出学生ID
        String id = String.valueOf(session.getAttribute("id"));

        model.addAttribute("examCode", examCode);

        System.out.println(examCode);

        Score score = scoreService.getScoreByExamCodeAndStudentID(examCode, id);

        //如果数据库里有已经考试的信息
        if (score != null) {
            //如果已经参加过考试了
            if (scoreService.is_marked(examCode, id)) {
                //获取分数返回
                model.addAttribute("msg", "已经参加过这个考试了成绩为:" + score.getScore());
            } else {
                model.addAttribute("msg", "已经参加过这个考试了但是老师未批改");
            }
            return "student/exam/joined";
        }
        //否则就还没考过这个试卷
        return "student/exam/exam";
    }

    @GetMapping("/toAddExamPage")
    public String toAddExamPage() {
        return "teacher/exam/addExam";
    }

    @GetMapping("/toExamScoresPage/{examCode}")
    public String toExamScoresPage(@PathVariable("examCode")String examCode,Model model) {
        model.addAttribute("examCode",examCode);
        return "teacher/exam/scores";
    }

    @GetMapping(name = "跳转到个人完善信息页面",value = "/toStudentInfoPage")
    public String toStudentInfoPage(HttpSession session,Model model){
        Object id = session.getAttribute("id");
        Student student = studentService.findById((Integer) id);
        model.addAttribute("student",student);
        return "student/studentInfo";
    }

    @RequestMapping(name = "判断session里的id是否为空",value = "/judgeSession",method = RequestMethod.GET)
    @ResponseBody
    public String judgeSession(HttpServletRequest request, HttpServletResponse response,HttpSession session){
        String id = CookieUtil.getCookieValue(request, "id");
        //Object id = session.getAttribute("id");
        if(id==null||"".equals(id)){
            return "false";
        }
        return "true";
    }


}
