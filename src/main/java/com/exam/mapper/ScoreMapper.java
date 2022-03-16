package com.exam.mapper;

import com.exam.entity.Score;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ScoreMapper {
    /**
     * @param score 添加一条成绩记录
     * @return
     */
    @Options(useGeneratedKeys = true,keyProperty = "scoreId")
    @Insert("insert into score(examCode,studentId,subject,score,answerDate) values(#{examCode},#{studentId},#{subject},#{score},#{answerDate})")
    int add(Score score);

    @Select("select scoreId,examCode,studentId,subject,score,answerDate from score")
    List<Score> findAll();

    @Select("select scoreId,examCode,studentId,subject,score,answerDate from score where studentId = #{studentId}")
    List<Score> findById(Integer studentId);

//    /**
//     *
//     * @return 查询每位学生的学科分数。 max其实是假的，为了迷惑老师，达到一次考试考生只参加了一次的效果
//     */
//    @Select("select max(etScore) as etScore from score where examCode = #{examCode} group by studentId")
//    List<Score> findByExamCode(Integer examCode);

    //判断某个人是否打分了
    @Select("select is_marked from score where examCode = #{examCode} and studentId=#{studentId}")
    Boolean is_marked(@Param("examCode")String examCode,@Param("studentId")String studentId);

    //获取所有未打分的 考试ID 和 学生ID
    @Select("select * from score where is_marked = \"0\"")
    List<Score> toBeMarkedScore();

    //获取某个学生某场考试的成绩通过examCode和studentId
    @Select("select * from score where examCode = #{examCode} and studentId=#{studentId}")
    Score getScoreByExamCodeAndStudentID(@Param("examCode")String examCode,@Param("studentId")String studentId);

    @Update("update score set score=#{score} where examCode=#{examCode} and studentId=#{studentId}")
    int updateScoreByExamCodeAndStudentId(@Param("score")String score,@Param("examCode")String examCode,@Param("studentId")String studentId);

    @Update("update score set is_marked= true where examCode=#{examCode} and studentId=#{studentId}")
    int setIsMarked(@Param("examCode")String examCode,@Param("studentId")String studentId);

    @Select("select * from score where examCode = #{examCode}")
    List<Score> selectScoresByExamCode(@Param("examCode")String examCode);

    @Delete("delete from score where scoreId = #{scoreId}")
    Boolean deleteScoreById(@Param("scoreId")String scoreId);
}
