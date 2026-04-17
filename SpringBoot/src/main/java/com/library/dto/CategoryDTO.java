package com.library.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotBlank(message = "分类Code不能为空")
    private String code;

    private String remark; // 备注
}