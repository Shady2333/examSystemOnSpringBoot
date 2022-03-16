package com.exam.service;

import com.exam.entity.Score;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScoreService {
    int add(Score score);

    List<Score> findAll();

    List<Score> findById(Integer studentId);

    //List<Score> findByExamCode(Integer examCode);


    //判断学生是否参加过考试
    Boolean is_marked(String examCode,String studentId);


    //获取所有未打分的 考试ID 和 学生ID
    List<Score> getToBeMarkedScore();


    //获取某个学生某场考试的成绩通过examCode和studentId
    Score getScoreByExamCodeAndStudentID(String examCode,String studentId);

    int updateScoreByExamCodeAndStudentId(String score,String examCode,String studentId);

    int setIsMarked(@Param("examCode")String examCode,@Param("studentId")String studentId);

    List<Score> selectScoresByExamCode(@Param("examCode")String examCode);

    Boolean deleteScoreById(String scoreId);

}
