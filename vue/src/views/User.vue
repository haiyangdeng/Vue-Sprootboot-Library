<template>
    <div class="container">
        <div style="margin: 10px 0">
            <el-form :inline="true">
                <el-form-item label="用户名">
                    <el-input v-model="username" placeholder="请输入姓名" clearable>
                        <template #prefix
                            ><el-icon><search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="nickname" placeholder="请输入姓名" clearable>
                        <template #prefix
                            ><el-icon><search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="电话号码">
                    <el-input v-model="phone" placeholder="请输入电话号码" clearable>
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

        <div style="margin: 10px 0">
            <el-popconfirm title="确认删除?" @confirm="deleteBatch" v-if="userStore.userInfo.role == 'admin'">
                <template #reference>
                    <el-button type="danger">批量删除</el-button>
                </template>
            </el-popconfirm>
        </div>

        <el-table :data="tableData" stripe border @selection-change="handleSelectionChange">
            <el-table-column v-if="userStore.userInfo.role == 'admin'" type="selection" width="55" />
            <el-table-column prop="id" label="读者编号" sortable />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="nickname" label="姓名" />
            <el-table-column prop="role" label="角色">
                <template #default="scope">
                    {{ scope.row.role == 'admin' ? '管理员' : scope.row.role == 'user' ? '读者' : '' }}
                </template>
            </el-table-column>
            <el-table-column prop="phone" label="电话号码" />
            <el-table-column prop="sex" label="性别">
                <template #default="scope">
                    {{ scope.row.sex == 1 ? '男' : scope.row.sex == 2 ? '女' : '' }}
                </template>
            </el-table-column>

            <el-table-column prop="address" label="地址" />
            <el-table-column fixed="right" label="操作" width="280">
                <template #default="scope">
                    <el-button @click="handleEdit(scope.row)">编辑</el-button>
                    <el-button type="warning" @click="handleResetPassword(scope.row.id)">重置密码</el-button>
                    <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row.id)">
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

        <el-dialog v-model="dialogVisible" title="编辑读者信息" width="30%">
            <el-form :model="form" label-width="120px">
                <el-form-item label="用户名">
                    <el-input style="width: 80%" v-model="form.username" />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input style="width: 80%" v-model="form.nickname" />
                </el-form-item>
                <el-form-item label="电话号码">
                    <el-input style="width: 80%" v-model="form.phone" />
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="form.sex">
                        <el-radio :value="1">男</el-radio>
                        <el-radio :value="2">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input type="textarea" style="width: 80%" v-model="form.address" />
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
import { ref, onMounted } from 'vue';
import request from '../utils/request';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue'; // 别忘了导入图标
import { useUserStore } from '@/stores/user';

// 响应式变量
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'));
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const nickname = ref('');
const username = ref('');
const phone = ref('');
const ids = ref([]);
const dialogVisible = ref(false);
const form = ref({});
const userStore = useUserStore();

// 方法定义
const load = () => {
    request
        .get('user/list', {
            params: {
                pageNum: currentPage.value,
                pageSize: pageSize.value,
                nickname: nickname.value,
                username: username.value,
                phone: phone.value,
            },
        })
        .then((res) => {
            tableData.value = res.data.records;
            total.value = res.data.total;
        });
};

const clear = () => {
    nickname.value = '';
    username.value = '';
    phone.value = '';
    load();
};

const handleSelectionChange = (val) => {
    ids.value = val.map((v) => v.id);
};

const deleteBatch = () => {
    if (!ids.value.length) {
        ElMessage.warning('请选择数据！');
        return;
    }
    request.post('/user/deleteBatch', ids.value).then((res) => {
        if (res.code === '0' || res.code === 200) {
            ElMessage.success('批量删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const handleDelete = (id) => {
    request.delete(`user/${id}`).then((res) => {
        if (res.code == 200) {
            ElMessage.success('删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const handleResetPassword = (id) => {
    request.put(`/user/resetPwd/${id}`).then((res) => {
        if (res.code == 200) {
            ElMessage.success('密码已重置为: 123456');
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const handleEdit = (row) => {
    form.value = JSON.parse(JSON.stringify(row));
    dialogVisible.value = true;
};

const save = () => {
    const method = form.value.id ? 'put' : 'post';
    const path = form.value.id ? 'update' : 'add';

    request[method](`/user/${path}`, form.value).then((res) => {
        if (res.code == 200) {
            ElMessage.success(form.value.id ? '更新成功' : '添加成功');
            dialogVisible.value = false;
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const handleSizeChange = (val) => {
    pageSize.value = val;
    load();
};

const handleCurrentChange = (val) => {
    currentPage.value = val;
    load();
};

// 生命周期钩子
onMounted(() => {
    load();
});
</script>
