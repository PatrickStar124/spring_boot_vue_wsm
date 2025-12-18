<template>
  <!-- 直接显示购物车内容，删除“未登录显示登录框”的条件渲染 -->
  <div class="cart-page">
    <h2>我的购物车</h2>

    <!-- 购物车列表（不管登录状态，直接渲染） -->
    <div class="cart-list" v-if="cartList.length > 0">
      <div class="cart-item" v-for="item in cartList" :key="item.id">
        <div class="item-name">{{ item.name }}</div>
        <div class="item-price">¥{{ item.price }}</div>
        <div class="item-count">x{{ item.count }}</div>
      </div>
    </div>

    <!-- 空购物车提示（替代登录提 示） -->
    <div class="empty-cart" v-else>
      购物车为空，快去添加商品吧～
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
// 注释掉 token 相关判断（核心：跳过登录校验）
// import { useRouter } from 'vue-router'
// const router = useRouter()

// 1. 模拟购物车数据（无需请求后端接口，直接显示）
const cartList = ref([
  { id: 1, name: '测试商品1', price: 99, count: 1 },
  { id: 2, name: '测试商品2', price: 199, count: 2 }
])

onMounted(() => {
  // 2. 注释掉“获取购物车数据”的接口请求（原请求需要登录 token，会触发登录校验）
  // getCartData()

  // 【可选】如果后端接口强制校验 token，临时塞一个假 token 到本地存储，绕过校验
  // localStorage.setItem('token', 'fake-token-123456')
})

// 3. 注释掉需要登录的接口请求函数（避免请求失败弹登录提示）
// const getCartData = async () => {
//   try {
//     const res = await axios.get('http://localhost:8090/cart/list')
//     cartList.value = res.data.data || []
//   } catch (err) {
//     // 原逻辑：请求失败→弹登录提示/跳登录页，直接注释
//     // alert('请先登录！')
//     // router.push('/login')
//     console.log('跳过登录，使用模拟购物车数据')
//   }
// }
</script>

<style scoped>
.cart-page {
  width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.cart-list {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-top: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.item-name {
  width: 50%;
  font-size: 16px;
}

.item-price {
  width: 20%;
  color: #f40;
}

.item-count {
  width: 20%;
  text-align: center;
}

.empty-cart {
  margin-top: 50px;
  text-align: center;
  color: #999;
  font-size: 18px;
}
</style>