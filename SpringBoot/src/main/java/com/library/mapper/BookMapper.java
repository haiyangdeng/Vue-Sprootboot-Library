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
            // 1. 获取【当前登录人】的借阅记录 ID
            "(SELECT br.id FROM sys_borrow br " +
            " WHERE br.book_id = b.id AND br.status = 'unreturned' AND br.user_id = #{currentUserId} LIMIT 1) AS recordId, " +

            // 2. 状态标识 (建议保持全局状态：只要有人借了就显示借阅中，还是仅针对当前人？)
            // 如果 status 也要针对当前人，请同样加入 br.user_id = #{currentUserId}
            "(SELECT IF(COUNT(*) > 0, 'unreturned', 'available') " +
            " FROM sys_borrow br WHERE br.book_id = b.id AND br.status = 'unreturned' AND br.user_id = #{currentUserId}) AS status, " +

            // 3. 状态文本
            "(SELECT IF(COUNT(*) > 0, '借阅中', '在库') " +
            " FROM sys_borrow br WHERE br.book_id = b.id AND br.status = 'unreturned' AND br.user_id = #{currentUserId}) AS statusName " +

            "FROM sys_book b " +
            "LEFT JOIN sys_category c ON b.category_id = c.id " +
            "${ew.customSqlSegment}")
    IPage<BookVO> findBookVOPage(Page<Book> page,
                                 @Param(Constants.WRAPPER) Wrapper<Book> wrapper,
                                 @Param("currentUserId") Long currentUserId);
}