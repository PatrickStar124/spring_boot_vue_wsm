<template>
  <div class="home-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎使用图书购物车系统</h1>
      <p class="welcome-subtitle">{{ user?.name ? user.name + '，' : '' }}开始今天的购物吧！</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon size="24"><Goods /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.productCount }}</div>
                <div class="stat-label">商品总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon size="24"><ShoppingCart /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.todayOrders }}</div>
                <div class="stat-label">今日订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.userCount }}</div>
                <div class="stat-label">用户总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #f56c6c;">
                <el-icon size="24"><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.pendingOrders }}</div>
                <div class="stat-label">待处理订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3>快捷操作</h3>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="$router.push('/ProductManage')">
            <el-icon size="48" color="#409eff"><Goods /></el-icon>
            <div class="action-text">浏览商品</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="$router.push('/CartManage')">
            <el-icon size="48" color="#67c23a"><ShoppingCart /></el-icon>
            <div class="action-text">查看购物车</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="$router.push('/OrderManage')">
            <el-icon size="48" color="#e6a23c"><Document /></el-icon>
            <div class="action-text">我的订单</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="$router.push('/UserManage')">
            <el-icon size="48" color="#909399"><User /></el-icon>
            <div class="action-text">个人中心</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 热门商品推荐 -->
    <div class="hot-products" v-if="hotProducts.length > 0">
      <h3>热门商品推荐</h3>
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in hotProducts" :key="product.id">
          <el-card class="product-card" shadow="hover">
            <div class="product-image">
              <img :src="product.coverUrl || 'https://via.placeholder.com/200x250?text=Book'" :alt="product.productName" />
            </div>
            <div class="product-info">
              <div class="product-name">{{ product.productName }}</div>
              <div class="product-author">{{ product.author }}</div>
              <div class="product-price">
                <span class="current-price">¥{{ (product.price * (product.discount || 1)).toFixed(2) }}</span>
                <span v-if="product.discount < 1" class="original-price">¥{{ product.price.toFixed(2) }}</span>
              </div>
              <el-button type="primary" size="small" @click="addToCart(product)" :disabled="product.stock <= 0">
                加入购物车
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 时间显示 -->
    <div class="time-section">
      <DateUtils />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import DateUtils from "./DateUtils.vue"
import {
  Goods,
  ShoppingCart,
  User,
  Clock,
  Document
} from '@element-plus/icons-vue'

const user = ref({})
const stats = ref({
  productCount: 0,
  todayOrders: 0,
  userCount: 0,
  pendingOrders: 0
})

const hotProducts = ref([])

// 加载数据
const loadData = async () => {
  // 这里调用API获取统计数据
  stats.value = {
    productCount: 1250,
    todayOrders: 42,
    userCount: 289,
    pendingOrders: 12
  }

  // 加载热门商品
  hotProducts.value = [
    {
      id: 1,
      productName: '红楼梦',
      author: '曹雪芹',
      price: 59.70,
      discount: 0.8,
      stock: 15,
      coverUrl: 'https://img1.doubanio.com/view/subject/s/public/s1070959.jpg'
    },
    {
      id: 2,
      productName: 'Java编程思想',
      author: 'Bruce Eckel',
      price: 108.00,
      discount: 0.9,
      stock: 8,
      coverUrl: 'https://img2.doubanio.com/view/subject/s/public/s27243455.jpg'
    },
    {
      id: 3,
      productName: '三体',
      author: '刘慈欣',
      price: 48.00,
      discount: 0.85,
      stock: 20,
      coverUrl: 'https://img3.doubanio.com/view/subject/s/public/s2768378.jpg'
    },
    {
      id: 4,
      productName: '活着',
      author: '余华',
      price: 28.00,
      discount: 1,
      stock: 30,
      coverUrl: 'https://img1.doubanio.com/view/subject/s/public/s29053580.jpg'
    }
  ]

  const userData = sessionStorage.getItem('CurUser')
  if (userData) {
    user.value = JSON.parse(userData)
  } else {
    user.value = { name: '访客' }
  }
}

// 加入购物车
const addToCart = (product) => {
  let cart = JSON.parse(localStorage.getItem('shoppingCart') || '[]')

  const existingItem = cart.find(item => item.productId === product.id)

  if (existingItem) {
    existingItem.quantity += 1
    if (existingItem.quantity > product.stock) {
      alert('超过库存数量')
      return
    }
  } else {
    cart.push({
      productId: product.id,
      productName: product.productName,
      price: product.price,
      discount: product.discount || 1,
      quantity: 1,
      stock: product.stock,
      image: product.coverUrl,
      author: product.author
    })
  }

  localStorage.setItem('shoppingCart', JSON.stringify(cart))
  alert('已加入购物车')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-title {
  font-size: 36px;
  color: #303133;
  margin-bottom: 10px;
}

.welcome-subtitle {
  font-size: 18px;
  color: #909399;
}

.stat-card {
  border-radius: 10px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.quick-actions {
  margin: 40px 0;
}

.quick-actions h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #303133;
}

.action-card {
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.3s;
  border-radius: 10px;
}

.action-card:hover {
  transform: translateY(-5px);
}

.action-text {
  margin-top: 20px;
  font-size: 16px;
  font-weight: 500;
}

.hot-products {
  margin: 40px 0;
}

.hot-products h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #303133;
}

.product-card {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.product-image {
  height: 250px;
  overflow: hidden;
  border-radius: 5px;
  margin-bottom: 15px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-author {
  color: #999;
  font-size: 14px;
  margin-bottom: 15px;
}

.product-price {
  margin-bottom: 15px;
}

.current-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 14px;
}

.product-card .el-button {
  margin-top: auto;
  width: 100%;
}

.time-section {
  margin-top: 40px;
  text-align: center;
}
</style>