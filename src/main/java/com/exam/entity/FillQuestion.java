package com.exam.entity;

import lombok.Data;

//填空题实体类
@Data
public class FillQuestion extends Question {
    private Integer questionId;

    private String subject;


    private String level;

    private String section;

    private String analysis; //题目解析

}
