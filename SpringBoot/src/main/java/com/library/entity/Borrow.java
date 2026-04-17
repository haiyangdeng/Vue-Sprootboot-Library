package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_borrow")
public class Borrow {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long bookId;
    private String status;
    private LocalDateTime borrowTime;
    private LocalDateTime returnTime;

    // 自动填充字段（配合你之前的 MetaObjectHandler）
//    @TableField(fill = FieldFill.INSERT)
//    private LocalDateTime createTime;
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private LocalDateTime updateTime;

    private LocalDateTime returnDeadline;

    // --- 以下字段在数据库 sys_borrow 表中不存在，必须排除 ---

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String bookName;

    @TableField(exist = false)
    private String bookIsbn;

    @TableField(exist = false, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createTime;

    @TableField(exist = false, fill = FieldFill.INSERT)
    private LocalDateTime updateTime;

}