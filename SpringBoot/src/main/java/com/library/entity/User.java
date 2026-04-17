package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String nickname;

    private Integer sex;

    @JsonIgnore
    private String password;

    private String role;

    private String phone;

    private String email;

    private String address;

    // 关键点：指定在插入时填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 关键点：指定在插入和更新时都填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String remark;

    // 非数据库字段，用于接收前端传参
    @TableField(exist = false)
    private String token;
}