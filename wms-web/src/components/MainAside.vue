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

    <el-menu-item index="/ProductManage">
      <el-icon><Goods /></el-icon>
      <span>商品管理</span>
    </el-menu-item>

    <el-menu-item index="/CartManage">
      <el-icon><ShoppingCart /></el-icon>
      <span>购物车</span>
    </el-menu-item>

    <el-menu-item index="/OrderManage">
      <el-icon><Document /></el-icon>
      <span>订单管理</span>
    </el-menu-item>

    <el-menu-item index="/UserManage">
      <el-icon><User /></el-icon>
      <span>用户管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  Home,
  Goods,
  ShoppingCart,
  Document,
  User
} from '@element-plus/icons-vue'

const route = useRoute()
const props = defineProps(['isCollapse'])

// 获取当前路由用于默认激活状态
const currentRoute = computed(() => route.path)

return {
  currentRoute
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