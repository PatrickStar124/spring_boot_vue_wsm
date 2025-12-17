<template>
  <el-container style="height: 100%;">
    <el-aside :width="aside_width" style="height: 100vh; background-color: rgb(238, 241, 246);">
      <MainAside :is-collapse="isCollapse"></MainAside>
    </el-aside>

    <el-container>
      <el-header style="border-bottom: 1px solid #e6e6e6;">
        <MainHeader @do-collapse="doCollapse" :icon="icon"></MainHeader>
      </el-header>

      <el-main style="padding: 20px;">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>