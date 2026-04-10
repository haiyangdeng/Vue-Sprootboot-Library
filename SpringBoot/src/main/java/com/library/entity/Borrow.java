package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_borrow")
public class Borrow implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long bookId;

    private Date borrowTime;

    private Date returnDeadline;

    private Date returnTime;

    private String status;

    private Integer overdueDays;

    private String remark;

    // 非数据库字段
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String bookName;

    @TableField(exist = false)
    private String author;
}