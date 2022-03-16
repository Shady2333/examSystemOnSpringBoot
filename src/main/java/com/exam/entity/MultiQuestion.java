package com.exam.entity;

import lombok.Data;

// 选择题实体
@Data
public class MultiQuestion extends Question {
    private Integer questionId;

    private String subject;

    private String section;

    private String answerA;

    private String answerB;

    private String answerC;

    private String answerD;

    private String level;

    private String rightAnswer;

    private String analysis; //题目解析


    @Override
    public String getAnswer() {
        return rightAnswer;
    }
}