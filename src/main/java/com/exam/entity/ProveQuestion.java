package com.exam.entity;

import lombok.Data;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/8/10
 */
//和填空题和计算题结构相同
@Data
public class ProveQuestion extends Question {
    private Integer questionId;

    private String subject;

    private String level;

    private String section;

    private String analysis; //
}
