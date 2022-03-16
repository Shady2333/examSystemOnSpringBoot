package com.exam.entity;

import lombok.Data;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/12/19
 */
@Data
public class Answer {

    private String studentId;  //学生ID

    private String examCode;    //考试ID

    private String questionId;  //题目ID

    private String questionType;//题目类型

    private String studentAnswer;//学生的答案

    private String comment;     //老师评论

    private String finalScore;  //本题最终得分

}
