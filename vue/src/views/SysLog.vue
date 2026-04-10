<template>
  <div class="home" style ="padding: 10px">
    <div style="margin: 10px 0;">
      <el-form inline="true" size="small">
        <el-form-item label="用户名" >
          <el-input v-model="search" placeholder="请输入用户名"  clearable>
            <template #prefix><el-icon class="el-input__icon"><search/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="margin-left: 1%" @click="load" size="mini">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button size="mini"  type="danger" @click="clear">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="tableData" stripe border="true">
      <el-table-column prop="id" label="ID" sortable />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="operation" label="操作" />
      <el-table-column prop="method" label="方法" />
      <el-table-column prop="ip" label="IP地址" />
      <el-table-column prop="createTime" label="操作时间" />
    </el-table>
    <div style="margin: 10px 0">
      <el-pagination
          v-model:currentPage="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
import request from "../utils/request";

export default {
  created(){
    this.load()
  },
  name: 'SysLog',
  methods: {
    load(){
      request.get("/sysLog",{
        params:{
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          search: this.search,
        }
      }).then(res =>{
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    clear(){
      this.search = ""
      this.load()
    },
    handleSizeChange(pageSize){
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum){
      this.currentPage = pageNum
      this.load()
    },
  },
  data() {
    return {
      search:'',
      total:0,
      currentPage:1,
      pageSize: 10,
      tableData: [],
    }
  },
}
</script>
