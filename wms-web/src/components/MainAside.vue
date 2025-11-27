<template>
  <el-menu
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      style="height: 100%;"
      :default-active="currentRoute"
      :collapse="isCollapse"
      :collapse-transition="false"
      router
  >
    <el-menu-item index="/Home">
      <el-icon><Home /></el-icon>
      <span>首页</span>
    </el-menu-item>

    <el-menu-item
        :index="'/' + item.menuclick"
        v-for="(item, i) in menu"
        :key="i"
    >
      <el-icon>
        <component :is="getIconComponent(item.menuicon)" />
      </el-icon>
      <span>{{ item.menuname }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
import {
  Home,
  User,
  Setting,
  Goods,
  List,
  Document,
  Monitor
} from '@element-plus/icons-vue'

export default {
  name: "MainAside",
  components: {
    Home
  },
  props: {
    isCollapse: Boolean
  },
  setup() {
    const store = useStore()
    const route = useRoute()

    // 获取当前路由用于默认激活状态
    const currentRoute = computed(() => route.path)

    // 安全地获取菜单数据，添加错误处理
    const menu = computed(() => {
      // 如果 store 或 store.state 不存在，返回空数组
      if (!store || !store.state) {
        console.warn('Store is not available, returning empty menu')
        return []
      }
      return store.state.menu || []
    })

    // 图标映射表
    const iconMap = {
      'el-icon-s-custom': User,
      'el-icon-user-solid': User,
      'el-icon-s-tools': Setting,
      'el-icon-s-goods': Goods,
      'el-icon-s-order': List,
      'el-icon-document': Document,
      'el-icon-monitor': Monitor
    }

    // 根据图标名称获取对应的图标组件
    const getIconComponent = (iconName) => {
      return iconMap[iconName] || Monitor // 默认图标
    }

    return {
      menu,
      currentRoute,
      getIconComponent
    }
  }
}
</script>

<style scoped>
.el-menu {
  border-right: none;
}

.el-menu-item {
  transition: all 0.3s;
}

.el-menu-item:hover {
  background-color: #434a50 !important;
}

.el-menu-item.is-active {
  background-color: #409eff !important;
}
</style>