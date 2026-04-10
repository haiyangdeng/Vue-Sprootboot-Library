package com.library.controller;

import com.library.common.Result;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public Result<?> getBorrowStats(
            @RequestParam(defaultValue = "month") String timeType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return borrowService.getBorrowStats(timeType, startTime, endTime);
    }

    /**
     * 热门图书TOP统计
     */
    @GetMapping("/hotBook")
    public Result<?> getHotBooks(
            @RequestParam(defaultValue = "10") Integer topNum) {
        return borrowService.getHotBooks(topNum);
    }

    /**
     * 用户借阅次数TOP统计
     */
    @GetMapping("/topUser")
    public Result<?> getTopBorrowUsers(
            @RequestParam(defaultValue = "10") Integer topNum) {
        return borrowService.getTopBorrowUsers(topNum);
    }
}