<template>
    <div class="container">
        <div style="margin: 10px 0">
            <el-form :inline="true">
                <el-form-item label="图书编号">
                    <el-input v-model="bookIsbn" placeholder="请输入图书编号" clearable>
                        <template #prefix
                            ><el-icon><search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="图书名称">
                    <el-input v-model="bookName" placeholder="请输入图书名称" clearable>
                        <template #prefix
                            ><el-icon><search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="读者">
                    <el-input v-model="userName" placeholder="请输入读者编号" clearable>
                        <template #prefix
                            ><el-icon><search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="load">查询</el-button>
                    <el-button type="danger" @click="clear">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin: 10px 0" v-if="userStore.userInfo.role == 'admin'">
            <el-popconfirm title="确认删除?" @confirm="deleteBatch">
                <template #reference>
                    <el-button type="danger">批量删除</el-button>
                </template>
            </el-popconfirm>
        </div>

        <el-table :data="tableData" stripe border @selection-change="handleSelectionChange">
            <el-table-column v-if="userStore.userInfo.role == 'admin'" type="selection" width="55" />
            <el-table-column prop="bookIsbn" label="图书编号" sortable />
            <el-table-column prop="bookName" label="图书名称" />
            <el-table-column prop="userName" label="读者" sortable />
            <el-table-column prop="borrowTime" label="借阅时间" sortable />
            <el-table-column prop="returnTime" label="归还时间" sortable />
            <el-table-column prop="status" label="状态">
                <template #default="scope">
                    <el-tag v-if="scope.row.status == 'unreturned'" type="warning">未归还</el-tag>
                    <el-tag v-if="scope.row.status == 'returned'" type="success">已归还</el-tag>
                    <el-tag v-if="scope.row.status == 'overdue'" type="danger">逾期 - {{ scope.row.overdueDays }}天</el-tag>
                </template>
            </el-table-column>
            <el-table-column v-if="user.role === 1" label="操作" width="200px">
                <template #default="scope">
                    <el-button @click="handleEdit(scope.row)">编辑</el-button>
                    <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row)">
                        <template #reference>
                            <el-button type="danger">删除</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
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

        <el-dialog v-model="dialogVisible" title="修改借阅记录" width="30%">
            <el-form :model="form" label-width="120px">
                <el-form-item label="借阅时间">
                    <el-date-picker v-model="form.borrowTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择日期时间" />
                </el-form-item>
                <el-form-item label="归还时间">
                    <el-date-picker v-model="form.returnTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择日期时间" />
                </el-form-item>
                <el-form-item label="是否归还">
                    <el-radio-group v-model="form.status">
                        <el-radio :label="0">未归还</el-radio>
                        <el-radio :label="1">已归还</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="save">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../utils/request';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 响应式状态
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'));
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const bookIsbn = ref('');
const bookName = ref('');
const userName = ref('');
const dialogVisible = ref(false);
const form = ref({});
const forms = ref([]); // 用于批量选择

// 加载数据
const load = () => {
    request
        .get('/borrow/list', {
            params: {
                pageNum: currentPage.value,
                pageSize: pageSize.value,
                bookIsbn: bookIsbn.value,
                bookName: bookName.value,
                userName: userName.value,
            },
        })
        .then((res) => {
            tableData.value = res.data.records;
            total.value = res.data.total;
        });
};

// 重置查询
const clear = () => {
    bookIsbn.value = '';
    bookName.value = '';
    userName.value = '';
    load();
};

// 批量选择
const handleSelectionChange = (val) => {
    forms.value = val;
};

// 批量删除
const deleteBatch = () => {
    if (!forms.value.length) {
        ElMessage.warning('请选择数据！');
        return;
    }
    request.post('/borrow/deleteRecords', forms.value).then((res) => {
        if (res.code === '0' || res.code === 200) {
            ElMessage.success('批量删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 编辑弹窗
const handleEdit = (row) => {
    form.value = JSON.parse(JSON.stringify(row));
    dialogVisible.value = true;
};

// 保存/更新
const save = () => {
    // 修正原代码逻辑：通常 PUT 用于更新，且路径格式应一致
    if (form.value.id || form.value.isbn) {
        // 根据你的后端接口定义，这里假设更新用 PUT
        request.put('/borrow/' + form.value.isbn, form.value).then((res) => {
            if (res.code == 200) {
                ElMessage.success('更新成功');
                dialogVisible.value = false;
                load();
            } else {
                ElMessage.error(res.msg);
            }
        });
    } else {
        request.post('/borrow', form.value).then((res) => {
            if (res.code == 200) {
                ElMessage.success('新增成功');
                dialogVisible.value = false;
                load();
            } else {
                ElMessage.error(res.msg);
            }
        });
    }
};

// 单条删除
const handleDelete = (row) => {
    request.post('borrow/deleteRecord', row).then((res) => {
        if (res.code == 200) {
            ElMessage.success('删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 分页切换
const handleSizeChange = (val) => {
    pageSize.value = val;
    load();
};

const handleCurrentChange = (val) => {
    currentPage.value = val;
    load();
};

// 初始化加载
onMounted(() => {
    load();
});
</script>
