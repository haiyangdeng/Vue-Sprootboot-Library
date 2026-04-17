package com.library.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BookWithUserVO {
    private Integer id;
    private String isbn;
    private String bookName;
    private String nickName;
    private Integer prolong;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date lendtime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date deadtime;

    // 计算出来的字段，VO 专供
    private Long totalDays;
    private Long remainingDays;
}