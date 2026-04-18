package com.library.vo;

import lombok.Data;


@Data
public class BookHotVO {
    private Long id;
    private String name;
    private String author;
    private Integer borrowCount;
    private Integer borrowNum;
}