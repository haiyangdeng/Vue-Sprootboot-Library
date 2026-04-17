<template>
    <div class="container">
        <div style="margin: 10px">
            <el-form :inline="true">
                <el-form-item label="分类名称">
                    <el-input v-model="search" placeholder="请输入分类名称" clearable @keyup.enter="load">
                        <template #prefix>
                            <el-icon class="el-input__icon"><Search /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="load">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="danger" @click="clear">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin: 10px">
            <el-button type="primary" @click="add">新增分类</el-button>
            <el-popconfirm title="确认删除?" @confirm="deleteBatch">
                <template #reference>
                    <el-button type="danger">批量删除</el-button>
                </template>
            </el-popconfirm>
        </div>

        <el-table :data="tableData" stripe border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="ID" sortable />
            <el-table-column prop="code" label="分类编码" />
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="remark" label="分类描述" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column fixed="right" label="操作" width="160">
                <template #default="scope">
                    <el-button @click="handleEdit(scope.row)">编辑</el-button>
                    <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row.id)">
                        <template #reference>
                            <el-button type="danger">删除</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <div style="margin: 10px">
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

        <el-dialog v-model="dialogVisible" title="分类信息" width="30%">
            <el-form :model="form" ref="ruleFormRef" :rules="rules" label-width="120px">
                <el-form-item label="分类编号">
                    <el-input v-model="form.code" style="width: 80%" disabled placeholder="自动生成"></el-input>
                </el-form-item>
                <el-form-item label="分类名称" prop="name">
                    <el-input v-model="form.name" style="width: 80%"></el-input>
                </el-form-item>
                <el-form-item label="分类描述">
                    <el-input v-model="form.remark" type="textarea" style="width: 80%"></el-input>
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
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import request from '../utils/request';

// --- 数据定义 ---
const search = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const tableData = ref([]);
const ids = ref([]);
const dialogVisible = ref(false);
const ruleFormRef = ref(null);

const form = ref({
    code: '',
    name: '',
    remark: '',
});
const rules = ref({
    name: [{ required: true, message: '请输入', trigger: ['change', 'blur'] }],
});

// --- 方法逻辑 ---

// 加载列表
const load = () => {
    request
        .get('/category/list', {
            params: {
                pageNum: currentPage.value,
                pageSize: pageSize.value,
                name: search.value,
            },
        })
        .then((res) => {
            tableData.value = res.data.records || [];
            total.value = res.data.total || 0;
        });
};

// 重置查询
const clear = () => {
    search.value = '';
    load();
};

// 新增按钮
const add = () => {
    form.value = {};
    dialogVisible.value = true;
};

// 编辑按钮
const handleEdit = (row) => {
    // 深拷贝，避免修改表单时影响表格行数据
    form.value = JSON.parse(JSON.stringify(row));
    dialogVisible.value = true;
};

// 保存逻辑（新增或更新）
const save = async () => {
    await ruleFormRef.value.validate((valid, fields) => {
        if (valid) {
            if (form.value.id) {
                // 更新
                request.put('/category/update', form.value).then((res) => {
                    if (res.code == 200) {
                        ElMessage.success('更新成功');
                        dialogVisible.value = false;
                        load();
                    } else {
                        ElMessage.error(res.msg);
                    }
                });
            } else {
                // 新增
                request.post('/category/add', form.value).then((res) => {
                    if (res.code == 200) {
                        ElMessage.success('添加成功');
                        dialogVisible.value = false;
                        load();
                    } else {
                        ElMessage.error(res.msg);
                    }
                });
            }
        } else {
            console.log('error submit!', fields);
        }
    });
};

// 单行删除
const handleDelete = (id) => {
    request.delete(`/category/delete/${id}`).then((res) => {
        if (res.code == 200) {
            ElMessage.success('删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 多选改变
const handleSelectionChange = (val) => {
    ids.value = val.map((v) => v.id);
};

// 批量删除
const deleteBatch = () => {
    if (!ids.value.length) {
        ElMessage.warning('请选择数据！');
        return;
    }
    request.post('/category/deleteBatch', ids.value).then((res) => {
        // 注意：你原代码这里判断的是 res.code === '0'，请确保后端逻辑一致
        if (res.code === '0' || res.code == 200) {
            ElMessage.success('批量删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 分页大小改变
const handleSizeChange = (val) => {
    pageSize.value = val;
    load();
};

// 页码改变
const handleCurrentChange = (val) => {
    currentPage.value = val;
    load();
};

// --- 生命周期 ---
onMounted(() => {
    load();
});
</script>

<style scoped>
.icon {
    width: 1em;
    height: 1em;
    margin-right: 8px;
}
</style>
