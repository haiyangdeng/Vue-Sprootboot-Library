<template>
    <div class="container">
        <div style="margin: 10px 0">
            <el-form :inline="true">
                <el-form-item label="用户名">
                    <el-input v-model="search" placeholder="请输入用户名" clearable>
                        <template #prefix>
                            <el-icon class="el-input__icon"><search /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="margin-left: 1%" @click="load">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="danger" @click="clear">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <el-table :data="tableData" stripe border>
            <el-table-column prop="id" label="ID" sortable />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="operation" label="操作" />
            <el-table-column prop="method" label="方法" />
            <el-table-column prop="ip" label="IP地址" />
            <el-table-column prop="createDate" label="操作时间" />
        </el-table>

        <div style="margin: 10px 0">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[5, 10, 20]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../utils/request';
import { Search } from '@element-plus/icons-vue'; // 必须显式导入图标组件

// 声明响应式数据
const search = ref('');
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const tableData = ref([]);

// 定义方法
const load = () => {
    request
        .get('/sysLog/page', {
            params: {
                pageNum: currentPage.value, // JS 中使用 ref 必须加 .value
                pageSize: pageSize.value,
                search: search.value,
            },
        })
        .then((res) => {
            // 确保你的拦截器返回的是 res.data
            tableData.value = res.data.records;
            total.value = res.data.total;
        });
};

const clear = () => {
    search.value = '';
    load();
};

const handleSizeChange = (val) => {
    pageSize.value = val;
    load();
};

const handleCurrentChange = (val) => {
    currentPage.value = val;
    load();
};

// 替代原来的 created 钩子
onMounted(() => {
    load();
});
</script>
