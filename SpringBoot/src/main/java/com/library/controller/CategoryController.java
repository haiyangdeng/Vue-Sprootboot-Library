package com.library.controller;

import com.library.common.Result;
import com.library.entity.Category;
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
    public Result<?> getCategoryList() {
        return categoryService.getCategoryList();
    }

    /**
     * 新增分类（管理员）
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    /**
     * 修改分类（管理员）
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}