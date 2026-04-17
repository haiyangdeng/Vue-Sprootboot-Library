package com.library.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.Result;
import com.library.dto.CategoryDTO;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
import com.library.mapper.CategoryMapper;
import com.library.service.CategoryService;
import com.library.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public Result<?> getCategoryList(CategoryDTO queryDTO) {
        // 1. 构造查询条件
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();

        // 确保从 queryDTO 中取值
        if (queryDTO != null) {
            wrapper.like(StrUtil.isNotBlank(queryDTO.getName()), Category::getName, queryDTO.getName());
            wrapper.like(StrUtil.isNotBlank(queryDTO.getCode()), Category::getCode, queryDTO.getCode());
        }

        wrapper.orderByAsc(Category::getCreateTime);

        // 2. 执行查询
        List<Category> list = this.list(wrapper);

        // 3. 填充图书数量（你之前的逻辑）
        List<CategoryVO> voList = list.stream().map(entity -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(entity, vo);
            Integer count = baseMapper.countBooksByCategoryId(entity.getId());
            vo.setBookCount(count != null ? count : 0);
            return vo;
        }).collect(Collectors.toList());

        // 4. 封装 records 格式返回
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("records", voList);
        responseData.put("total", voList.size());

        return Result.success(responseData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addCategory(CategoryDTO dto) {
        if (StrUtil.isBlank(dto.getCode())) {
            dto.setCode(IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase());
        }

        if (checkNameExists(dto.getName(), null)) {
            return Result.error("分类名称已存在");
        }
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);

        this.save(category);
        return Result.success("新增成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateCategory(CategoryDTO dto) {
        if (dto.getId() == null) return Result.error("ID不能为空");
        if (checkNameExists(dto.getName(), dto.getId())) {
            return Result.error("分类名称已存在");
        }
        Category category = this.getById(dto.getId());
        if (category == null) return Result.error(404, "分类不存在");

        BeanUtils.copyProperties(dto, category);
        this.updateById(category);
        return Result.success("修改成功");
    }

    // 重点：这个方法必须存在，且参数是 Long id
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteCategory(Long id) {
        // 检查分类下是否有图书
        LambdaQueryWrapper<Book> bookWrapper = Wrappers.lambdaQuery();
        bookWrapper.eq(Book::getCategoryId, id);
        long count = bookMapper.selectCount(bookWrapper);
        if (count > 0) {
            return Result.error("该分类下尚有图书，无法删除");
        }

        boolean removed = this.removeById(id);
        if (removed) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败，分类可能不存在");
    }

    /**
     * 私有校验方法
     */
    private boolean checkNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Category::getName, name);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }
}