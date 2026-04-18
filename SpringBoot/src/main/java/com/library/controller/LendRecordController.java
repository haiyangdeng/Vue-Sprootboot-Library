package com.library.controller;

import com.library.common.Result;
import com.library.dto.BorrowDTO;
import com.library.dto.LendRecordDTO; // 确保导入正确的DTO
import com.library.service.BorrowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/LendRecord")
public class LendRecordController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 借书接口 (POST /LendRecord)
     * 建议使用 DTO 直接接收参数，而不是 Map
     */
    @PostMapping
    public Result<?> handleLend(@RequestBody BorrowDTO dto) {
        try {
            // 参数基础校验
            if (dto.getUserId() == null && dto.getIsbn() == null) {
                return Result.error("读者ID和图书ISBN不能都为空");
            }
            // 执行业务逻辑
            borrowService.borrowBook(dto);
            return Result.success("借阅成功");
        } catch (Exception e) {
            log.error("借阅失败", e);
            return Result.error("借阅失败：" + e.getMessage());
        }
    }

    /**
     * 还书接口 (POST /LendRecord/return/{id})
     */
    @PostMapping("/return/{id}")
    public Result<?> handleReturn(@PathVariable Long id) {
        try {
            if (id == null) {
                return Result.error("记录ID不能为空");
            }
            borrowService.returnBook(id);
            return Result.success("归还成功");
        } catch (Exception e) {
            log.error("归还失败，ID: {}", id, e);
            return Result.error("归还失败：" + e.getMessage());
        }
    }
}