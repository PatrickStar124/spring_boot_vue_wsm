<template>
  <div class="header-container">
    <div class="left-section">
      <el-icon class="collapse-icon" @click="collapse">
        <component :is="iconComponent" />
      </el-icon>
    </div>

    <div class="center-section">
      <span class="title">图书管理系统</span>
    </div>

    <div class="right-section">
      <el-dropdown>
        <span class="user-name">{{ user?.name || '未登录' }}</span>
        <el-icon class="dropdown-icon">
          <ArrowDown />
        </el-icon>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="toUser">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item>
            <el-dropdown-item @click="logout" divided>
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  User,
  SwitchButton,
  Fold,
  Expand
} from '@element-plus/icons-vue'

export default {
  name: "MainHeader",
  components: {
    ArrowDown,
    User,
    SwitchButton
  },
  props: {
    icon: {
      type: String,
      default: 'Fold'
    }
  },
  emits: ['doCollapse'],
  setup(props, { emit }) {
    const router = useRouter()
    const user = ref({})

    // 动态计算图标组件
    const iconComponent = computed(() => {
      const iconMap = {
        'Fold': Fold,
        'Expand': Expand
      }
      return iconMap[props.icon] || Fold
    })

    // 初始化用户数据
    const initUser = () => {
      const userData = sessionStorage.getItem('CurUser')
      if (userData) {
        user.value = JSON.parse(userData)
      }
    }

    const toUser = () => {
      console.log('跳转到个人中心')
      router.push("/Home")
    }

    const logout = () => {
      console.log('退出登录')

      ElMessageBox.confirm('您确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true,
      })
          .then(() => {
            ElMessage({
              type: 'success',
              message: '退出登录成功'
            })

            sessionStorage.clear()
            router.push("/")
          })
          .catch(() => {
            ElMessage({
              type: 'info',
              message: '已取消退出登录'
            })
          })
    }

    const collapse = () => {
      emit('doCollapse')
    }

    // 组件挂载时初始化用户数据
    onMounted(() => {
      initUser()
    })

    return {
      user,
      iconComponent,
      toUser,
      logout,
      collapse
    }
  }
}
</script>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
  border-bottom: 1px solid #e6e6e6;
}

.left-section {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: #409eff;
}

.center-section {
  flex: 1;
  text-align: center;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.right-section {
  display: flex;
  align-items: center;
}

.user-name {
  margin-right: 5px;
}

.dropdown-icon {
  color: #909399;
}
</style>