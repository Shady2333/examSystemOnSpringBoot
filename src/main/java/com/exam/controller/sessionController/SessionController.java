package com.exam.controller.sessionController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/11/20
 */
@RestController
@RequestMapping("/session")
public class SessionController {

    /**
     * 放弃使用
     * @param session
     * @return
     */
    @GetMapping("/getExamingExamCode")
    public String getExamingExamCode(HttpSession session){
        //方便前后端分离获取正在考试的号码
        Object examID = session.getAttribute("examCode");
        return (String) examID;
    }
}
