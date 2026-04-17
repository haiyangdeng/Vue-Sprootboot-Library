<template>
    <div class="header-wrap">
        <div class="logo-wrap">
            <img :src="imgUrl" class="icon" />
            <h1>图书馆管理系统</h1>
        </div>
        <div style="flex: 1"></div>
        <div style="width: 200px; padding-right: 20px; display: flex; align-items: center; justify-content: flex-end">
            <el-dropdown>
                <span class="el-dropdown-link">
                    <el-avatar :size="30" :src="circleUrl" style="margin-right: 10px" />
                    {{ userStore.userInfo.username || '未登录' }}
                    <el-icon class="el-icon--right">
                        <arrow-down />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="handleExit">退出系统</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowDown } from '@element-plus/icons-vue';
import logoImg from '@/assets/icon/logo.png';
import avatarImg from '@/assets/icon/avatar.png';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

const imgUrl = ref(logoImg);
const circleUrl = ref(avatarImg);

onMounted(() => {
    console.log(userStore.userInfo, 'userStore');

    if (!userStore.userInfo.username) {
        const userStr = sessionStorage.getItem('user');
        if (userStr) {
            const d = JSON.parse(userStr);
            userStore.setUser(d.userInfo);
        }
    }
});

const handleExit = () => {
    sessionStorage.removeItem('user');

    router.push('/login');
    ElMessage.success('退出系统成功');
};
</script>

<style scoped lang="scss">
.header-wrap {
    height: 60px;
    line-height: 60px;
    display: flex;
    background-color: #10111c;

    .logo-wrap {
        width: 300px;
        padding-left: 30px;
        font-weight: bold;
        display: flex;
        align-items: center;

        h1 {
            margin-left: 20px;
            font-size: 20px;
            color: #fff;
        }
    }
}
.icon {
    width: 40px;
    height: 40px;
    padding-top: 5px;
    padding-right: 10px;
}

.el-dropdown-link {
    cursor: pointer;
    color: #fff;
    display: flex;
    align-items: center;
}
</style>
