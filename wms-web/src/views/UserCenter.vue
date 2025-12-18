<template>
  <div class="user-center">
    <h1>用户中心</h1>
    <div class="user-info">
      <p><strong>账号:</strong> {{ userInfo.no }}</p>
      <p><strong>姓名:</strong> {{ userInfo.name }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userInfo = ref({})

onMounted(async () => {
  if (userStore.user) {
    await loadUserInfo()
  }
})

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo(userStore.user.id)
    if (res.code === 200) {
      userInfo.value = res.data
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}
</script>