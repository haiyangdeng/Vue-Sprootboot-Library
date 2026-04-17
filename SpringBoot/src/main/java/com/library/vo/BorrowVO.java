package com.library.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorrowVO {
    private Long id;
    private Long userId;
    private String userName;     // 关联查询
    private Long bookId;
    private String bookName;     // 关联查询
    private String bookIsbn;
    private String status; //借阅状态：unreturned-未归还，returned-已归还，overdue-逾期
    private String overdueDays;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnTime;
}