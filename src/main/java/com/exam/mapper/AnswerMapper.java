package com.exam.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Answer;
import com.exam.entity.CalQuestion;
import com.exam.vo.AnswerVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface AnswerMapper {
    @Select("select question, subject, score, section,level, \"选择题\" as type from multi_question " +
            "union select  question, subject, score, section,level, \"判断题\" as type  from judge_question " +
            "union select  question, subject, score, section,level, \"填空题\" as type from fill_question")
    IPage<AnswerVO> findAll(Page page);

    @Insert("insert into answer(studentId,examCode,questionId,questionType,studentAnswer,comment,finalScore) values " +
            "(#{studentId},#{examCode},#{questionId},#{questionType},#{studentAnswer},#{comment},#{finalScore})")
    int add(Answer answer);


    @Select("select * from answer where examCode = #{examCode} and studentId = #{studentId}")
    List<Answer> getAnswerByExamCodeAndStudentId(@Param("examCode")String examCode,@Param("studentId")String studentId);

    @Update("update answer set finalScore = #{score} where studentId = #{studentId} and examCode=#{examCode} and questionId=#{questionId} and questionType=#{questionType} ")
    int updateFinalScoreBy(@Param("score")String score,
                           @Param("studentId")String studentId,
                           @Param("examCode")String examCode,
                           @Param("questionId")String questionId,
                           @Param("questionType")String questionType);
}
