package com.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookDTO {
    private Long id;
    private String isbn;
    private String name;
    private String author;
    private String publisher;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate publishTime;
    private BigDecimal price;
    private Long categoryId;
    private Integer stock;
    private Integer borrowNum;
    private String description;
    private String remark;
}