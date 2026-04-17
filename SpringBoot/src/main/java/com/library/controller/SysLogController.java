package com.library.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.common.annotation.Log;
import com.library.entity.SysLog;
import com.library.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysLog")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like("username", search).or().like("operation", search);
        }
        wrapper.orderByDesc("create_date");
        return Result.success(sysLogService.page(page, wrapper));
    }

    @DeleteMapping("/{id}")
    @Log("删除日志")
    public Result<?> delete(@PathVariable Long id) {
        sysLogService.removeById(id);
        return Result.success();
    }
}