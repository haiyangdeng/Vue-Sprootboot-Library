<template>
    <div class="container">
        <div style="margin: 10px 0">
            <el-form :inline="true">
                <el-form-item label="图书编号">
                    <el-input v-model="isbn" placeholder="请输入图书编号" clearable>
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
                <el-form-item label="借阅者" v-if="userStore.userInfo.role == 'admin'">
                    <el-input v-model="nickName" placeholder="请输入借阅者昵称" clearable>
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
            <el-table-column prop="isbn" label="图书编号" sortable />
            <el-table-column prop="bookName" label="图书名称" />
            <el-table-column prop="nickName" label="借阅者" />
            <el-table-column prop="status" label="状态">
                <template #default="scope">
                    <el-tag v-if="scope.row.status == 'unreturned'" type="warning">未归还</el-tag>
                    <el-tag v-if="scope.row.status == 'returned'" type="success">已归还</el-tag>
                    <el-tag v-if="scope.row.status == 'overdue'" type="danger">逾期 - {{ Math.abs(scope.row.remainingDays) }}天</el-tag>
                </template>
            </el-table-column>

            <el-table-column prop="lendtime" label="借阅时间" />
            <el-table-column prop="deadtime" label="最迟归还日期" />
            <el-table-column prop="prolong" label="可续借次数" />
            <el-table-column fixed="right" label="操作" width="180">
                <template #default="scope">
                    <!-- <el-button v-if="userStore.userInfo.role == 'admin'"  @click="handleEdit(scope.row)">修改</el-button>

                    <el-popconfirm v-if="userStore.userInfo.role == 'admin'" title="确认删除?" @confirm="handleDelete(scope.row)">
                        <template #reference>
                            <el-button type="danger"  style="margin-left: 5px">删除</el-button>
                        </template>
                    </el-popconfirm> -->

                    <el-popconfirm
                        v-if="userStore.userInfo.role == 'user'"
                        title="确认续借(续借一次延长30天)?"
                        @confirm="handlereProlong(scope.row)"
                        :disabled="scope.row.prolong == 0"
                    >
                        <template #reference>
                            <el-button type="warning" :disabled="scope.row.prolong == 0">续借</el-button>
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

        <el-dialog v-model="dialogVisible2" title="修改借阅信息" width="30%">
            <el-form :model="form" label-width="120px">
                <el-form-item label="图书编号">
                    <el-input style="width: 80%" v-model="form.isbn" disabled />
                </el-form-item>
                <el-form-item label="图书名称">
                    <el-input style="width: 80%" v-model="form.bookName" />
                </el-form-item>
                <el-form-item label="借阅者">
                    <el-input style="width: 80%" v-model="form.nickName" />
                </el-form-item>
                <el-form-item label="续借次数">
                    <el-input style="width: 80%" v-model="form.prolong" type="number" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible2 = false">取 消</el-button>
                    <el-button type="primary" @click="save">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../utils/request';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import moment from 'moment';
import { useUserStore } from '@/stores/user';

// 响应式数据
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const isbn = ref('');
const bookName = ref('');
const nickName = ref('');
const dialogVisible2 = ref(false);
const form = ref({});
const forms = ref([]); // 存储选中的行

const userStore = useUserStore();

// 加载数据
const load = () => {
    request
        .get('/bookWithUser/page', {
            params: {
                pageNum: currentPage.value,
                pageSize: pageSize.value,
                isbn: isbn.value,
                bookName: bookName.value,
                nickName: userStore.userInfo.role == 'admin' ? nickName.value : userStore.userInfo.id,
            },
        })
        .then((res) => {
            tableData.value = res.data.records;
            total.value = res.data.total;
        });
};

// 重置搜索
const clear = () => {
    isbn.value = '';
    bookName.value = '';
    nickName.value = '';
    load();
};

// 多选处理
const handleSelectionChange = (val) => {
    forms.value = val;
};

// 批量删除
const deleteBatch = () => {
    if (!forms.value.length) {
        ElMessage.warning('请选择数据！');
        return;
    }
    request.post('bookWithUser/deleteRecords', forms.value).then((res) => {
        if (res.code === '0' || res.code === 200 || res.code === 200) {
            ElMessage.success('批量删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 单个删除
const handleDelete = (row) => {
    request.post('bookWithUser/deleteRecord', row).then((res) => {
        if (res.code == 200 || res.code == 0) {
            ElMessage.success('删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 续借处理
const handlereProlong = (row) => {
    const rowCopy = JSON.parse(JSON.stringify(row));
    let nowDate = new Date(rowCopy.deadtime);
    nowDate.setDate(nowDate.getDate() + 30);

    rowCopy.deadtime = moment(nowDate).format('YYYY-MM-DD HH:mm:ss');
    rowCopy.prolong = rowCopy.prolong - 1;

    request.post('/bookWithUser/update', rowCopy).then((res) => {
        if (res.code == 200 || res.code == 0) {
            ElMessage.success('续借成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 修改保存
const save = () => {
    request.post('/bookWithUser', form.value).then((res) => {
        if (res.code == 200 || res.code == 0) {
            ElMessage.success('修改信息成功');
            dialogVisible2.value = false;
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 编辑按钮点击
const handleEdit = (row) => {
    form.value = JSON.parse(JSON.stringify(row));
    dialogVisible2.value = true;
};

// 分页逻辑
const handleSizeChange = (val) => {
    pageSize.value = val;
    load();
};

const handleCurrentChange = (val) => {
    currentPage.value = val;
    load();
};

// 生命周期
onMounted(() => {
    load();
});
</script>
