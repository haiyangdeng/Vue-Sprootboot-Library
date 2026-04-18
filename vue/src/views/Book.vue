<template>
    <div class="container">
        <div style="margin: 10px 0">
            <el-form :inline="true">
                <el-form-item label="图书编号">
                    <el-input v-model="isbn" placeholder="请输入图书编号" clearable>
                        <template #prefix
                            ><el-icon><Search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="图书名称">
                    <el-input v-model="bookname" placeholder="请输入图书名称" clearable>
                        <template #prefix
                            ><el-icon><Search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="作者">
                    <el-input v-model="author" placeholder="请输入作者" clearable>
                        <template #prefix
                            ><el-icon><Search /></el-icon
                        ></template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="load">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="danger" @click="clear">重置</el-button>
                </el-form-item>
                <el-form-item style="float: right" v-if="numOfOutDataBook !== 0">
                    <el-popconfirm confirm-button-text="查看" cancel-button-text="取消" title="您有图书已逾期，请尽快归还" @confirm="toLook">
                        <template #reference>
                            <el-button type="warning">逾期通知</el-button>
                        </template>
                    </el-popconfirm>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin: 10px 0">
            <el-button type="primary" @click="add" v-if="userStore.userInfo.role == 'admin'">上架</el-button>
            <el-popconfirm title="确认删除?" @confirm="deleteBatch" v-if="userStore.userInfo.role == 'admin'">
                <template #reference>
                    <el-button type="danger">批量删除</el-button>
                </template>
            </el-popconfirm>
        </div>

        <el-table :data="tableData" stripe border @selection-change="handleSelectionChange">
            <el-table-column v-if="userStore.userInfo.role == 'admin'" type="selection" width="55" />
            <el-table-column prop="isbn" label="图书编号" sortable />
            <el-table-column prop="name" label="图书名称" />
            <el-table-column prop="price" label="价格" sortable />
            <el-table-column prop="author" label="作者" />
            <el-table-column prop="publisher" label="出版社" />
            <el-table-column prop="publishTime" label="出版时间" sortable />
            <el-table-column prop="stock" label="库存" sortable />
            <el-table-column prop="statusName" label="状态">
                <template #default="scope">
                    <template v-if="userStore.userInfo.role == 'user'">
                        <el-tag :type="isbnArray.includes(scope.row.isbn) ? 'warning' : 'success'"> {{ isbnArray.includes(scope.row.isbn) ? '借阅中' : '在库' }}</el-tag>
                    </template>
                    <template v-if="userStore.userInfo.role == 'admin'">
                        <el-tag :type="scope.row.status == 'unreturned' ? 'warning' : 'success'">{{ scope.row.statusName }}</el-tag>
                    </template>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="200">
                <template #default="scope">
                    <template v-if="userStore.userInfo.role == 'admin'">
                        <el-button @click="handleEdit(scope.row)">修改</el-button>
                        <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row.id)">
                            <template #reference>
                                <el-button type="danger">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                    <template v-if="userStore.userInfo.role == 'user'">
                        <el-button @click="handlelend(scope.row)" :disabled="scope.row.stock <= 0 || isbnArray.includes(scope.row.isbn)">借阅</el-button>
                        <el-popconfirm title="确认还书?" @confirm="handlereturn(scope.row)">
                            <template #reference>
                                <el-button type="danger" :disabled="!isbnArray.includes(scope.row.isbn)">还书</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </template>
            </el-table-column>
        </el-table>

        <div style="margin: 10px 0">
            <el-pagination
                v-model:currentPage="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[5, 10, 20]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <el-dialog v-model="dialogVisible3" title="逾期详情" width="50%">
            <el-table :data="outDateBook" style="width: 100%">
                <el-table-column prop="isbn" label="图书编号" />
                <el-table-column prop="bookName" label="书名" />
                <el-table-column prop="lendtime" label="借阅日期" />
                <el-table-column prop="deadtime" label="截至日期" />
            </el-table>
            <template #footer>
                <el-button type="primary" @click="dialogVisible3 = false">确认</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="dialogVisible" :title="form.id ? '修改书籍信息' : '上架书籍'" width="30%">
            <el-form :model="form" ref="ruleFormRef" :rules="rules" label-width="120px">
                <el-form-item label="图书编号"><el-input v-model="form.isbn" style="width: 80%" /></el-form-item>
                <el-form-item label="图书名称" prop="name"><el-input v-model="form.name" style="width: 80%" /></el-form-item>
                <el-form-item label="价格"><el-input v-model="form.price" style="width: 80%" /></el-form-item>
                <el-form-item label="作者"><el-input v-model="form.author" style="width: 80%" /></el-form-item>
                <el-form-item label="出版社"><el-input v-model="form.publisher" style="width: 80%" /></el-form-item>
                <el-form-item label="出版时间">
                    <el-date-picker v-model="form.publishTime" type="date" value-format="YYYY-MM-DD" style="width: 80%" />
                </el-form-item>
                <el-form-item label="库存">
                    <el-input-number v-model="form.stock" :min="0" style="width: 80%" />
                </el-form-item>
                <el-form-item label="图书分类">
                    <el-select v-model="form.categoryId" placeholder="请选择" style="width: 240px" @change="handleCategoryId">
                        <el-option v-for="item in categoryOptions" :key="item.code" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { Search, InfoFilled } from '@element-plus/icons-vue';
import moment from 'moment';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 状态定义
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const isbn = ref('');
const bookname = ref('');
const author = ref('');
const ids = ref([]);

const dialogVisible = ref(false);
const dialogVisible3 = ref(false);

const form = ref({});
const outDateBook = ref([]);
const numOfOutDataBook = ref(0);
const isbnArray = ref([]);
const bookData = ref([]);
const number = ref(0);
const rules = reactive({
    name: [
        {
            required: true,
            message: '请输入',
            trigger: ['change', 'blur'],
        },
    ],
});
const ruleFormRef = ref(null);
let categoryOptions = reactive([]);

const init = async () => {
    getCategory();
    // 如果是普通用户，检查逾期情况

    await load();
};

// 加载图书分类
const getCategory = () => {
    request
        .get('/category/list', {
            params: {
                pageNum: 1,
                pageSize: 1000,
            },
        })
        .then((res) => {
            categoryOptions = res.data.records || [];
        });
};

// 加载数据
const load = async () => {
    numOfOutDataBook.value = 0;
    outDateBook.value = [];

    await request
        .get('/book/list', {
            params: {
                pageNum: currentPage.value,
                pageSize: pageSize.value,
                isbn: isbn.value,
                name: bookname.value,
                author: author.value,
            },
        })
        .then((res) => {
            tableData.value = res.data.records;
            total.value = res.data.total;
        });

    if (userStore.userInfo.role == 'user') {
        await checkUserBooks();
    }
};

const checkUserBooks = async () => {
    await request
        .get('/borrow/list', {
            params: {
                pageNum: '1',
                pageSize: total.value,
                userId: userStore.userInfo.id,
            },
        })
        .then((res) => {
            bookData.value = res.data.records;
            number.value = bookData.value.length;
            const nowDate = new Date();
            isbnArray.value = bookData.value.filter((book) => book.status == 'unreturned').map((book) => book.bookIsbn);

            outDateBook.value = bookData.value
                .filter((book) => {
                    return new Date(book.deadtime) < nowDate;
                })
                .map((book) => {
                    return {
                        isbn: book.isbn,
                        bookName: book.bookName,
                        deadtime: book.deadtime,
                        lendtime: book.lendtime,
                    };
                });

            numOfOutDataBook.value = outDateBook.value.length;

            console.log(bookData.value, isbnArray.value, numOfOutDataBook.value, numOfOutDataBook.value, 'bookData');
        });
};

// 搜索重置
const clear = () => {
    isbn.value = '';
    bookname.value = '';
    author.value = '';
    load();
};

const handleCategoryId = () => {};

// 操作逻辑
const handleSelectionChange = (val) => {
    ids.value = val.map((v) => v.id);
};

const deleteBatch = () => {
    if (!ids.value.length) {
        ElMessage.warning('请选择数据！');
        return;
    }
    request.post('/book/deleteBatch', ids.value).then((res) => {
        if (res.code === '0') {
            ElMessage.success('批量删除成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const handleDelete = (id) => {
    request.delete('/book/delete/' + id).then((res) => {
        if (res.code == 200) ElMessage.success('删除成功');
        else ElMessage.error(res.msg);
        load();
    });
};

const handleEdit = (row) => {
    form.value = JSON.parse(JSON.stringify(row));
    dialogVisible.ref = true; // 注意：这里原代码有两个dialog，我合并成了一个
    dialogVisible.value = true;
};

const add = () => {
    form.value = {};
    dialogVisible.value = true;
};

const save = async () => {
    await ruleFormRef.value.validate((valid, fields) => {
        if (valid) {
            if (form.value.id) {
                request.put('/book/update', form.value).then((res) => {
                    if (res.code == 200) ElMessage.success('修改成功');
                    else ElMessage.error(res.msg);
                    load();
                    dialogVisible.value = false;
                });
            } else {
                form.value.borrownum = 0;
                form.value.status = 1;
                request.post('/book/add', form.value).then((res) => {
                    if (res.code == 200) ElMessage.success('上架成功');
                    else ElMessage.error(res.msg);
                    load();
                    dialogVisible.value = false;
                });
            }
        } else {
            console.log('error submit!', fields);
        }
    });
};

const handlelend = (row) => {
    if (row.stock <= 0) return ElMessage.warning('库存不足');
    if (number.value >= 5) return ElMessage.warning('借阅已达上限');
    if (numOfOutDataBook.value > 0) return ElMessage.warning('请先归还逾期书籍');

    const lendForm = {
        bookId: row.id,
        userId: userStore.userInfo.id,
    };

    request.post('/LendRecord', lendForm).then((res) => {
        if (res.code == 200) {
            ElMessage.success('借阅成功');
            load(); // 刷新列表
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const handlereturn = (row) => {
    request.post(`/LendRecord/return/${row.recordId}`).then((res) => {
        if (res.code == 200) {
            ElMessage.success('还书成功');
            load(); // 刷新列表
        } else {
            this.$message.errror(res.msg);
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

const toLook = () => {
    dialogVisible3.value = true;
};

onMounted(() => {
    init();
});
</script>
