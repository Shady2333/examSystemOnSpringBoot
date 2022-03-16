package com.exam.vo;

import lombok.Data;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2021/2/22 18:17
 */
@Data
public class ExamScoreVo {
    private Integer examCode;

    private Integer studentId;

    private String studentName;

    private String subject;

    private Integer score;

    private Integer scoreId;

    private String answerDate;
}
