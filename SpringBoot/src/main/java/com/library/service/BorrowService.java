package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.BorrowDTO;
import com.library.entity.Borrow;
import com.library.vo.BorrowVO;
import java.util.List;
import java.util.Map;

public interface BorrowService extends IService<Borrow> {

    /**
     * 借阅图书
     * 优化：返回 void。如果借阅失败（如库存不足），直接抛出自定义业务异常
     */
    void borrowBook(BorrowDTO dto);

    /**
     * 归还图书
     */
    void returnBook(Long id);

    /**
     * 分页查询借阅记录
     * 优化：直接返回 MyBatis-Plus 的 IPage 包装 VO
     */
    IPage<BorrowVO> getBorrowPage(BorrowDTO queryDTO);

    /** * 借阅趋势统计
     * 优化：返回具体的 List 结构
     */
    List<Map<String, Object>> getBorrowStats(String timeType, String startTime, String endTime);

    /** 热门图书排行 */
    List<Map<String, Object>> getHotBooks(Integer topNum);

    /** 活跃用户排行 */
    List<Map<String, Object>> getTopBorrowUsers(Integer topNum);
}