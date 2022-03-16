package com.exam.vo;

import lombok.Data;

/**
 * 老师批改结果模型
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/12/21
 */
@Data
public class MarkVO {
    /**
     * 题目ID
     */
    private String questionId;
    /**
     * 题目类型
     */
    private String questionType;
    /**
     * 题干
     */
    private String question;
    /**
     * 学生答案
     */
    private String studentAnswer;
    /**
     * 正确答案
     */
    private String rightAnswer;
    /**
     * 分数，可能为空
     */
    private String score;
    /**
     * 满分是多少
     */
    private String fullScore;
}
