package com.exam.serviceimpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.FillQuestion;
import com.exam.entity.Question;
import com.exam.mapper.FillQuestionMapper;
import com.exam.service.FillQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FillQuestionServiceImpl implements FillQuestionService {

    @Autowired
    private FillQuestionMapper fillQuestionMapper;

    @Override
    public List<FillQuestion> findAll() {
        return fillQuestionMapper.findAll();
    }

    @Override
    public List<FillQuestion> findByIdAndType(Integer paperId) {
        return fillQuestionMapper.findByIdAndType(paperId);
    }

    @Override
    public IPage<FillQuestion> findAll(Page<FillQuestion> page) {
        return fillQuestionMapper.findAll(page);
    }

    @Override
    public int delete(Integer id) {
        return fillQuestionMapper.delete(id);
    }

    @Override
    public FillQuestion findOnlyQuestionId() {
        return fillQuestionMapper.findOnlyQuestionId();
    }

    @Override
    public int add(FillQuestion fillQuestion) {
        return fillQuestionMapper.add(fillQuestion);
    }

    @Override
    public List<Integer> findBySubject(String subject, Integer pageNo) {
        return fillQuestionMapper.findBySubject(subject,pageNo);
    }

    @Override
    public List<FillQuestion> findBySection(String section) {
        return fillQuestionMapper.findBySection(section);
    }

    @Override
    public List<FillQuestion> findBySectionAndSubject(String section, String subject) {
        return fillQuestionMapper.findBySectionAndSubject(section,subject);
    }

    @Override
    public FillQuestion findById(String questionId) {
        return fillQuestionMapper.findById(questionId);
    }
}
