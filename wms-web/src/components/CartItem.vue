<template>
  <div class="cart-item">
    <div class="item-left">
      <div class="item-image">
        <img :src="item.book.image || '/default-book.jpg'" :alt="item.book.name" />
      </div>
      <div class="item-info">
        <h4 class="item-title">{{ item.book.name }}</h4>
        <p class="item-author">作者：{{ item.book.author }}</p>
        <div class="item-price">
          ¥{{ item.book.price ? item.book.price.toFixed(2) : '0.00' }}
        </div>
      </div>
    </div>

    <div class="item-right">
      <button class="btn-remove" @click="removeFromCart" :disabled="isRemoving">
        {{ isRemoving ? '移除中...' : '移除' }}
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CartItem',
  props: {
    item: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isRemoving: false
    }
  },
  methods: {
    async removeFromCart() {
      const userStr = localStorage.getItem('user')
      if (!userStr) {
        alert('请先登录')
        return
      }

      let user
      try {
        user = JSON.parse(userStr)
      } catch (e) {
        alert('用户信息错误')
        return
      }

      this.isRemoving = true
      try {
        const response = await axios.delete('http://localhost:8090/cart/remove', {
          params: {
            userId: user.id,
            bookId: this.item.bookId
          }
        })

        if (response.data.code === 200) {
          this.$emit('remove', this.item)
        } else {
          alert(response.data.msg || '移除失败')
        }
      } catch (error) {
        console.error('移除失败:', error)
        alert('网络错误，请稍后重试')
      } finally {
        this.isRemoving = false
      }
    }
  }
}
</script>

<style scoped>
.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  background: white;
  transition: all 0.3s ease;
}

.cart-item:hover {
  background: #fafafa;
}

.item-left {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
}

.item-image {
  width: 80px;
  height: 100px;
  margin-right: 16px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 4px;
  background: #f5f5f5;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 6px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-author {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.item-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.item-right {
  margin-left: 16px;
}

.btn-remove {
  padding: 6px 16px;
  background: transparent;
  color: #ff4d4f;
  border: 1px solid #ff4d4f;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-remove:hover {
  background: #ff4d4f;
  color: white;
}

.btn-remove:disabled {
  background: #f5f5f5;
  color: #d9d9d9;
  border-color: #d9d9d9;
  cursor: not-allowed;
}
</style>