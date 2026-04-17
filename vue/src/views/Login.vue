<template>
    <div class="login-container">
        <el-form ref="formRef" :model="form" :rules="rules" class="login-page">
            <h2 class="title" style="margin-bottom: 20px">系统登录</h2>
            <el-form-item prop="username">
                <el-input v-model="form.username" clearable>
                    <template #prefix>
                        <el-icon class="el-input__icon"><User /></el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input v-model="form.password" clearable show-password>
                    <template #prefix>
                        <el-icon class="el-input__icon"><Lock /></el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" style="width: 100%" @click="handleLogin">登 录</el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="text" @click="router.push('/register')">前往注册 >> </el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';
import { User, Lock } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);

const form = reactive({
    username: 'admin',
    password: '123456',
});

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const handleLogin = () => {
    if (!formRef.value) return;

    formRef.value.validate((valid) => {
        if (valid) {
            request
                .post('user/login', form)
                .then((res) => {
                    if (res.code === 200) {
                        ElMessage.success('登录成功');

                        userStore.setUser(res.data.userInfo);
                        userStore.setToken(res.data.token);

                        sessionStorage.setItem('user', JSON.stringify(res.data));
                        router.push('/dashboard');
                    } else {
                        ElMessage.error(res.msg || '登录失败');
                    }
                })
                .catch((err) => {
                    console.error(err);
                    ElMessage.error('网络错误');
                });
        }
    });
};
</script>

<style scoped>
.login-container {
    position: fixed;
    width: 100%;
    height: 100vh;
    background: url('@/img/bg2.svg');
    background-size: contain;
    overflow: hidden;
}
.login-page {
    border-radius: 5px;
    margin: 180px auto;
    width: 350px;
    padding: 35px 35px 15px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
}
</style>
