package com.library.controller;

import com.library.common.Result;
import com.library.common.annotation.Log;
import com.library.dto.CategoryDTO;
import com.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public Result<?> getCategoryList(CategoryDTO queryDTO) {
        return categoryService.getCategoryList(queryDTO);
    }

    /**
     * 新增分类（管理员）
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("新增分类")
    public Result<?> addCategory(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.addCategory(categoryDTO);
    }

    /**
     * 修改分类（管理员）
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("修改分类")
    public Result<?> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(categoryDTO);
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Log("删除分类")
    public Result<?> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}