package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.Result;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.UserService;
import com.library.vo.BookHotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard") // 确保这里的路径匹配前端报错的 /api/dashboard
public class DashboardController {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private BorrowService borrowService;

    @GetMapping
    public Result<?> getDashboard() {
        // 直接返回所有统计数据的集合
        Map<String, Object> res = new HashMap<>();
        res.put("statistics", getStatisticsData());
        res.put("topBooks", borrowService.getHotBooks(10));
        res.put("topUsers", borrowService.getTopBorrowUsers(10));
        return Result.success(res);
    }

    @GetMapping("/statistics")
    public Result<?> getStatistics() {
        return Result.success(getStatisticsData());
    }

    /** 2. 热门图书排行 (/api/dashboard/topBooks) */
    @GetMapping("/topBooks")
    public Result<List<BookHotVO>> getTopBooks(@RequestParam(defaultValue = "10") Integer limit) {
        // 接收前端传来的 limit 参数，默认值为 10
        return Result.success(borrowService.getHotBooks(limit));
    }

    /** 3. 借阅达人排行 (/api/dashboard/topUsers) */
    @GetMapping("/topUsers")
    public Result<?> getTopUsers(@RequestParam(defaultValue = "10") Integer limit) {
        // 接收前端传来的 limit 参数，默认值为 10
        return Result.success(borrowService.getTopBorrowUsers(limit));
    }

    private Map<String, Object> getStatisticsData() {
        Map<String, Object> res = new HashMap<>();
        res.put("bookCount", bookService.count());
        res.put("userCount", userService.count());
        res.put("borrowCount", borrowService.count());
        return res;
    }
}