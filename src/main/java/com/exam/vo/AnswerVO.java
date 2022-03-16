package com.exam.vo;

import lombok.Data;

/**
 * 学生答案的视图模型
 * @author Zuhai Chen
 */
@Data
public class AnswerVO {
    private String question;
    private String subject;
    private String score;
    private String section;
    private String level;
    private String type;
}
