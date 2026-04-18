package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.Result;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.mapper.CategoryMapper;
import com.library.service.BookService;
import com.library.vo.BookVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result<IPage<BookVO>> getBookPage(Integer pageNum, Integer pageSize, String name, String author, String isbn) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Book> wrapper = new QueryWrapper<>();

        // 构建查询条件...
        wrapper.like(StringUtils.hasText(name), "b.name", name);
        // ... 其他条件

        // 【核心步骤】获取当前登录用户ID
        // 假设你之前在 JwtAuthenticationFilter 中把 userId 存入了 request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long currentUserId = (Long) attributes.getRequest().getAttribute("userId");

        // 传入 currentUserId
        IPage<BookVO> voPage = baseMapper.findBookVOPage(page, wrapper, currentUserId);

        return Result.success(voPage);
    }

    @Override
    public Result<BookVO> getBookDetail(Long id) {
        // 1. 查询数据库实体 Entity
        Book book = baseMapper.selectById(id);
        if (book == null) {
            return Result.error("图书不存在");
        }

        return Result.success(convertToVO(book));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addBook(BookDTO dto) {
        // 1. 校验分类 ID 是否存在（防止前端传空）
        if (dto.getCategoryId() == null) {
            return Result.error("请选择图书分类");
        }

        Book book = new Book();

        // 2. 拷贝属性（排除掉类型不匹配的 publishTime）
        BeanUtils.copyProperties(dto, book, "publishTime");

        // 3. 手动处理 publishTime 转换（你之前遇到的报错）
        if (dto.getPublishTime() != null) {
            book.setPublishTime(dto.getPublishTime().atStartOfDay());
        }

        // 4. 【关键】如果此时报错，说明 BeanUtils 没拷过去，手动补一下
        book.setCategoryId(dto.getCategoryId());

        this.save(book);
        return Result.success("新增图书成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateBook(BookDTO dto) {
        if (dto.getId() == null) return Result.error("ID不能为空");
        Book book = this.getById(dto.getId());
        if (book == null) return Result.error(404, "图书不存在");

        BeanUtils.copyProperties(dto, book, "publishTime"); // 忽略时间字段

        if (dto.getPublishTime() != null) {
            book.setPublishTime(dto.getPublishTime().atStartOfDay()); // 手动对齐
        }

        this.updateById(book);
        return Result.success("修改图书成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteBook(Long id) {
        // 逻辑检查：检查是否有未归还记录
        checkBorrowStatus(id);

        this.removeById(id);
        return Result.success("删除图书成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchDeleteBooks(List<Long> ids) {
        for (Long id : ids) {
            checkBorrowStatus(id);
        }
        this.removeBatchByIds(ids);
        return Result.success("批量删除成功");
    }

    /**
     * 私有辅助方法：检查图书借阅状态
     */
    private void checkBorrowStatus(Long bookId) {
        LambdaQueryWrapper<Borrow> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Borrow::getBookId, bookId);
        wrapper.eq(Borrow::getStatus, "unreturned");
        long count = borrowMapper.selectCount(wrapper);
        if (count > 0) {
            Book book = this.getById(bookId);
            String bookName = (book != null) ? book.getName() : String.valueOf(bookId);
            // 抛出运行时异常以触发事务回滚，或者在具体逻辑中处理
            throw new RuntimeException("图书《" + bookName + "》尚有未归还记录，无法删除");
        }
    }

    // 2. 转换逻辑
    private BookVO convertToVO(Book book) {
        if (book == null) return null;
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);

        if (book.getCategoryId() != null) {
            Category category = categoryMapper.selectById(book.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        return vo;
    }
}