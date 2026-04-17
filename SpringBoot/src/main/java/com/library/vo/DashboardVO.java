package com.library.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    private Long bookCount;      // 图书总数
    private Long userCount;      // 用户总数
    private Long borrowCount;    // 总借阅次数
    private Long pendingReturn;  // 待还书数量

    // 还可以增加图表数据，例如最近7天的借阅趋势
    private List<Map<String, Object>> lineData;
}