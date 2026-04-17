<template>
    <div class="container">
        <el-form ref="formRef" :model="form" status-icon :rules="rules" label-width="100px" class="demo-ruleForm">
            <el-form-item label="老密码" prop="password2">
                <el-input v-model="form.password2" type="password" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="password">
                <el-input v-model="form2.password" type="password" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="checkpassword">
                <el-input v-model="form.checkpassword" type="password" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submitForm">提交</el-button>
                <el-button @click="resetForm">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { ElMessage } from 'element-plus';

const router = useRouter();
const formRef = ref(null); // 获取表单 DOM 实例

// 基础数据
const form = reactive({
    password2: '',
    checkpassword: '',
    truepassword: '', // 保存当前登录用户的真实密码（从缓存获取）
});

const form2 = reactive({
    password: '',
    id: 0,
});

// 自定义校验规则
const validatePass2 = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入老密码'));
    } else {
        if (value !== form.truepassword) {
            callback(new Error('密码错误'));
        } else {
            callback();
        }
    }
};

const validatePass = (rule, value, callback) => {
    // 注意：这里由于 v-model 绑定的是 form2.password，校验时需要手动检查
    if (form2.password === '') {
        callback(new Error('请输入新密码'));
    } else {
        callback();
    }
};

const validatePass3 = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'));
    } else if (value !== form2.password) {
        callback(new Error('两次输入密码不匹配'));
    } else {
        callback();
    }
};

const rules = {
    password: [{ validator: validatePass, trigger: 'blur', required: true }],
    checkpassword: [{ validator: validatePass3, trigger: 'blur', required: true }],
    password2: [{ validator: validatePass2, trigger: 'blur', required: true }],
};

// 初始化获取用户信息
onMounted(() => {
    const userStr = sessionStorage.getItem('user');
    if (userStr) {
        const user = JSON.parse(userStr);
        form.truepassword = user.password;
        form2.id = user.id;
    }
});

// 提交表单
const submitForm = () => {
    if (!formRef.value) return;

    formRef.value.validate((valid) => {
        if (valid) {
            request.put('/user', form2).then((res) => {
                if (res.code == 200 || res.code === '0') {
                    ElMessage.success('密码修改成功, 请重新登录');
                    sessionStorage.removeItem('user');
                    router.push('/login');
                } else {
                    ElMessage.error(res.msg);
                }
            });
        }
    });
};

// 重置表单
const resetForm = () => {
    if (!formRef.value) return;
    formRef.value.resetFields();
    form2.password = ''; // 需手动清空不在 form 里的字段
};
</script>
