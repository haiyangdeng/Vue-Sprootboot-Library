package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.Result;
import com.library.common.annotation.Log;
import com.library.dto.BorrowDTO;
import com.library.dto.LendRecordDTO;
import com.library.service.BorrowService;
import com.library.vo.BorrowVO;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Log("借阅图书")
    public Result<?> borrowBook(@RequestBody @Validated BorrowDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        // 如果不是管理员，强制只能为当前登录用户借书
        if (!"admin".equals(role)) {
            dto.setUserId(userId);
        } else if (dto.getUserId() == null) {
            // 如果是管理员且没传被借阅人ID，默认给自己借（或者可以报错提示必须传ID）
            dto.setUserId(userId);
        }

        // 调用 Service（现在返回 void，报错由 ServiceException 处理）
        borrowService.borrowBook(dto);
        return Result.success("借阅成功");
    }

    /**
     * 分页查询借阅记录
     */
    @GetMapping("/list")
    public Result<IPage<BorrowVO>> getBorrowList(
            BorrowDTO queryDTO,
            HttpServletRequest request) {

        // 普通用户只能查自己的记录
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            queryDTO.setUserId((Long) request.getAttribute("userId"));
        }

        // 获取 Service 返回的裸数据并包装进 Result
        IPage<BorrowVO> data = borrowService.getBorrowPage(queryDTO);
        return Result.success(data);
    }
}