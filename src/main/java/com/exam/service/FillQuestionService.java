package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.FillQuestion;
import com.exam.entity.Question;

import java.util.List;

public interface FillQuestionService {

    List<FillQuestion> findByIdAndType(Integer paperId);

    IPage<FillQuestion> findAll(Page<FillQuestion> page);

    List<FillQuestion> findAll();

    FillQuestion findOnlyQuestionId();

    int add(FillQuestion fillQuestion);

    int delete(Integer id);

    List<Integer> findBySubject(String subject,Integer pageNo);

    List<FillQuestion> findBySection(String section);

    List<FillQuestion> findBySectionAndSubject(String section, String subject);

    FillQuestion findById(String questionId);
}
