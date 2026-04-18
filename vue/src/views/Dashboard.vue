<template>
    <div class="container">
        <div>
            <el-row :gutter="20">
                <el-col :span="8" v-for="item in cards" :key="item.title">
                    <el-card class="box-card">
                        <template #header>
                            <div class="clearfix">{{ item.title }}</div>
                        </template>
                        <div class="text item">
                            <svg class="icon" aria-hidden="true">
                                <use :xlink:href="item.icon" style="width: 100px"></use>
                            </svg>
                            <span class="text">{{ item.data }}</span>
                        </div>
                    </el-card>
                </el-col>
            </el-row>

            <div id="myTimer" style="margin-left: 15px; font-weight: 550">{{ currentTime }}</div>

            <el-row :gutter="20" style="margin-top: 20px">
                <el-col :span="12">
                    <el-card class="box-card">
                        <template #header>
                            <div class="clearfix">
                                <span>热门借阅图书 TOP5</span>
                            </div>
                        </template>
                        <el-table :data="topBooks" style="width: 100%">
                            <el-table-column prop="name" label="书名" />
                            <el-table-column prop="author" label="作者" />
                            <el-table-column prop="borrowNum" label="借阅次数" />
                        </el-table>
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card class="box-card">
                        <template #header>
                            <div class="clearfix">
                                <span>用户借阅次数 TOP5</span>
                            </div>
                        </template>
                        <el-table :data="topUsers" style="width: 100%">
                            <el-table-column prop="name" label="用户名" />
                            <el-table-column prop="value" label="借阅次数" />
                        </el-table>
                    </el-card>
                </el-col>
            </el-row>
            <div ref="chartRef" id="main" style="margin-left: 5px; margin-top: 20px"></div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, shallowRef } from 'vue';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import request from '../utils/request';

// 1. 响应式数据
const cards = reactive([
    { title: '已借阅', field: 'borrowCount', data: 0, icon: '#iconlend-record-pro' },
    { title: '图书数', field: 'bookCount', data: 0, icon: '#iconbook-pro' },
    { title: '用户数', field: 'userCount', data: 0, icon: '#iconpopulation' },
]);

const topBooks = ref([]);
const topUsers = ref([]);
const currentTime = ref('');
const chartRef = ref(null);
let timer = null;

/** * 重要：使用 shallowRef 存储 ECharts 实例。
 * 因为 ECharts 实例是一个复杂的深层对象，Vue 的响应式代理（Proxy）会破坏其内部逻辑导致报错或性能下降。
 */
const myChart = shallowRef(null);

// 2. 方法定义
const getTimer = () => {
    currentTime.value = new Date().toLocaleString();
};

const loadDashboard = () => {
    request.get('/dashboard/statistics').then((res) => {
        if (res.code == 200 || res.code == 0) {
            cards[0].data = res.data.borrowCount;
            cards[1].data = res.data.bookCount;
            cards[2].data = res.data.userCount;
            // 获取数据后再初始化或更新图表
            initChart();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

const loadTopBooks = () => {
    request.get('/dashboard/topBooks?limit=5').then((res) => {
        if (res.code == 200 || res.code == 0) topBooks.value = res.data;
    });
};

const loadTopUsers = () => {
    request.get('/dashboard/topUsers?limit=5').then((res) => {
        if (res.code == 200 || res.code == 0) topUsers.value = res.data;
    });
};

const initChart = () => {
    if (!chartRef.value) return;

    // 如果实例不存在则初始化
    if (!myChart.value) {
        myChart.value = echarts.init(chartRef.value);
    }

    const option = {
        title: { text: '系统统计' },
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
            type: 'category',
            data: cards.map((item) => item.title),
            axisTick: { alignWithLabel: true },
        },
        yAxis: { type: 'value' },
        series: [
            {
                type: 'bar',
                label: { show: true },
                barWidth: '25%',
                data: [
                    { value: cards[0].data, itemStyle: { color: '#5470c6' } },
                    { value: cards[1].data, itemStyle: { color: '#91cc75' } },
                    { value: cards[2].data, itemStyle: { color: '#fac858' } },
                ],
            },
        ],
    };
    myChart.value.setOption(option);
};

const handleResize = () => {
    myChart.value?.resize();
};

// 3. 生命周期
onMounted(() => {
    getTimer();
    timer = setInterval(getTimer, 1000);

    loadDashboard();
    loadTopBooks();
    loadTopUsers();

    window.addEventListener('resize', handleResize);
});

// 卸载时清理，防止内存泄漏
onUnmounted(() => {
    if (timer) clearInterval(timer);
    window.removeEventListener('resize', handleResize);
    myChart.value?.dispose();
});
</script>

<style scoped>
.box-card {
    width: 80%;
    margin-bottom: 25px;
    margin-left: 10px;
}

.clearfix {
    text-align: center;
    font-size: 15px;
}

.text {
    text-align: center;
    font-size: 24px;
    font-weight: 700;
    vertical-align: super;
}

#main {
    width: 100%;
    height: 450px;
    margin-top: 20px;
}

.icon {
    width: 50px;
    height: 50px;
    padding-top: 5px;
    padding-right: 10px;
}

.el-card {
    width: 100%;
}
</style>
