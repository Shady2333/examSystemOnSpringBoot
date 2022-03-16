package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.CalQuestion;
import com.exam.entity.FillQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/8/10
 */
public interface CalQuestionService {
    List<CalQuestion> findByIdAndType(Integer paperId);

    IPage<CalQuestion> findAll(Page<CalQuestion> page);

    List<CalQuestion> findAll();

    CalQuestion findOnlyQuestionId();

    int delete(Integer id);

    int add(CalQuestion calQuestion);

    List<Integer> findBySubject(String subject,Integer pageNo);

    List<CalQuestion> findBySection(String section);

    List<CalQuestion> findBySectionAndSubject(String section, String subject);

    CalQuestion findById(String questionId);
}
