package com.exam.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.CalQuestion;
import com.exam.entity.FillQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Zuhai Chen
 * @version 1./10
 * @date 2020/
 */
@Mapper
public interface CalQuestionMapper {
    @Select("select * from cal_question where questionId in (select questionId from paper_manage where questionType = 3 and paperId = #{paperId})")
    List<CalQuestion> findByIdAndType(Integer paperId);

    @Select("select * from cal_question")
    IPage<CalQuestion> findAll(Page page);

    @Select("select * from cal_question")
    List<CalQuestion> findAll();

    @Delete("delete from cal_question where questionId=#{id}")
    int delete(Integer id);

    /**
     * 查询最后一条questionId
     * @return FillQuestion
     */
    @Select("select questionId from cal_question order by questionId desc limit 1")
    CalQuestion findOnlyQuestionId();

    @Options(useGeneratedKeys = true,keyProperty ="questionId" )
    @Insert("insert into cal_question(subject,question,answer,analysis,score,level,section) values " +
            "(#{subject,},#{question},#{answer},#{analysis},#{score},#{level},#{section})")
    int add(CalQuestion calQuestion);

    @Select("select questionId from cal_question where subject = #{subject} order by rand() desc limit #{pageNo}")
    List<Integer> findBySubject(String subject,Integer pageNo);

    @Select("select * from cal_question where section=#{section}")
    List<CalQuestion> findBySection(String section);

    /**
     * 通过 单元和科目选择
     * @param section
     * @param subject
     * @return
     */
    @Select("select * from cal_question where section=#{section} and subject=#{subject}")
    List<CalQuestion> findBySectionAndSubject(@Param("section") String section,@Param("subject") String subject);


    @Select("select * from cal_question where questionId =#{questionId}")
    CalQuestion findById(String questionId);
}
