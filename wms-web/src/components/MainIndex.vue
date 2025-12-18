<template>
  <el-container style="height: 100%; border: 1px solid #eee">
    <el-aside :width="aside_width" style="height: 100vh;background-color: rgb(238, 241, 246);margin-left: -1px;">
      <MainAside :is-collapse="isCollapse"></MainAside>
    </el-aside>

    <el-container style="height: 100%;">
      <el-header style="text-align: right; font-size: 12px;height: 100%;border-bottom: rgba(168,168,168,0.3) 1px solid;">
        <MainHeader @do-collapse="doCollapse" :icon="icon"></MainHeader>
      </el-header>

      <el-main style="height: 100%;">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref } from 'vue'
import MainAside from "./MainAside.vue"
import MainHeader from "./MainHeader.vue"
import { Fold, Expand } from '@element-plus/icons-vue'

export default {
  name: "MainIndex",
  components: {
    MainAside,
    MainHeader
  },
  setup() {
    const isCollapse = ref(false)
    const aside_width = ref('200px')
    const icon = ref(Fold)

    const doCollapse = () => {
      console.log('折叠/展开侧边栏')

      isCollapse.value = !isCollapse.value
      if (!isCollapse.value) { // 展开
        aside_width.value = '200px'
        icon.value = Fold
      } else { // 收起
        aside_width.value = '64px'
        icon.value = Expand
      }
    }

    return {
      isCollapse,
      aside_width,
      icon,
      doCollapse
    }
  }
}
</script>

<style scoped>
.el-header {
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