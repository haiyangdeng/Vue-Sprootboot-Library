package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.Result;
import com.library.service.BorrowService;
import com.library.vo.BookHotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stat")
@PreAuthorize("hasRole('ADMIN')")
public class StatController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 借阅数据统计
     */
    @GetMapping("/borrow")
    public Result<List<Map<String, Object>>> getBorrowStats(
            @RequestParam(defaultValue = "month") String timeType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        // Service 返回的是 List，这里用 Result.success() 包装
        List<Map<String, Object>> data = borrowService.getBorrowStats(timeType, startTime, endTime);
        return Result.success(data);
    }

    /**
     * 热门图书TOP统计
     */
    @GetMapping("/hotBook")
    public Result<List<BookHotVO>> getHotBooks(
            @RequestParam(defaultValue = "10") Integer topNum) {
        List<BookHotVO> data = borrowService.getHotBooks(topNum);
        return Result.success(data);
    }

    /**
     * 用户借阅次数TOP统计
     */
    @GetMapping("/topUser")
    public Result<List<Map<String, Object>>> getTopBorrowUsers(
            @RequestParam(defaultValue = "10") Integer topNum) {
        List<Map<String, Object>> data = borrowService.getTopBorrowUsers(topNum);
        return Result.success(data);
    }
}