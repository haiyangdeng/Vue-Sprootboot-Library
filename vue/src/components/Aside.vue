<template>
    <div>
        <el-menu
            style="width: 200px; min-height: calc(100vh - 50px)"
            :default-active="activePath"
            class="el-menu-vertical-demo"
            router
            background-color="#30333c"
            text-color="#fff"
        >
            <el-menu-item index="/dashboard">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icondashboard "></use>
                </svg>
                <span>展示板</span>
            </el-menu-item>

            <el-sub-menu index="2">
                <template #title>
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-mingpian "></use>
                    </svg>
                    <span>个人信息</span>
                </template>
                <el-menu-item index="/person">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-a-bianji1 "></use>
                    </svg>
                    <span>修改个人信息</span>
                </el-menu-item>
                <el-menu-item index="/password">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-mima "></use>
                    </svg>
                    <span>修改密码</span>
                </el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/user" v-if="userStore.userInfo.role === 'admin'">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconreader "></use>
                </svg>
                <span>读者管理</span>
            </el-menu-item>

            <el-menu-item index="/book" v-if="userStore.userInfo.role === 'admin'">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconbook "></use>
                </svg>
                <span>书籍管理</span>
            </el-menu-item>

            <el-menu-item index="/category" v-if="userStore.userInfo.role === 'admin'">
                <el-icon><Menu /></el-icon>
                <span>分类管理</span>
            </el-menu-item>

            <el-menu-item index="/book" v-if="userStore.userInfo.role === 'user'">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconbook "></use>
                </svg>
                <span>图书查询</span>
            </el-menu-item>

            <el-menu-item index="/lendrecord" v-if="userStore.userInfo.role === 'admin'">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconlend-record "></use>
                </svg>
                <span>借阅管理</span>
            </el-menu-item>

            <el-menu-item index="/lendrecord" v-if="userStore.userInfo.role === 'user'">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconlend-record"></use>
                </svg>
                <span>借阅信息</span>
            </el-menu-item>

            <el-menu-item index="/bookwithuser">
                <el-icon><Grid /></el-icon>
                <span>借阅状态</span>
            </el-menu-item>

            <el-menu-item index="/syslog" v-if="userStore.userInfo.role === 'admin'">
               <el-icon><List /></el-icon>
                <span>操作日志</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const userStore = useUserStore();

const activePath = computed(() => route.path);
</script>

<style scoped>
.icon {
    width: 30px;
    height: 30px;
    padding-top: 5px;
    padding-right: 10px;
}

/* 修复 el-sub-menu 的文字颜色，Element Plus 建议通过 css 变量或 class 修改 */
:deep(.el-sub-menu__title) {
    color: #fff !important;
}
</style>
