package com.exam.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.FillQuestion;
import com.exam.entity.MultiQuestion;
import com.exam.entity.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

//填空题
@Mapper
public interface FillQuestionMapper {

    @Select("select * from fill_question where questionId in (select questionId from paper_manage where questionType = 2 and paperId = #{paperId})")
    List<FillQuestion> findByIdAndType(Integer paperId);

    @Select("select * from fill_question")
    IPage<FillQuestion> findAll(Page page);

    @Select("select * from fill_question")
    List<FillQuestion> findAll();

    @Delete("delete from fill_question where questionId=#{id}")
    int delete(Integer id);

    /**
     * 查询最后一条questionId
     * @return FillQuestion
     */
    @Select("select questionId from fill_question order by questionId desc limit 1")
    FillQuestion findOnlyQuestionId();

    @Options(useGeneratedKeys = true,keyProperty ="questionId" )
    @Insert("insert into fill_question(subject,question,answer,analysis,score,level,section) values " +
            "(#{subject,},#{question},#{answer},#{analysis},#{score},#{level},#{section})")
    int add(FillQuestion fillQuestion);

    @Select("select questionId from fill_question where subject = #{subject} order by rand() desc limit #{pageNo}")
    List<Integer> findBySubject(String subject,Integer pageNo);


    @Select("select * from fill_question where section=#{section}")
    List<FillQuestion> findBySection(String section);

    /**
     * 通过 单元和科目选择
     * @param section
     * @param subject
     * @return
     */
    @Select("select * from fill_question where section=#{section} and subject=#{subject}")
    List<FillQuestion> findBySectionAndSubject(@Param("section") String section, @Param("subject") String subject);

    @Select("select * from fill_question where questionId =#{questionId}")
    FillQuestion findById(String questionId);
}
