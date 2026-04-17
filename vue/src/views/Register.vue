<template>
    <div class="login-container">
        <el-form ref="registerFormRef" :model="form" :rules="rules" class="login-page">
            <h2 class="title" style="margin-bottom: 20px">用户注册</h2>

            <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="请输入用户名" clearable>
                    <template #prefix>
                        <el-icon><User /></el-icon>
                    </template>
                </el-input>
            </el-form-item>

            <el-form-item prop="nickname">
                <el-input v-model="form.nickname" placeholder="请输入姓名" clearable>
                    <template #prefix>
                        <el-icon><User /></el-icon>
                    </template>
                </el-input>
            </el-form-item>

            <el-form-item prop="password">
                <el-input v-model="form.password" placeholder="请输入密码" clearable show-password>
                    <template #prefix>
                        <el-icon><Lock /></el-icon>
                    </template>
                </el-input>
            </el-form-item>

            <el-form-item prop="confirm">
                <el-input v-model="form.confirm" placeholder="请再次确认密码" clearable show-password>
                    <template #prefix>
                        <el-icon><Lock /></el-icon>
                    </template>
                </el-input>
            </el-form-item>

            <el-form-item prop="role">
                <el-radio-group v-model="form.role">
                    <el-radio label="2">读者</el-radio>
                    <el-radio label="1">管理员</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item prop="authorize" v-if="form.role === '1'">
                <el-input v-model="form.authorize" placeholder="请输入管理员注册码" clearable show-password>
                    <template #prefix>
                        <el-icon><Lock /></el-icon>
                    </template>
                </el-input>
            </el-form-item>

            <!-- <el-form-item>
                <div style="display: flex; gap: 10px">
                    <el-input v-model="form.validCode" style="width: 45%" placeholder="请输入验证码"></el-input>
                    <ValidCode @input="createValidCode" style="width: 50%" />
                </div>
            </el-form-item> -->

            <el-form-item>
                <el-button type="primary" style="width: 100%" @click="handleRegister">注 册</el-button>
            </el-form-item>

            <el-form-item>
                <el-button type="text" @click="router.push('/login')">前往登录>> </el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue'; // 需确保已安装图标库
import request from '../utils/request';
import ValidCode from '../components/Validate.vue';

// 路由实例
const router = useRouter();

// 表单引用
const registerFormRef = ref(null);

// 响应式表单数据
const form = reactive({
    username: '',
    nickname: '',
    password: '',
    confirm: '',
    role: '2', // 默认选中读者
    authorize: '',
    validCode: '',
});

// 图形验证码的值
const validCodeFromComponent = ref('');

// 校验规则
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 13, message: '长度要求为2到13位', trigger: 'blur' },
    ],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    confirm: [{ required: true, message: '请确认密码', trigger: 'blur' }],
    authorize: [{ required: true, message: '请输入注册码', trigger: 'blur' }],
};

// 接收子组件传递的验证码
const createValidCode = (data) => {
    validCodeFromComponent.value = data;
};

// 注册逻辑
const handleRegister = () => {
    if (!registerFormRef.value) return;

    registerFormRef.value.validate((valid) => {
        if (valid) {
            // // 1. 验证码非空校验
            // if (!form.validCode) {
            //     ElMessage.error('请填写验证码');
            //     return;
            // }
            // 2. 验证码正确性校验 (不区分大小写)
            // if (form.validCode.toLowerCase() !== validCodeFromComponent.value.toLowerCase()) {
            //     ElMessage.error('验证码错误');
            //     return;
            // }
            // 3. 密码一致性校验
            if (form.password !== form.confirm) {
                ElMessage.error('两次密码输入不一致');
                return;
            }
            // 4. 管理员注册码校验
            if (form.role === '1' && form.authorize !== '1234') {
                ElMessage.error('请输入正确的注册码');
                return;
            }

            // 5. 发送请求
            request
                .post('user/register', form)
                .then((res) => {
                    if (res.code === 200) {
                        ElMessage.success('注册成功');
                        router.push('/login');
                    } else {
                        ElMessage.error(res.msg || '注册失败');
                    }
                })
                .catch((err) => {
                    console.error(err);
                });
        }
    });
};
</script>

<style scoped>
.login-container {
    position: fixed;
    width: 100%;
    height: 100%;
    background: url('../img/bg2.svg');
    background-size: contain;
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
