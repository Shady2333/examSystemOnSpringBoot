package com.exam.serviceimpl;

import com.exam.entity.Score;
import com.exam.mapper.ScoreMapper;
import com.exam.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;

    @Autowired
    public ScoreServiceImpl(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }

    @Override
    public int add(Score score) {
        return scoreMapper.add(score);
    }

    @Override
    public List<Score> findAll() {
        return scoreMapper.findAll();
    }

    @Override
    public List<Score> findById(Integer studentId) {
        return scoreMapper.findById(studentId);
    }

//    @Override
//    public List<Score> findByExamCode(Integer examCode) {
//        return scoreMapper.findByExamCode(examCode);
//    }



    /**
     * 判断学生是否参加过某场考试
     * @param examCode
     * @param studentId
     * @return
     */
    @Override
    public Boolean is_marked(String examCode, String studentId) {
        return scoreMapper.is_marked(examCode,studentId);
    }

    /**
     * 获取所有未打分的 考试ID 和 学生ID
     * @return
     */
    @Override
    public List<Score> getToBeMarkedScore() {
        return scoreMapper.toBeMarkedScore();
    }

    /**
     * 获取某个学生某场考试的成绩通过examCode和studentId
     * @param examCode
     * @param studentId
     * @return
     */
    @Override
    public Score getScoreByExamCodeAndStudentID(String examCode, String studentId) {
        return scoreMapper.getScoreByExamCodeAndStudentID(examCode,studentId);
    }

    /**
     * 更新分数
     * @param score
     * @param examCode
     * @param studentId
     * @return
     */
    @Override
    public int updateScoreByExamCodeAndStudentId(String score,String examCode, String studentId) {
        return scoreMapper.updateScoreByExamCodeAndStudentId(score,examCode,studentId);
    }

    /**
     * 设置为已经批阅过
     * @param examCode
     * @param studentId
     * @return
     */
    @Override
    public int setIsMarked(String examCode, String studentId) {
        return scoreMapper.setIsMarked(examCode,studentId);
    }

    @Override
    public List<Score> selectScoresByExamCode(String examCode) {
        return scoreMapper.selectScoresByExamCode(examCode);
    }

    /**
     * 通过id删除分数
     * @param scoreId
     * @return
     */
    @Override
    public Boolean deleteScoreById(String scoreId) {
        return scoreMapper.deleteScoreById(scoreId);
    }
}
