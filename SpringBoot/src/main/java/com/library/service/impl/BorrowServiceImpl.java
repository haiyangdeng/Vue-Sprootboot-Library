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
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.vo.BookHotVO;
import com.library.vo.BorrowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements BorrowService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookService bookService;

    /**
     * 借阅图书
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(BorrowDTO dto) {
        // 1. 获取图书信息（优先使用 bookId，如果没有再尝试 isbn）
        Book book = null;
        if (dto.getBookId() != null) {
            book = bookMapper.selectById(dto.getBookId());
        } else if (dto.getIsbn() != null) {
            QueryWrapper<Book> bookQuery = new QueryWrapper<>();
            bookQuery.eq("isbn", dto.getIsbn());
            book = bookMapper.selectOne(bookQuery);
        }

        if (book == null) {
            throw new ServiceException("借阅失败：未找到对应的图书信息");
        }

        if (book.getStock() <= 0) {
            throw new ServiceException("该图书库存不足");
        }

        // 2. 检查是否重复借阅
        QueryWrapper<Borrow> borrowQuery = new QueryWrapper<>();
        borrowQuery.eq("book_id", book.getId())
                .eq("user_id", dto.getUserId())
                .eq("status", "unreturned");
        if (this.count(borrowQuery) > 0) {
            throw new ServiceException("您已借阅该书尚未归还，请勿重复借阅");
        }

        // 3. 时间处理
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadTime = now.plusDays(30);

        // 4. 更新库存和借阅量
        book.setStock(book.getStock() - 1);
        int borrowNum = book.getBorrowNum() == null ? 0 : book.getBorrowNum();
        book.setBorrowNum(borrowNum + 1);
        bookMapper.updateById(book);

        // 5. 保存借阅记录
        Borrow borrow = new Borrow();
        borrow.setBookId(book.getId());
        borrow.setUserId(dto.getUserId());
        borrow.setBorrowTime(now);
        borrow.setReturnDeadline(deadTime); // 别忘了这行，解决之前的 SQL 报错
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
        // 1. 获取借阅记录 (此处的 id 必须是 sys_borrow 表的主键)
        Borrow borrow = this.getById(id);

        // 【调试技巧】如果依然报错，请在控制台看打印出来的 ID 到底是什么
        if (borrow == null) {
            log.error("归还操作失败：未找到记录。传入的 recordId 为: {}", id );
            throw new ServiceException("未找到 ID 为 " + id + " 的借阅记录，请检查参数是否为借阅记录ID而非图书ID");
        }

        // 2. 状态校验：防止重复归还
        if ("returned".equals(borrow.getStatus())) {
            throw new ServiceException("该记录已归还，请勿重复操作");
        }

        // 3. 更新借阅记录状态
        borrow.setStatus("returned");
        borrow.setReturnTime(LocalDateTime.now());
        // 注意：updateById 是根据 borrow 对象的 id 属性进行更新的
        boolean updateBorrowSuccess = this.updateById(borrow);
        if (!updateBorrowSuccess) {
            throw new ServiceException("借阅记录状态更新失败");
        }

        // 4. 恢复图书库存 (sys_book)
        // 借阅记录里存有 bookId，直接取出来用
        Long bookId = borrow.getBookId();
        boolean updateBookSuccess = bookService.update()
                .setSql("stock = stock + 1")
                .eq("id", bookId)
                .update();

        if (!updateBookSuccess) {
            // 如果库存更新失败（比如书被删了），事务会回滚
            throw new ServiceException("恢复图书库存失败，该图书可能已被移除");
        }

        log.warn("用户 {} 成功归还图书 ID: {}, 借阅记录 ID: {}", borrow.getUserId(), bookId, id);
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
    public  List<BookHotVO> getHotBooks(Integer topNum) {
        return bookMapper.selectHotBooks(topNum);
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