package com.library.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class BorrowDTO {
    @NotNull(message = "图书ID不能为空")
    private String isbn;
    private Long bookId;
    private String bookIsbn;
    private String bookName;
    private Long userId;
    private String userName;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}