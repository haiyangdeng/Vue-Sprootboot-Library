package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

    /**
     * 分页查询借阅记录（关联用户名、图书名称）
     */
    IPage<Borrow> selectBorrowPage(Page<Borrow> page,
                                   @Param("userId") Long userId,
                                   @Param("bookId") Long bookId,
                                   @Param("status") String status);

    /**
     * 借阅统计
     */
    List<Map<String, Object>> selectBorrowStats(@Param("timeType") String timeType,
                                                @Param("startTime") String startTime,
                                                @Param("endTime") String endTime);

    /**
     * 用户借阅次数TOP N
     */
    @Select("SELECT u.id as userId, u.username, COUNT(b.id) as borrowCount " +
            "FROM sys_user u " +
            "LEFT JOIN sys_borrow b ON u.id = b.user_id " +
            "WHERE u.role = 'user' " +
            "GROUP BY u.id, u.username " +
            "ORDER BY borrowCount DESC " +
            "LIMIT #{topNum}")
    List<Map<String, Object>> selectTopBorrowUsers(@Param("topNum") Integer topNum);
}