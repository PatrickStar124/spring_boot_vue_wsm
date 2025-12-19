<template>
  <div class="book-card">
    <div class="book-cover">
      <img
          :src="book.image || '/default-book.jpg'"
          :alt="book.name"
          loading="lazy"
          @error="$event.target.src = '/default-book.jpg'"
      />
    </div>
    <div class="book-info">
      <h3 class="book-title">{{ book.name }}</h3>
      <p class="book-author">作者：{{ book.author || '未知' }}</p>
      <p class="book-description">{{ book.description || '暂无描述' }}</p>
      <div class="book-footer">
        <span class="book-price">¥{{ book.price ? book.price.toFixed(2) : '0.00' }}</span>
        <span class="book-stock">库存：{{ book.stock !== undefined ? book.stock : '充足' }}</span>
      </div>
      <div class="book-actions">
        <button
            class="btn-add-to-cart"
            @click="addToCart"
            :disabled="!currentUser || isAdding"
        >
          {{ isAdding ? '添加中...' : (currentUser ? '加入购物车' : '请先登录') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'BookCard',
  props: {
    book: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isAdding: false,
      currentUser: null
    }
  },
  created() {
    this.loadUser()
  },
  methods: {
    loadUser() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        try {
          this.currentUser = JSON.parse(userStr)
        } catch (e) {
          console.error('解析用户信息失败:', e)
        }
      }
    },
    async addToCart() {
      if (!this.currentUser) {
        alert('请先登录')
        this.$router.push('/login')
        return
      }

      this.isAdding = true
      try {
        const response = await axios.post('http://localhost:8090/cart/add', null, {
          params: {
            userId: this.currentUser.id,
            bookId: this.book.id
          }
        })

        if (response.data.code === 200) {
          alert('已加入购物车')
          this.$emit('cart-updated')
        } else {
          alert(response.data.msg || '加入购物车失败')
        }
      } catch (error) {
        console.error('加入购物车失败:', error)
        alert('网络错误，请稍后重试')
      } finally {
        this.isAdding = false
      }
    }
  }
}
</script>

<style scoped>
.book-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  background: white;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.book-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.book-cover {
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.book-card:hover .book-cover img {
  transform: scale(1.05);
}

.book-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-author {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.book-description {
  font-size: 13px;
  color: #888;
  line-height: 1.5;
  margin-bottom: 12px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.book-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.book-stock {
  font-size: 12px;
  color: #999;
  padding: 2px 8px;
  background: #f5f5f5;
  border-radius: 10px;
}

.book-actions {
  margin-top: auto;
}

.btn-add-to-cart {
  width: 100%;
  padding: 8px 0;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-add-to-cart:hover {
  background: #40a9ff;
}

.btn-add-to-cart:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}
</style>