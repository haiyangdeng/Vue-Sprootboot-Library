package com.library.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrow;
import com.library.vo.BorrowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

    /** 借阅趋势统计 (MySQL 示例) */
    @Select("<script>" +
            "SELECT " +
            "  <choose>" +
            "    <when test='timeType == \"day\"'>DATE_FORMAT(borrow_time, '%Y-%m-%d')</when>" +
            "    <otherwise>DATE_FORMAT(borrow_time, '%Y-%m')</otherwise>" +
            "  </choose> AS date, " +
            "  COUNT(*) AS count " +
            "FROM sys_borrow " +
            "<where>" +
            "  <if test='startTime != null'> AND borrow_time >= #{startTime} </if>" +
            "  <if test='endTime != null'> AND borrow_time &lt;= #{endTime} </if>" +
            "</where>" +
            "GROUP BY date ORDER BY date ASC" +
            "</script>")
    List<Map<String, Object>> selectBorrowStats(@Param("timeType") String timeType,
                                                @Param("startTime") String startTime,
                                                @Param("endTime") String endTime);

    /** 热门图书统计 */
    @Select("SELECT b.name as name, COUNT(br.id) as value " +
            "FROM sys_borrow br " +
            "JOIN sys_book b ON br.book_id = b.id " +
            "GROUP BY br.book_id, b.name " + // 这里必须包含 b.name
            "ORDER BY value DESC LIMIT #{topNum}")
    List<Map<String, Object>> selectHotBooks(@Param("topNum") Integer topNum);

    /** 活跃用户统计 */
    @Select("SELECT u.username as name, COUNT(br.id) as value " +
            "FROM sys_borrow br " +
            "JOIN sys_user u ON br.user_id = u.id " +
            "GROUP BY br.user_id, u.username " + // 这里必须包含 u.username
            "ORDER BY value DESC LIMIT #{topNum}")
    List<Map<String, Object>> selectTopBorrowUsers(@Param("topNum") Integer topNum);

    @Select("SELECT b.*, u.username as userName, bk.name as bookName, bk.isbn as bookIsbn " +
            "FROM sys_borrow b " +
            "LEFT JOIN sys_user u ON b.user_id = u.id " +
            "LEFT JOIN sys_book bk ON b.book_id = bk.id " + // 注意这里的 bk 别名
            "${ew.customSqlSegment}")
    IPage<BorrowVO> selectBorrowPage(Page<Borrow> page, @Param(Constants.WRAPPER) Wrapper<Borrow> wrapper);

    // 统计借阅量前 10 的图书
    @Select("SELECT bk.name as name, COUNT(b.id) as value " +
            "FROM sys_borrow b " +
            "LEFT JOIN sys_book bk ON b.book_id = bk.id " +
            "GROUP BY b.book_id, bk.name " + // 修改处
            "ORDER BY value DESC LIMIT 10")
    List<Map<String, Object>> selectTopBooks();

    // 统计借阅量前 10 的用户
    @Select("SELECT u.username as name, COUNT(b.id) as value " +
            "FROM sys_borrow b " +
            "LEFT JOIN sys_user u ON b.user_id = u.id " +
            "GROUP BY b.user_id " +
            "ORDER BY value DESC LIMIT 10")
    List<Map<String, Object>> selectTopUsers();
}