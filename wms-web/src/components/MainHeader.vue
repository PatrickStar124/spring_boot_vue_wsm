<template>
  <div>
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