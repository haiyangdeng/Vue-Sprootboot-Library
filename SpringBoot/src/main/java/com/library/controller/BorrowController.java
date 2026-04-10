package com.library.controller;

import com.library.common.Result;
import com.library.dto.BorrowDTO;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 借阅图书
     */
    @PostMapping("/add")
    public Result<?> borrowBook(@RequestBody @Validated BorrowDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 普通用户只能为自己借阅
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            dto.setUserId(userId);
        }
        return borrowService.borrowBook(dto.getBookId(), dto.getUserId());
    }

    /**
     * 归还图书
     */
    @PutMapping("/return/{id}")
    public Result<?> returnBook(@PathVariable Long id) {
        return borrowService.returnBook(id);
    }

    /**
     * 分页查询借阅记录
     */
    @GetMapping("/list")
    public Result<?> getBorrowList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {

        // 普通用户只能查自己的记录
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            userId = (Long) request.getAttribute("userId");
        }

        return borrowService.getBorrowPage(pageNum, pageSize, userId, bookId, status);
    }
}