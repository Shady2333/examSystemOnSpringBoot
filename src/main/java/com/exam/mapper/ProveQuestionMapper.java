package com.exam.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.CalQuestion;
import com.exam.entity.FillQuestion;
import com.exam.entity.MultiQuestion;
import com.exam.entity.ProveQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/7/18
 */
@Mapper
public interface ProveQuestionMapper {
    @Select("select * from prove_question where questionId in (select questionId from paper_manage where questionType = 4 and paperId = #{paperId})")
    List<ProveQuestion> findByIdAndType(Integer paperId);

    @Select("select * from prove_question")
    IPage<ProveQuestion> findAll(Page page);

    @Select("select * from prove_question")
    List<ProveQuestion> findAll();

    @Delete("delete  from prove_question where questionId=#{id}")
    int deleteById(int id);

    /**
     * 查询最后一条questionId
     * @return FillQuestion
     */
    @Select("select questionId from prove_question order by questionId desc limit 1")
    ProveQuestion findOnlyQuestionId();

    @Options(useGeneratedKeys = true,keyProperty ="questionId" )
    @Insert("insert into prove_question(subject,question,answer,analysis,score,level,section) values " +
            "(#{subject,},#{question},#{answer},#{analysis},#{score},#{level},#{section})")
    int add(ProveQuestion proveQuestion);

    @Select("select questionId from prove_question where subject = #{subject} order by rand() desc limit #{pageNo}")
    List<Integer> findBySubject(String subject,Integer pageNo);

    @Select("select * from prove_question where section=#{section}")
    List<ProveQuestion> findBySection(String section);

    @Delete("delete from prove_question where questionId=#{id}")
    int delete(Integer id);

    /**
     * 通过 单元和科目选择
     * @param section
     * @param subject
     * @return
     */
    @Select("select * from prove_question where section=#{section} and subject=#{subject}")
    List<ProveQuestion> findBySectionAndSubject(@Param("section") String section,@Param("subject") String subject);


    @Select("select * from prove_question where questionId =#{questionId}")
    ProveQuestion findById(String questionId);
}
