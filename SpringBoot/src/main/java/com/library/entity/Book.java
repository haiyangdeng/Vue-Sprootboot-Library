package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_book") // 对应 SQL 中的表名
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String isbn;
    private String name;
    private String author;
    private String publisher;
    private LocalDateTime publishTime;
    private BigDecimal price;
    private Long categoryId;
    private Integer stock;
    private Integer borrowNum;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String remark;
}