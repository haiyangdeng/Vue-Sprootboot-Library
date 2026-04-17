package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.Result;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.vo.BookVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

public interface BookService extends IService<Book> {
    Result<IPage<BookVO>> getBookPage(Integer pageNum, Integer pageSize, String name, String author, String isbn);
    Result<BookVO> getBookDetail(Long id);
    Result<?> addBook(BookDTO dto);
    Result<?> updateBook(BookDTO dto);
    Result<?> deleteBook(Long id);
    Result<?> batchDeleteBooks(List<Long> ids);
}