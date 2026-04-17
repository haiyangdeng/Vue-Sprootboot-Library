package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("sys_category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String code;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 必须是 LocalDateTime

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 必须是 LocalDateTime

    private String remark;

    // 非数据库字段：分类下的图书数量
    @TableField(exist = false)
    private Integer bookCount;
}