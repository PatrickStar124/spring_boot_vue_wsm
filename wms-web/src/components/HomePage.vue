<template>
  <div class="home-container">
    <h1 class="welcome-title">{{ '欢迎你！' + user.name }}</h1>
    <el-descriptions class="descriptions" title="个人中心" :column="2" size="large" border>
      <el-descriptions-item>
        <template #label>
          <el-icon><User /></el-icon>
          账号
        </template>
        {{ user.no }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <el-icon><Iphone /></el-icon>
          电话
        </template>
        {{ user.phone }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <el-icon><Location /></el-icon>
          性别
        </template>
        <el-tag
            :type="user.sex === '1' ? 'primary' : 'danger'"
            disable-transitions
        >
          <el-icon>
            <component :is="user.sex == 1 ? Male : Female" />
          </el-icon>
          {{ user.sex == 1 ? "男" : "女" }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <el-icon><Tickets /></el-icon>
          角色
        </template>
        <el-tag
            type="success"
            disable-transitions
        >
          {{ user.roleId == 0 ? "超级管理员" : (user.roleId == 1 ? "管理员" : "用户") }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <DateUtils />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import DateUtils from "./DateUtils.vue"
import {
  User,
  Iphone,
  Location,
  Tickets,
  Male,
  Female
} from '@element-plus/icons-vue'

export default {
  name: "HomePage",
  components: {
    DateUtils,
    User,
    Iphone,
    Location,
    Tickets,
    Male,
    Female
  },
  setup() {
    const user = ref({})

    const init = () => {
      const userData = sessionStorage.getItem('CurUser')
      if (userData) {
        user.value = JSON.parse(userData)
      }
    }

    onMounted(() => {
      init()
    })

    return {
      user
    }
  }
}
</script>

<style scoped>
.home-container {
  text-align: center;
  background-color: #f1f1f3;
  height: 100%;
  padding: 0;
  margin: 0;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-top: 20px;
}

.welcome-title {
  font-size: 50px;
  margin-bottom: 30px;
  color: #333;
}

.descriptions {
  width: 90%;
  margin: 0 auto;
  text-align: center;
}

.el-descriptions-item {
  padding: 15px 0;
}

.el-tag {
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
</style>