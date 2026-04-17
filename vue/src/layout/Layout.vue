<template>
    <Header />
    <!--主体-->
    <div style="display: flex; width: 100%; height: calc(100vh - 60px); overflow: hidden">
        <!--侧边栏-->
        <Aside />
        <!--内容区域-->

        <div class="main">
            <router-view />
        </div>
    </div>
</template>

<script>
import Header from '../components/Header.vue';
import Aside from '../components/Aside.vue';
export default {
    name: 'Layout',
    components: {
        Header,
        Aside,
    },
};
</script>
