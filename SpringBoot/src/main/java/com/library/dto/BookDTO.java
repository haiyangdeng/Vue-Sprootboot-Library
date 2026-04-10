package com.library.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BookDTO {
    private Long id;

    @NotBlank(message = "图书名称不能为空")
    private String name;

    @NotBlank(message = "作者不能为空")
    private String author;

    @NotBlank(message = "出版社不能为空")
    private String publisher;

    @NotNull(message = "出版时间不能为空")
    private Date publishTime;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    private String description;

    private String remark;
}