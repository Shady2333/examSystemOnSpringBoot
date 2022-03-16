package com.exam.vo;

import lombok.Data;

/**
 * 学生提交的试卷概览
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/12/20
 */
@Data
public class PaperVO {

    private String studentId;

    private String studentName;

    private String examCode;

    private String subject;
    /**
     * 日期
     */
    private String answerDate;

}
