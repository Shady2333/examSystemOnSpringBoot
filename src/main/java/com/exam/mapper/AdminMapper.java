package com.exam.mapper;

import com.exam.entity.Admin;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
/*  @Mapper注解
1.使用@Mapper将AdminMapper接口交给Spring进行管理

2.不用写Mapper映射文件（XML）

3.为这个AdminMapper接口生成一个实现类，让别的类进行引用*/
/*
*  @Repository需要在Spring中配置扫描地址，然后生成Dao层的Bean才能被注入到Service层中。

 @Mapper不需要配置扫描地址，通过xml里面的namespace里面的接口地址，生成了Bean后注入到Service层中。
* */
@Mapper
@Repository
public interface AdminMapper {

    @Select("select adminName,sex,tel,email,cardId,role from admin")
    public List<Admin> findAll();

    @Select("select adminName,sex,tel,email,cardId,role from admin where adminId = #{adminId}")
    public Admin findById(Integer adminId);

    @Delete("delete from admin where adminId = #{adminId}")
    public int deleteById(int adminId);

    @Update("update admin set adminName = #{adminName},sex = #{sex}," +
            "tel = #{tel}, email = #{email},pwd = #{pwd},cardId = #{cardId},role = #{role} where adminId = #{adminId}")
    public int update(Admin admin);

    @Options(useGeneratedKeys = true,keyProperty = "adminId")
    @Insert("insert into admin(adminName,sex,tel,email,pwd,cardId,role) " +
            "values(#{adminName},#{sex},#{tel},#{email},#{pwd},#{cardId},#{role})")
    public int add(Admin admin);
}
