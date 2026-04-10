package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.Result;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService extends ServiceImpl<BookMapper, Book> {

    @Autowired
    private BorrowMapper borrowMapper;

    /**
     * 分页查询图书
     */
    public Result<?> getBookPage(Integer pageNum, Integer pageSize,
                                 String name, String author, Long categoryId) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        IPage<Book> result = baseMapper.selectBookPage(page, name, author, categoryId);
        return Result.success(result);
    }

    /**
     * 获取图书详情
     */
    public Result<?> getBookDetail(Long id) {
        Book book = this.getById(id);
        if (book == null) {
            return Result.error(404, "图书不存在");
        }
        return Result.success(book);
    }

    /**
     * 新增图书
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addBook(BookDTO dto) {
        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        this.save(book);
        return Result.success("新增图书成功");
    }

    /**
     * 修改图书
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateBook(BookDTO dto) {
        Book book = this.getById(dto.getId());
        if (book == null) {
            return Result.error(404, "图书不存在");
        }

        BeanUtils.copyProperties(dto, book);
        this.updateById(book);
        return Result.success("修改图书成功");
    }

    /**
     * 删除图书
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteBook(Long id) {
        Book book = this.getById(id);
        if (book == null) {
            return Result.error(404, "图书不存在");
        }

        // 检查是否有未归还的借阅记录
        LambdaQueryWrapper<Borrow> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Borrow::getBookId, id);
        wrapper.eq(Borrow::getStatus, "unreturned");
        long count = borrowMapper.selectCount(wrapper);
        if (count > 0) {
            return Result.error("该图书有" + count + "条未归还的借阅记录，无法删除");
        }

        this.removeById(id);
        return Result.success("删除图书成功");
    }

    /**
     * 批量删除图书
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchDeleteBooks(List<Long> ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<Borrow> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(Borrow::getBookId, id);
            wrapper.eq(Borrow::getStatus, "unreturned");
            long count = borrowMapper.selectCount(wrapper);
            if (count > 0) {
                Book book = this.getById(id);
                return Result.error("图书《" + (book != null ? book.getName() : id) + "》有未归还记录，无法删除");
            }
        }

        this.removeBatchByIds(ids);
        return Result.success("批量删除成功");
    }
}