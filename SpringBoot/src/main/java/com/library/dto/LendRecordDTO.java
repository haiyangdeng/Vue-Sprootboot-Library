package com.library.dto;

import lombok.Data;

@Data
public class LendRecordDTO {
    private String isbn;
    private String bookName;
    private Long userId;
    private Integer borrowNum;
    private String lendTime;
    private String status;
}