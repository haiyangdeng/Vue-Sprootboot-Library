package com.library.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.library.vo.BookVO;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 1. 分页查询图书 (对应 BookServiceImpl 调用)
     */
    @Select("<script>" +
            "SELECT * FROM sys_book " +
            "<where>" +
            "  <if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%') </if>" +
            "  <if test='author != null and author != \"\"'> AND author LIKE CONCAT('%', #{author}, '%') </if>" +
            "  <if test='categoryId != null'> AND category_id = #{categoryId} </if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<BookVO> selectBookPage(Page<Book> page,
                               @Param("name") String name,
                               @Param("author") String author,
                               @Param("categoryId") Long categoryId);


    @Select("SELECT b.name as name, COUNT(br.id) as value " +
            "FROM sys_borrow br " +
            "JOIN sys_book b ON br.book_id = b.id " +
            "GROUP BY br.book_id, b.name " + // 核心修改：将 b.name 加入分组
            "ORDER BY value DESC LIMIT #{topNum}")
    List<Map<String, Object>> selectHotBooks(@Param("topNum") Integer topNum);



    @Select("SELECT b.*, c.name AS categoryName, " +
            "IF(br.id IS NULL, 'available', 'unreturned') AS status, " +  // 这里直接计算 status
            "IF(br.id IS NULL, '在库', '借阅中') AS statusName " +
            "FROM sys_book b " +
            "LEFT JOIN sys_category c ON b.category_id = c.id " +
            "LEFT JOIN sys_borrow br ON b.id = br.book_id AND br.status = 'unreturned' " +
            "${ew.customSqlSegment}")
    IPage<BookVO> findBookVOPage(Page<Book> page, @Param(Constants.WRAPPER) Wrapper<Book> wrapper);
}