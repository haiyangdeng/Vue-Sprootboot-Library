<template>
    <div class="container">
        <h2 style="padding: 30px">个人信息</h2>
        <el-form :model="form" label-width="80px">
            <el-form-item label="用户名">
                <el-input style="width: 80%" v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="姓名">
                <el-input style="width: 80%" v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="权限">
                <span v-if="form.role == 'admin'" style="margin: 5px">管理员</span>
                <span v-if="form.role == 'user'" style="margin: 5px">读者</span>
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
        <div style="text-align: center">
            <el-button type="primary" @click="update">保存</el-button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 定义组件名称（Vue3 中通常根据文件名自动生成，如需显式定义可用 defineOptions）
defineOptions({
    name: 'Person',
});

// 定义自定义事件 (替代 this.$emit)
const emit = defineEmits(['userInfo']);

// 响应式数据
// 直接从 sessionStorage 初始化，相当于原来的 created
const userInfo = userStore.userInfo;
const form = ref(userInfo);

// 更新方法
const update = () => {
    request.put('/user', form.value).then((res) => {
        console.log(res);
        // 兼容数字和字符串类型的 code
        if (res.code === '0' || res.code === 200) {
            ElMessage.success('更新成功');

            // 更新本地存储
            sessionStorage.setItem('user', JSON.stringify(form.value));

            // 触发父组件更新用户信息
            emit('userInfo');
        } else {
            ElMessage.error(res.msg || '更新失败');
        }
    });
};
</script>

<style scoped>
/* 建议加上 scoped 避免样式污染 */
.avatar-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}
.avatar-uploader:hover {
    border-color: #409eff;
}
.avatar {
    width: 178px;
    height: 178px;
    display: block;
}
.box-card {
    width: 60%;
    margin: auto;
    padding: 20px;
}
</style>
