<template>
  <el-container style="height:100%; border: 1px solid #eee">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246);height:100%;">
      <MainAside></MainAside>
    </el-aside>

    <el-container style="height:100%;">
      <el-header style="text-align: right; font-size: 12px;height: 100%;">
         <MainHeader></MainHeader>
      </el-header>

      <el-main style="height:100%;">
       <AppMain></AppMain>
      </el-main>
    </el-container>
  </el-container>
</template>

<script >
import MainAside from "@/components/MainAside.vue";
import MainHeader from "@/components/MainHeader.vue";
import AppMain from "@/components/AppMain.vue";

export default {
  name:"IndexView",
  components: {AppMain, MainHeader, MainAside},

}
</script>

<style scoped>
  .el-header {
    background-color: #B3C0D1;
    color: #333;
    line-height: 60px;
  }
  .el-main{
    padding: 5px;
  }
  .el-aside {
    color: #333;
  }
</style>