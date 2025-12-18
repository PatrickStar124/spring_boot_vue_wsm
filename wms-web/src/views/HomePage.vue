<template>
  <div class="home-page">
    <header class="header">
      <h1>📚 图书商城</h1>
      <div class="header-actions">
        <button @click="goToBookList" class="booklist-btn">全部图书</button>
        <button @click="goToCart" class="cart-btn">🛒 购物车</button>
        <div class="user-info" v-if="user">
          <span>欢迎，{{ user.name }}</span>
          <button @click="goToUserCenter" class="user-btn">个人中心</button>
          <button @click="logout" class="logout-btn">退出</button>
        </div>
        <div class="user-info" v-else>
          <button @click="goToLogin" class="login-btn">登录</button>
          <button @click="goToRegister" class="register-btn">注册</button>
        </div>
      </div>
    </header>

    <div class="welcome-section">
      <h2>欢迎来到图书购物车系统</h2>
      <p>这里汇集了各类精品图书，点击"全部图书"浏览更多，或将喜欢的图书加入购物车</p>
    </div>

    <div class="featured-books">
      <h3>🔥 热门推荐</h3>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="books.length === 0" class="empty">暂无推荐图书</div>
      <div v-else class="books-grid">
        <BookCard
            v-for="book in books.slice(0, 4)"
            :key="book.id"
            :book="book"
            @cart-updated="handleCartUpdate"
        />
      </div>
    </div>

    <div class="quick-links">
      <div class="link-card" @click="goToBookList">
        <div class="link-icon">📖</div>
        <h4>浏览全部图书</h4>
        <p>查看所有可用图书</p>
      </div>
      <div class="link-card" @click="goToCart">
        <div class="link-icon">🛒</div>
        <h4>查看购物车</h4>
        <p>管理您的购物清单</p>
      </div>
      <div class="link-card" @click="user ? goToUserCenter() : goToLogin()">
        <div class="link-icon">👤</div>
        <h4>{{ user ? '个人中心' : '用户登录' }}</h4>
        <p>{{ user ? '查看个人信息' : '登录以使用完整功能' }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import BookCard from '@/components/BookCard.vue'

export default {
  name: 'HomePage',
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
      } finally {
        this.loading = false
      }
    },
    handleCartUpdate() {
      console.log('购物车已更新')
    },
    goToBookList() {
      this.$router.push('/booklist')
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
    goToUserCenter() {
      this.$router.push('/usercenter')
    },
    logout() {
      localStorage.removeItem('user')
      this.user = null
      alert('已退出登录')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.home-page {
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

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.user-info {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-left: 10px;
}

.booklist-btn, .cart-btn, .login-btn, .register-btn, .logout-btn, .user-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.booklist-btn {
  background: #1890ff;
  color: white;
}

.cart-btn {
  background: #faad14;
  color: white;
}

.login-btn {
  background: #52c41a;
  color: white;
}

.register-btn {
  background: #722ed1;
  color: white;
}

.logout-btn {
  background: #f5222d;
  color: white;
}

.user-btn {
  background: #13c2c2;
  color: white;
}

.welcome-section {
  text-align: center;
  padding: 40px;
  margin-bottom: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
}

.welcome-section h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.welcome-section p {
  font-size: 16px;
  opacity: 0.9;
}

.featured-books {
  margin-bottom: 40px;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.featured-books h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
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

.quick-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.link-card {
  padding: 25px;
  background: white;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.link-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
  border-color: #1890ff;
}

.link-icon {
  font-size: 40px;
  margin-bottom: 15px;
}

.link-card h4 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.link-card p {
  font-size: 14px;
  color: #666;
}
</style>