package com.library.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.BorrowDTO;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.common.exception.ServiceException;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.service.BorrowService;
import com.library.vo.BorrowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements BorrowService {

    @Autowired
    private BookMapper bookMapper;

    /**
     * 借阅图书
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(BorrowDTO dto) {
        // 1. 校验图书
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null) throw new ServiceException("图书不存在");
        if (book.getStock() <= 0) throw new ServiceException("该图书库存不足，无法借阅");

        // 2. 检查是否重复借阅
        QueryWrapper<Borrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_id", dto.getBookId())
                .eq("user_id", dto.getUserId())
                .eq("status", "unreturned");
        if (this.count(queryWrapper) > 0) {
            throw new ServiceException("您已借阅该书尚未归还，请勿重复借阅");
        }

        // 3. 核心修复：定义时间变量
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadTime = now.plusDays(30); // 声明变量 deadTime

        // 4. 更新图书库存和借阅量 (sys_book)
        book.setStock(book.getStock() - 1);
        book.setBorrowNum(book.getBorrowNum() + 1);
        bookMapper.updateById(book);

        // 5. 保存借阅记录 (sys_borrow)
        Borrow borrow = new Borrow();
        borrow.setBookId(dto.getBookId());
        borrow.setUserId(dto.getUserId());
        borrow.setBorrowTime(now);
        borrow.setStatus("unreturned");
        this.save(borrow);
    }

    /**
     * 归还图书逻辑实现
     * 流程：校验记录 -> 修改借阅状态 -> 增加图书库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long id) {
        // 1. 获取借阅记录
        Borrow borrow = this.getById(id);
        if (borrow == null) {
            throw new ServiceException("未找到相关借阅记录");
        }

        // 2. 状态校验：如果已经是归还状态，则报错
        if ("returned".equals(borrow.getStatus())) {
            throw new ServiceException("该图书已归还，请勿重复操作");
        }

        // 3. 更新借阅记录状态 (sys_borrow)
        borrow.setStatus("returned");
        borrow.setReturnTime(LocalDateTime.now());
        // 计算是否逾期（可选逻辑）：
        // if (LocalDateTime.now().isAfter(borrow.getReturnDeadline())) {
        //    borrow.setStatus("overdue_returned"); // 逾期归还状态
        // }
        this.updateById(borrow);

        // 4. 【核心缺失步骤】恢复图书库存 (sys_book)
        Book book = bookMapper.selectById(borrow.getBookId());
        if (book != null) {
            // 库存 +1
            book.setStock(book.getStock() + 1);
            // 注意：借阅量一般不扣减，记录的是历史累计借阅次数
            bookMapper.updateById(book);
        } else {
            throw new ServiceException("关联的图书信息丢失，请检查数据");
        }
    }

    /**
     * 分页查询
     * 优化点：直接返回 IPage<BorrowVO>，类型明确
     */
    @Override
    public IPage<BorrowVO> getBorrowPage(BorrowDTO queryDTO) {
        Page<Borrow> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();

        // 精确匹配主表字段使用 b. 前缀
        wrapper.eq(queryDTO.getUserId() != null, "b.user_id", queryDTO.getUserId());
        // 状态过滤（解决你 status 没返回或无法过滤的问题）
        wrapper.eq(StrUtil.isNotBlank(queryDTO.getStatus()), "b.status", queryDTO.getStatus());

        // 模糊匹配关联表字段使用别名前缀
        wrapper.like(StrUtil.isNotBlank(queryDTO.getUserName()), "u.username", queryDTO.getUserName());
        wrapper.like(StrUtil.isNotBlank(queryDTO.getBookName()), "bk.name", queryDTO.getBookName());
        wrapper.eq(StrUtil.isNotBlank(queryDTO.getBookIsbn()), "bk.isbn", queryDTO.getBookIsbn());

        wrapper.orderByDesc("b.id");

        return baseMapper.selectBorrowPage(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> getHotBooks(Integer topNum) {
        return baseMapper.selectHotBooks(topNum);
    }

    @Override
    public List<Map<String, Object>> getTopBorrowUsers(Integer topNum) {
        return baseMapper.selectTopBorrowUsers(topNum);
    }

    @Override
    public List<Map<String, Object>> getBorrowStats(String timeType, String startTime, String endTime) {
        // 确保这里的参数类型 (String, String, String) 和返回值类型 (List)
        // 与接口中定义的一模一样
        return baseMapper.selectBorrowStats(timeType, startTime, endTime);
    }
}