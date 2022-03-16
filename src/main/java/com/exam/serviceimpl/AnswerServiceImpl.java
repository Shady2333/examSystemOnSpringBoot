package com.exam.serviceimpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Answer;
import com.exam.mapper.AnswerMapper;
import com.exam.service.AnswerService;
import com.exam.vo.AnswerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public IPage<AnswerVO> findAll(Page<AnswerVO> page) {
        return answerMapper.findAll(page);
    }


    //添加一个题目答案
    @Override
    public int add(Answer answer) {
        return answerMapper.add(answer);
    }

    //查看待批改试卷的所有题目
    @Override
    public List<Answer> getAnswerByExamCodeAndStudentId(String examCode, String studentId) {
        return answerMapper.getAnswerByExamCodeAndStudentId(examCode,studentId);
    }

    @Override
    public int updateFinalScoreBy(String score, String studentId, String examCode, String questionId, String questionType) {
        return answerMapper.updateFinalScoreBy(score,studentId,examCode,questionId,questionType);
    }
}
