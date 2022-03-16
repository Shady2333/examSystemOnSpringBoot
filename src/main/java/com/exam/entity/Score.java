package com.exam.entity;

import lombok.Data;

@Data
public class Score {
    private Integer examCode;

    private Integer studentId;

    private String subject;

    private Integer score;

    private Integer scoreId;

    private String answerDate;

    /**
     * 是否打过分数
     */
    private Boolean is_marked;
}