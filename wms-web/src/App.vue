<template>
  <div id="app">
    <!-- 路由容器：所有匹配的路由组件都会在这里渲染 -->
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
  // 无需注册任何组件，路由会自动渲染匹配的组件（如 LoginForm、MainIndex 等）
};
</script>

<style>
#app {
  height: 100%; /* 保留全屏高度，确保组件能占满页面 */
  font-family: Avenir, Helvetica, Arial, sans-serif; /* 可选：添加全局字体样式 */
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>