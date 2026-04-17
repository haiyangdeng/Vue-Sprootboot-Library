package com.library.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookVO {
    private Long id;
    private String isbn;
    private String name;
    private String author;
    private String publisher;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime publishTime;
    private BigDecimal price;
    private Integer stock;
    private Integer borrowNum;

    private String categoryName; // 数据库存的是 categoryId，VO 直接给前端分类名称
    private String status;
    private String statusName;   // 租借状态：在库 / 借阅中
    private String description;
    private String remark;
}

