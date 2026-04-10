package com.library.controller;

import com.library.common.Result;
import com.library.dto.BookDTO;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 分页查询图书列表
     */
    @GetMapping("/list")
    public Result<?> getBookList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId) {
        return bookService.getBookPage(pageNum, pageSize, name, author, categoryId);
    }

    /**
     * 获取图书详情
     */
    @GetMapping("/detail/{id}")
    public Result<?> getBookDetail(@PathVariable Long id) {
        return bookService.getBookDetail(id);
    }

    /**
     * 新增图书（管理员）
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> addBook(@RequestBody @Validated BookDTO dto) {
        return bookService.addBook(dto);
    }

    /**
     * 修改图书（管理员）
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateBook(@RequestBody @Validated BookDTO dto) {
        return bookService.updateBook(dto);
    }

    /**
     * 删除图书（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }

    /**
     * 批量删除图书（管理员）
     */
    @DeleteMapping("/batchDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> batchDeleteBooks(@RequestBody List<Long> ids) {
        return bookService.batchDeleteBooks(ids);
    }
}
