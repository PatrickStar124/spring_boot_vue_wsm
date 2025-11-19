<template>
  <div id="app">
    <MainIndex></MainIndex> <!-- 组件名同步修改 -->
  </div>
</template>

<script>
import MainIndex from "@/components/MainIndex.vue"; // 路径和组件名同步修改

export default {
  name: 'App',
  components: {
    MainIndex // 注册名同步修改
  }
};
</script>