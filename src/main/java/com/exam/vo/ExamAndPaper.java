package com.exam.vo;

import com.exam.entity.ExamManage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2021/1/8 11:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamAndPaper{
    private ExamManage examManage;
    private int size1;
    private int size2;
    private int size3;
    private int size4;
    private String section;
    private String subject;
}
