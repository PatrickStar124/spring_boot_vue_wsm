<template>
  <nav class="navbar">
    <div class="nav-left">
      <router-link to="/home">首页</router-link>
      <router-link to="/cart">购物车</router-link>
    </div>
    <div class="nav-right">
      <span v-if="userStore.user">欢迎，{{ userStore.user.name }}</span>
      <router-link v-if="userStore.user" to="/user-center">用户中心</router-link>
      <button v-if="userStore.user" @click="handleLogout">退出</button>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.clearUser()
  localStorage.removeItem('token')
  router.push('/login')
}
</script>