<template>
  <div id="app">
    <MainIndex></MainIndex>
  </div>
</template>

<script>

import MainIndex from "./components/MainIndex.vue";

export default {
  name: 'App',
  components: {
      MainIndex: MainIndex
  }
}
</script>

<style>
#app {
  height: 100%; /* 保留全屏高度，确保组件能占满页面 */
  font-family: Avenir, Helvetica, Arial, sans-serif; /* 可选：添加全局字体样式 */
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>