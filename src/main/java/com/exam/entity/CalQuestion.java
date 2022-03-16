package com.exam.entity;

import lombok.Data;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/8/10
 */
@Data
public class CalQuestion extends Question {
    private Integer questionId; //唯一ID 计算题表中

    private String subject;  //考试科目


    private String level;    //难度

    private String section;  //属于哪一个章节

    private String analysis; //分析
}
