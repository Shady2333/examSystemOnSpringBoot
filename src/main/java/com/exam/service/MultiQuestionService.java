package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.MultiQuestion;
import com.exam.entity.Question;

import java.util.List;

public interface MultiQuestionService {

    List<MultiQuestion> findByIdAndType(Integer PaperId);

    IPage<MultiQuestion> findAll(Page<MultiQuestion> page);

    List<MultiQuestion> findAll();

    int deleteById(Integer id);

    MultiQuestion findOnlyQuestionId();

    int add(MultiQuestion multiQuestion);

    List<Integer> findBySubject(String subject,Integer pageNo);

    List<MultiQuestion> findBySection(String section);

    /**
     * 通过 单元和科目 查询题目
     * @param section
     * @param subject
     * @return
     */
    List<MultiQuestion> findBySectionAndSubject(String section, String subject);

    MultiQuestion findById(String questionId);
}
