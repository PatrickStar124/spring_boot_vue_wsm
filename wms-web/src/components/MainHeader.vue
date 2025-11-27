<template>
  <div style="display: flex;line-height: 60px">
    <div style="margin-top: 8px;">
      <i class="el-icon-s-fold" style="font-size: 20px"></i>
    </div>
    <div style="flex: 1;text-align: center;font-size: 34px">
      <span>欢迎来到仓库管理系统</span>
    </div>
    <el-dropdown>
      <span>王小虎</span><i class="el-icon-arrow-down" style="margin-left: 5px;"></i>

      <!-- 错误修复: 将 v-slot:dropdown 移到 <template> 标签上 -->
      <template v-slot:dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="toUser">个人中心</el-dropdown-item>
          <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>

    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: "MainHeader",
  methods:{
    toUser(){
      console.log('to_user')
    },
    logout(){
      console.log('logout')
    }
  }
}
</script>

<style scoped>
</style>