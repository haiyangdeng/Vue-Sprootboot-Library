package com.library.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String role;

    private String phone;

    private String email;

    private String address;

    private String remark;
}
