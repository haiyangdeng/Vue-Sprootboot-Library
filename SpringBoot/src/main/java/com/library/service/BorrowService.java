package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class BorrowService extends ServiceImpl<BorrowMapper, Borrow> {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 借阅图书
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> borrowBook(Long bookId, Long userId) {
        // 校验用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 校验图书
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return Result.error("图书不存在");
        }

        // 校验库存
        if (book.getStock() <= 0) {
            return Result.error("库存不足，无法借阅");
        }

        // 校验是否重复借阅（同一用户同一本书未归还）
        LambdaQueryWrapper<Borrow> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Borrow::getBookId, bookId);
        wrapper.eq(Borrow::getUserId, userId);
        wrapper.eq(Borrow::getStatus, "unreturned");
        long count = this.count(wrapper);
        if (count > 0) {
            return Result.error("已借阅该图书，请勿重复借阅");
        }

        // 创建借阅记录
        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBookId(bookId);
        borrow.setBorrowTime(new Date());

        // 设置归还截止时间（30天后）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        borrow.setReturnDeadline(calendar.getTime());

        borrow.setStatus("unreturned");
        borrow.setOverdueDays(0);
        this.save(borrow);

        // 扣减库存
        book.setStock(book.getStock() - 1);
        bookMapper.updateById(book);

        return Result.success("借阅成功");
    }

    /**
     * 归还图书
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> returnBook(Long borrowId) {
        Borrow borrow = this.getById(borrowId);
        if (borrow == null) {
            return Result.error(404, "借阅记录不存在");
        }

        if (!"unreturned".equals(borrow.getStatus())) {
            return Result.error("该图书已归还，无需重复操作");
        }

        Date now = new Date();
        borrow.setReturnTime(now);

        // 判断是否逾期
        if (now.after(borrow.getReturnDeadline())) {
            long diffMillis = now.getTime() - borrow.getReturnDeadline().getTime();
            int overdueDays = (int) TimeUnit.MILLISECONDS.toDays(diffMillis);
            borrow.setOverdueDays(Math.max(overdueDays, 1));
            borrow.setStatus("overdue");
        } else {
            borrow.setStatus("returned");
            borrow.setOverdueDays(0);
        }

        this.updateById(borrow);

        // 恢复库存
        Book book = bookMapper.selectById(borrow.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            bookMapper.updateById(book);
        }

        return Result.success("归还成功");
    }

    /**
     * 分页查询借阅记录
     */
    public Result<?> getBorrowPage(Integer pageNum, Integer pageSize,
                                   Long userId, Long bookId, String status) {
        Page<Borrow> page = new Page<>(pageNum, pageSize);
        IPage<Borrow> result = baseMapper.selectBorrowPage(page, userId, bookId, status);
        return Result.success(result);
    }

    /**
     * 借阅数据统计
     */
    public Result<?> getBorrowStats(String timeType, String startTime, String endTime) {
        List<Map<String, Object>> stats = baseMapper.selectBorrowStats(timeType, startTime, endTime);
        return Result.success(stats);
    }

    /**
     * 热门图书统计
     */
    public Result<?> getHotBooks(Integer topNum) {
        if (topNum == null || topNum <= 0) topNum = 10;
        List<Map<String, Object>> hotBooks = bookMapper.selectHotBooks(topNum);
        return Result.success(hotBooks);
    }

    /**
     * 借阅次数TOP用户
     */
    public Result<?> getTopBorrowUsers(Integer topNum) {
        if (topNum == null || topNum <= 0) topNum = 10;
        List<Map<String, Object>> topUsers = baseMapper.selectTopBorrowUsers(topNum);
        return Result.success(topUsers);
    }
}