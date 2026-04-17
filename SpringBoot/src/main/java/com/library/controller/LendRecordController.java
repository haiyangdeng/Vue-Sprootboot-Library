package com.library.controller;

import com.library.common.Result;
import com.library.dto.BorrowDTO;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/LendRecord")
public class LendRecordController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 借书接口 (POST /api/LendRecord)
     */
    @PostMapping
    public Result<?> handleLend(@RequestBody Map<String, Object> params) {
        try {
            BorrowDTO dto = new BorrowDTO();
            if (params.get("isbn") != null) {
                dto.setBookIsbn(params.get("isbn").toString());
            }
            Object readerIdObj = params.get("readerId");
            if (readerIdObj == null) return Result.error("读者ID不能为空");
            dto.setUserId(Long.valueOf(readerIdObj.toString()));

            borrowService.borrowBook(dto);
            return Result.success("借阅成功");
        } catch (Exception e) {
            return Result.error("借阅失败：" + e.getMessage());
        }
    }

    /**
     * 还书接口 (POST /api/LendRecord/return/{id})
     * 前端调用示例：request.post('/LendRecord/return/' + recordId)
     */
    @PostMapping("/return/{id}")
    public Result<?> handleReturn(@PathVariable Long id) {
        try {
            // 调用你之前在 Service 实现的 returnBook 方法
            borrowService.returnBook(id);
            return Result.success("归还成功");
        } catch (Exception e) {
            return Result.error("归还失败：" + e.getMessage());
        }
    }
}