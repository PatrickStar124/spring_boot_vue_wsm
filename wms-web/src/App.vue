<template>
  <div id="app">
    <!-- 只有非登录页才显示MainIndex布局 -->
    <MainIndex v-if="!$route.meta.noLayout"></MainIndex>
    <!-- 登录页直接渲染，不显示  布局 -->
    <router-view v-else></router-view>
  </div>
</template>

<script>
import MainIndex from "./components/MainIndex.vue";

export default {
  name: 'App',
  components: {
    MainIndex
  }
}
</script>

<style>
#app {
  height: 100%;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
/* 确保页面全屏 */
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>