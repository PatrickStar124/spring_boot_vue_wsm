<template>
  <div class="book-list">
    <div class="book-grid">
      <BookCard
          v-for="book in books"
          :key="book.id"
          :book="book"
          @cart-updated="refreshCartCount"
      />
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import BookCard from './BookCard.vue';

export default {
  components: {
    BookCard
  },
  setup() {
    const books = ref([]);

    const fetchBooks = async () => {
      try {
        const response = await axios.get('http://localhost:8090/book/list');
        if (response.data.code === 200) {
          books.value = response.data.data;
        }
      } catch (error) {
        console.error('获取图书列表失败:', error);
      }
    };

    const refreshCartCount = () => {
      // 这里可以触发更新购物车数量的逻辑
      console.log('购物车已更新');
    };

    onMounted(() => {
      fetchBooks();
    });

    return {
      books,
      refreshCartCount
    };
  }
}
</script>

<style scoped>
.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
}
</style>