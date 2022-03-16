package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.CalQuestion;
import com.exam.entity.FillQuestion;
import com.exam.entity.ProveQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/8/10
 */
public interface ProveQuestionService {
    List<ProveQuestion> findByIdAndType(Integer paperId);

    IPage<ProveQuestion> findAll(Page<ProveQuestion> page);

    List<ProveQuestion> findAll();

    ProveQuestion findOnlyQuestionId();

    int add(ProveQuestion ProveQuestion);

    List<Integer> findBySubject(String subject,Integer pageNo);

    List<ProveQuestion> findBySection(String section);

    int delete(Integer id);

    List<ProveQuestion> findBySectionAndSubject(String section, String subject);

    ProveQuestion findById(String questionId);
}
