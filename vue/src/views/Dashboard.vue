<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in cards" :key="item.title">
        <el-card class="box-card">
          <div slot="header" class="clearfix">{{ item.title }}</div>
          <div class="text item">
            <svg class="icon" aria-hidden="true">
              <use :xlink:href="item.icon" style="width: 100px"></use>
            </svg>
            <span class="text">{{ item.data }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card class="box-card">
          <div slot="header" class="clearfix">{{ item.title }}</div>
          <div class="text item">
            <span class="text">{{ item.data }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div id="myTimer" style="margin-left: 15px;font-weight: 550;"></div>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>热门借阅图书 TOP10</span>
            </div>
          </template>
          <el-table :data="topBooks" style="width: 100%">
            <el-table-column prop="name" label="书名" />
            <el-table-column prop="author" label="作者" />
            <el-table-column prop="borrownum" label="借阅次数" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>用户借阅次数 TOP10</span>
            </div>
          </template>
          <el-table :data="topUsers" style="width: 100%">
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="lendCount" label="借阅次数" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <div id="main" style="margin-left: 5px; margin-top: 20px"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {ElMessage} from "element-plus";
import request from "../utils/request";

export default {
  data() {
    return {
      cards: [
        { title: '已借阅', data: 0, icon: '#iconlend-record-pro' },
        { title: '总访问', data: 0, icon: '#iconvisit'   },
        { title: '图书数', data: 0, icon: '#iconbook-pro' },
        { title: '用户数', data: 0, icon: '#iconpopulation' }
      ],
      statCards: [
        { title: '本月借阅', data: 0 },
        { title: '本月归还', data: 0 },
        { title: '总借阅', data: 0 },
        { title: '总逾期', data: 0 }
      ],
      topBooks: [],
      topUsers: [],
      data:{}
    }
  },
  mounted() {
    this.circleTimer()
    this.loadDashboard()
    this.loadStatistics()
    this.loadTopBooks()
    this.loadTopUsers()
  },
  methods: {
    loadDashboard() {
      request.get("/dashboard").then(res=>{
        if(res.code == 0)
        {
          this.cards[0].data = res.data.lendRecordCount
          this.cards[1].data = res.data.visitCount
          this.cards[2].data = res.data.bookCount
          this.cards[3].data = res.data.userCount
          this.initChart()
        }
        else
        {
          ElMessage.error(res.msg)
        }
      })
    },
    loadStatistics() {
      request.get("/dashboard/statistics").then(res=>{
        if(res.code == 0)
        {
          this.statCards[0].data = res.data.monthLend
          this.statCards[1].data = res.data.monthReturn
          this.statCards[2].data = res.data.totalLend
          this.statCards[3].data = res.data.totalOverdue
        }
      })
    },
    loadTopBooks() {
      request.get("/dashboard/topBooks?limit=10").then(res=>{
        if(res.code == 0)
        {
          this.topBooks = res.data
        }
      })
    },
    loadTopUsers() {
      request.get("/dashboard/topUsers?limit=10").then(res=>{
        if(res.code == 0)
        {
          this.topUsers = res.data
        }
      })
    },
    initChart() {
      var myChart = echarts.init(document.getElementById('main'))
      myChart.setOption({
        title: {
          text: '系统统计'
        },
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.cards.map(item => item.title),
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            type: 'bar',
            label: { show: true },
            barWidth: '25%',
            data: [
              { value: this.cards[0].data, itemStyle: { color: '#5470c6' } },
              { value: this.cards[1].data, itemStyle: { color: '#91cc75' } },
              { value: this.cards[2].data, itemStyle: { color: '#fac858' } },
              { value: this.cards[3].data, itemStyle: { color: '#ee6666' } }
            ]
          }
        ]
      })
      window.addEventListener('resize', () => {
        myChart.resize()
      })
    },
    circleTimer() {
      this.getTimer()
      setInterval(() => {
        this.getTimer()
      }, 1000)
    },
    getTimer() {
      var d = new Date()
      var t = d.toLocaleString()
      document.getElementById('myTimer').innerHTML = t
    }
  }
}
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
</style>
