package com.exam.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2021/1/12 13:35
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RandomGenerate {
    private int examCode;
    private int size1;
    private int size2;
    private int size3;
    private int size4;
    private int section;
    private int subject;
}
