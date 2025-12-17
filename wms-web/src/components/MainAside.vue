<template>
  <el-menu
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      style="height: 100%;"
      :default-active="activeMenu"
      :collapse="isCollapse"
      :collapse-transition="false"
      router
  >
    <!-- 系统Logo/标题 -->
    <div class="sidebar-header" v-if="!isCollapse">
      <h2 class="system-title">图书购物车系统</h2>
    </div>
    <div class="sidebar-header-collapsed" v-else>
      <el-icon size="24" color="#ffd04b"><Collection /></el-icon>
    </div>

    <!-- 导航菜单 -->
    <el-menu-item index="/home">
      <el-icon><HomeFilled /></el-icon>
      <template #title>首页</template>
    </el-menu-item>

    <el-sub-menu index="2">
      <template #title>
        <el-icon><Goods /></el-icon>
        <span>图书管理</span>
      </template>
      <el-menu-item index="/ProductManage">图书列表</el-menu-item>
      <el-menu-item index="/categories">分类管理</el-menu-item>
      <el-menu-item index="/book/add">添加图书</el-menu-item>
    </el-sub-menu>

    <el-menu-item index="/CartManage">
      <el-icon><ShoppingCart /></el-icon>
      <template #title>购物车</template>
    </el-menu-item>

    <el-sub-menu index="4">
      <template #title>
        <el-icon><Document /></el-icon>
        <span>订单管理</span>
      </template>
      <el-menu-item index="/OrderManage">我的订单</el-menu-item>
      <el-menu-item index="/order/pending">待处理订单</el-menu-item>
      <el-menu-item index="/order/history">历史订单</el-menu-item>
    </el-sub-menu>

    <!-- 用户管理 -->
    <el-sub-menu index="5">
      <template #title>
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </template>
      <el-menu-item index="/UserManage">用户列表</el-menu-item>
      <el-menu-item index="/admin/roles">角色管理</el-menu-item>
      <el-menu-item index="/admin/permissions">权限管理</el-menu-item>
    </el-sub-menu>

    <el-menu-item index="/StatisticPage">
      <el-icon><DataAnalysis /></el-icon>
      <template #title>数据统计</template>
    </el-menu-item>

    <!-- 个人信息 -->
    <el-menu-item index="/profile">
      <el-icon><UserFilled /></el-icon>
      <template #title>个人中心</template>
    </el-menu-item>
  </el-menu>
</template>

<script>
// 导入所有用到的Element Plus图标
import {
  Collection,
  HomeFilled,
  Goods,
  ShoppingCart,
  Document,
  User,
  DataAnalysis,
  UserFilled
} from '@element-plus/icons-vue'

export default {
  name: 'MainAside',
  // 注册所有图标组件（解决图标无法显示的问题）
  components: {
    Collection,
    HomeFilled,
    Goods,
    ShoppingCart,
    Document,
    User,
    DataAnalysis,
    UserFilled
  },
  props: {
    isCollapse: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    activeMenu() {
      const path = this.$route.path
      // 根据实际路由路径设置激活菜单，兼容大小写和嵌套路由
      if (path === '/ProductManage' || path.startsWith('/ProductManage')) return '/ProductManage'
      if (path === '/CartManage' || path.startsWith('/CartManage')) return '/CartManage'
      if (path === '/OrderManage' || path.startsWith('/OrderManage')) return '/OrderManage'
      if (path === '/UserManage' || path.startsWith('/UserManage')) return '/UserManage'
      if (path === '/StatisticPage' || path.startsWith('/StatisticPage')) return '/StatisticPage'
      if (path === '/home' || path === '/' || path === '/Home') return '/home'
      return path
    },
    userRole() {
      try {
        const userData = sessionStorage.getItem('CurUser')
        if (userData) {
          const user = JSON.parse(userData)
          return user.role || 'user'
        }
      } catch (error) {
        console.error('解析用户角色失败:', error)
      }
      return 'user'
    }
  }
}
</script>

<style scoped>
/* 侧边栏基础样式 */
.el-menu {
  border-right: none;
  transition: width 0.3s ease;
}

/* 展开状态的头部 */
.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #434a50;
}

/* 折叠状态的头部 */
.sidebar-header-collapsed {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #434a50;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 系统标题样式 */
.system-title {
  color: #ffd04b;
  font-size: 18px;
  margin: 0;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 菜单项样式 */
.el-menu-item,
.el-sub-menu__title {
  height: 56px;
  line-height: 56px;
  transition: all 0.3s;
}

/* 菜单项hover效果 */
.el-menu-item:hover {
  background-color: #434a50 !important;
}

/* 激活菜单项样式 */
.el-menu-item.is-active {
  background-color: #409eff !important;
}

/* 子菜单样式 */
.el-sub-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  min-width: 0;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .sidebar-header .system-title {
    font-size: 16px;
  }
}
</style>