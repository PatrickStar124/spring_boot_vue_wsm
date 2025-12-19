<template>
  <div id="app">
    <!-- 路由出口：根据路由动态显示 views/ 目录下的页面 -->
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* 全局基础样式 1*/
#app {
  height: 100%;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

/* 确保页面全屏 */
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

/* 全局链接样式 */
a {
  color: #1890ff;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

/* 全局按钮基础样式 */
button {
  cursor: pointer;
  font-family: inherit;
}
</style>