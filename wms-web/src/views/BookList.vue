<template>
  <div class="book-list">
    <header class="header">
      <h1>📚 全部图书</h1>
      <div class="header-actions">
        <button @click="goHome" class="home-btn">返回首页</button>
        <button @click="goToCart" class="cart-btn">🛒 购物车</button>
        <div class="user-info" v-if="user">
          <span>欢迎，{{ user.name }}</span>
          <button @click="logout" class="logout-btn">退出</button>
        </div>
        <div class="user-info" v-else>
          <button @click="goToLogin" class="login-btn">登录</button>
          <button @click="goToRegister" class="register-btn">注册</button>
        </div>
      </div>
    </header>

    <div class="book-filter">
      <input
          v-model="searchKeyword"
          placeholder="搜索图书名称或作者..."
          class="search-input"
          @input="filterBooks"
      />
      <button @click="refreshBooks" class="refresh-btn">刷新</button>
    </div>

    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filteredBooks.length === 0" class="empty">
        {{ searchKeyword ? '没有找到相关图书' : '暂无图书' }}
      </div>
      <div v-else class="books-grid">
        <BookCard
            v-for="book in filteredBooks"
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
  name: 'BookList',
  components: {
    BookCard
  },
  data() {
    return {
      user: null,
      books: [],
      filteredBooks: [],
      loading: true,
      searchKeyword: ''
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
          this.filteredBooks = [...this.books]
        } else {
          alert('获取图书失败: ' + response.data.msg)
        }
      } catch (error) {
        console.error('获取图书失败:', error)
        alert('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    filterBooks() {
      if (!this.searchKeyword.trim()) {
        this.filteredBooks = [...this.books]
        return
      }

      const keyword = this.searchKeyword.toLowerCase()
      this.filteredBooks = this.books.filter(book => {
        return (
            (book.name && book.name.toLowerCase().includes(keyword)) ||
            (book.author && book.author.toLowerCase().includes(keyword))
        )
      })
    },
    refreshBooks() {
      this.searchKeyword = ''
      this.fetchBooks()
    },
    handleCartUpdate() {
      console.log('购物车已更新')
    },
    goHome() {
      this.$router.push('/')
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
    }
  }
}
</script>

<style scoped>
.book-list {
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

.home-btn, .cart-btn, .login-btn, .register-btn, .logout-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.home-btn {
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

.book-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.refresh-btn {
  padding: 8px 16px;
  background: #13c2c2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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
  background: white;
  border-radius: 8px;
}
</style>