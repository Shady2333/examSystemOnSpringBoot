package com.exam.serviceimpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.CalQuestion;
import com.exam.entity.FillQuestion;
import com.exam.mapper.CalQuestionMapper;
import com.exam.mapper.FillQuestionMapper;
import com.exam.service.CalQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/7/18
 */
@Service
public class CalQuestionServiceImpl implements CalQuestionService {
    @Autowired
    private CalQuestionMapper calQuestionMapper;

    @Override
    public List<CalQuestion> findByIdAndType(Integer paperId) {
        return calQuestionMapper.findByIdAndType(paperId);
    }

    @Override
    public IPage<CalQuestion> findAll(Page<CalQuestion> page) {
        return calQuestionMapper.findAll(page);
    }

    @Override
    public CalQuestion findOnlyQuestionId() {
        return calQuestionMapper.findOnlyQuestionId();
    }

    @Override
    public int add(CalQuestion calQuestion) {
        return calQuestionMapper.add(calQuestion);
    }

    @Override
    public List<CalQuestion> findAll() {
        return calQuestionMapper.findAll();
    }

    @Override
    public List<Integer> findBySubject(String subject, Integer pageNo) {
        return calQuestionMapper.findBySubject(subject,pageNo);
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @Override
    public int delete(Integer id) {
        return calQuestionMapper.delete(id);
    }

    @Override
    public List<CalQuestion> findBySection(String section) {
        return calQuestionMapper.findBySection(section);
    }

    @Override
    public List<CalQuestion> findBySectionAndSubject(String section, String subject) {
        return calQuestionMapper.findBySectionAndSubject(section,subject);
    }

    @Override
    public CalQuestion findById(String questionId) {
        return calQuestionMapper.findById(questionId);
    }
}
