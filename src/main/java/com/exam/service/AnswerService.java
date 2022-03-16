package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Answer;
import com.exam.vo.AnswerVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnswerService {

    IPage<AnswerVO> findAll(Page<AnswerVO> page);

    int add(Answer answer);

    List<Answer> getAnswerByExamCodeAndStudentId(String examCode, String studentId);

    int updateFinalScoreBy(String score,
                           String studentId,
                           String examCode,
                           String questionId,
                           String questionType);

}
