package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.Result;
import com.library.dto.CategoryDTO;
import com.library.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 查询所有分类
     */
    Result<?> getCategoryList(CategoryDTO queryDTO);

    /**
     * 新增分类
     */
    Result<?> addCategory(CategoryDTO dto);

    /**
     * 修改分类
     */
    Result<?> updateCategory(CategoryDTO dto);

    /**
     * 删除分类 - 注意这个方法名和参数
     */
    Result<?> deleteCategory(Long id);
}