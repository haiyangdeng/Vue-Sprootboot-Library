package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询指定分类下的图书数量
     * 用于删除分类前的校验
     */
    @Select("SELECT COUNT(*) FROM sys_book WHERE category_id = #{categoryId}")
    Integer countBooksByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * (可选) 根据分类名称模糊查询
     * 虽然 BaseMapper 有 selectList，但有时手写 SQL 更直观
     */
    @Select("SELECT * FROM sys_category WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Category> selectByName(@Param("name") String name);
}