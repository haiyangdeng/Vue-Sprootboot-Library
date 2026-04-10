package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询指定分类下的图书数量
     */
    @Select("SELECT COUNT(*) FROM sys_book WHERE category_id = #{categoryId}")
    Integer countBooksByCategoryId(Long categoryId);
}