  <template>
    <el-container style="height: 100%;">
      <el-aside :width="asideWidth" style="height: 100vh; background-color: rgb(238, 241, 246);">
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

  <script setup>
  import { ref, computed } from 'vue'
  import MainAside from './MainAside.vue'
  import MainHeader from './MainHeader.vue'

  const isCollapse = ref(false)

  // 计算侧边栏宽度
  const asideWidth = computed(() => {
    return isCollapse.value ? '64px' : '200px'
  })

  // 计算图标类型
  const icon = computed(() => {
    return isCollapse.value ? 'Expand' : 'Fold'
  })

  // 切换折叠状态
  const doCollapse = () => {
    isCollapse.value = !isCollapse.value
  }
  </script>