package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
import com.library.mapper.CategoryMapper;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    @Autowired
    private BookMapper bookMapper;

    /**
     * 查询所有分类（含图书数量）
     */
    public Result<?> getCategoryList() {
        List<Category> list = this.list(
                Wrappers.<Category>lambdaQuery().orderByAsc(Category::getCreateTime)
        );

        // 查询每个分类下的图书数量
        list.forEach(category -> {
            Integer count = baseMapper.countBooksByCategoryId(category.getId());
            category.setBookCount(count);
        });

        return Result.success(list);
    }

    /**
     * 新增分类
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addCategory(Category category) {
        // 校验分类名称唯一
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Category::getName, category.getName());
        if (this.count(wrapper) > 0) {
            return Result.error("分类名称已存在");
        }

        this.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 修改分类
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateCategory(Category category) {
        Category existing = this.getById(category.getId());
        if (existing == null) {
            return Result.error(404, "分类不存在");
        }

        // 校验分类名称唯一（排除自身）
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Category::getName, category.getName());
        wrapper.ne(Category::getId, category.getId());
        if (this.count(wrapper) > 0) {
            return Result.error("分类名称已存在");
        }

        this.updateById(category);
        return Result.success("修改分类成功");
    }

    /**
     * 删除分类
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteCategory(Long id) {
        Category category = this.getById(id);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        // 检查分类下是否有图书
        LambdaQueryWrapper<Book> bookWrapper = Wrappers.lambdaQuery();
        bookWrapper.eq(Book::getCategoryId, id);
        long count = bookMapper.selectCount(bookWrapper);
        if (count > 0) {
            return Result.error("该分类下存在" + count + "本图书，无法删除");
        }

        this.removeById(id);
        return Result.success("删除分类成功");
    }
}

