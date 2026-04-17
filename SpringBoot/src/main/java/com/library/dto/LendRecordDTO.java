package com.library.dto;

import lombok.Data;

@Data
public class LendRecordDTO {
    private String isbn;
    private String bookname;
    private Long readerId;    // 对应前端 user.value.id
    private Integer borrownum;
    private String lendTime;
    private String status;
}