package com.exam.serviceimpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.ProveQuestion;
import com.exam.mapper.ProveQuestionMapper;
import com.exam.service.ProveQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/7/18
 */
@Service
public class ProveQuestionServiceImpl implements ProveQuestionService {

    @Autowired
    ProveQuestionMapper proveQuestionMapper;
    @Override
    public List<ProveQuestion> findByIdAndType(Integer paperId) {
        return proveQuestionMapper.findByIdAndType(paperId);
    }

    @Override
    public IPage<ProveQuestion> findAll(Page<ProveQuestion> page) {
        return proveQuestionMapper.findAll(page);
    }

    @Override
    public List<ProveQuestion> findAll() {
        return proveQuestionMapper.findAll();
    }

    @Override
    public ProveQuestion findOnlyQuestionId() {
        return proveQuestionMapper.findOnlyQuestionId();
    }

    @Override
    public int add(ProveQuestion ProveQuestion) {
        return proveQuestionMapper.add(ProveQuestion);
    }

    @Override
    public List<Integer> findBySubject(String subject, Integer pageNo) {
        return proveQuestionMapper.findBySubject(subject,pageNo);
    }

    @Override
    public List<ProveQuestion> findBySection(String section) {
        return proveQuestionMapper.findBySection(section);
    }

    @Override
    public int delete(Integer id) {
        return proveQuestionMapper.delete(id);
    }

    @Override
    public List<ProveQuestion> findBySectionAndSubject(String section, String subject) {
        return proveQuestionMapper.findBySectionAndSubject(section,subject);
    }

    @Override
    public ProveQuestion findById(String questionId) {
        return proveQuestionMapper.findById(questionId);
    }
}
