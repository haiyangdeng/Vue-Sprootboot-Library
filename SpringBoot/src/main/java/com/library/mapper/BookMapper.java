package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import com.library.vo.BookHotVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BookMapper extends BaseMapper<Book> {

    IPage<Book> selectBookPage(
            Page<Book> page,
            @Param("name") String name,
            @Param("author") String author,
            @Param("categoryId") Long categoryId
    );

    List<BookHotVO> selectHotBooks(@Param("limit") Integer limit);

}