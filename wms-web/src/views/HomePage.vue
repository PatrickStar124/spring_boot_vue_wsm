<template>
  <div class="home">
    <header class="header">
      <h1>📚 图书商城</h1>
      <div class="user-info" v-if="user">
        欢迎，{{ user.name }}
        <button @click="goToCart" class="cart-btn">🛒 购物车</button>
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
      <div class="user-info" v-else>
        <button @click="goToLogin" class="login-btn">登录</button>
        <button @click="goToRegister" class="register-btn">注册</button>
      </div>
    </header>

    <div class="book-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="books.length === 0" class="empty">暂无图书</div>
      <div v-else class="books-grid">
        <BookCard
            v-for="book in books"
            :key="book.id"
            :book="book"
            @cart-updated="handleCartUpdate"
        />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import BookCard from '@/components/BookCard.vue'

export default {
  name: 'Home',
  components: {
    BookCard
  },
  data() {
    return {
      user: null,
      books: [],
      loading: true
    }
  },
  created() {
    this.loadUser()
    this.fetchBooks()
  },
  methods: {
    loadUser() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        try {
          this.user = JSON.parse(userStr)
        } catch (e) {
          console.error('解析用户信息失败:', e)
        }
      }
    },
    async fetchBooks() {
      this.loading = true
      try {
        const response = await axios.get('http://localhost:8090/book/list')
        if (response.data.code === 200) {
          this.books = response.data.data
        }
      } catch (error) {
        console.error('获取图书失败:', error)
        alert('获取图书失败')
      } finally {
        this.loading = false
      }
    },
    goToCart() {
      if (!this.user) {
        alert('请先登录')
        this.$router.push('/login')
        return
      }
      this.$router.push('/cart')
    },
    goToLogin() {
      this.$router.push('/login')
    },
    goToRegister() {
      this.$router.push('/register')
    },
    logout() {
      localStorage.removeItem('user')
      this.user = null
      alert('已退出登录')
    },
    handleCartUpdate() {
      console.log('购物车已更新')
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.user-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.cart-btn, .login-btn, .register-btn, .logout-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.cart-btn {
  background: #1890ff;
  color: white;
}

.login-btn {
  background: #52c41a;
  color: white;
}

.register-btn {
  background: #faad14;
  color: white;
}

.logout-btn {
  background: #f5222d;
  color: white;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.loading, .empty {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #999;
}
</style>