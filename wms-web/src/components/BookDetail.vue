<template>
  <div class="book-detail-page" v-if="book">
    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <router-link to="/">首页</router-link> /
      <router-link to="/books">图书</router-link> /
      <span>{{ book.title }}</span>
    </div>

    <div class="book-detail-container">
      <!-- 左侧：图书封面和基本信息 -->
      <div class="book-left">
        <div class="book-cover-wrapper">
          <img
              :src="book.cover || defaultCover"
              :alt="book.title"
              class="book-cover"
              @error="handleImageError"
          />
          <div class="book-tags" v-if="book.tags && book.tags.length">
            <span
                v-for="tag in book.tags.slice(0, 3)"
                :key="tag"
                class="tag"
            >
              {{ tag }}
            </span>
          </div>
        </div>

        <div class="action-buttons">
          <button
              class="btn-favorite"
              :class="{ active: isFavorite }"
              @click="toggleFavorite"
          >
            <i class="icon-heart"></i>
            {{ isFavorite ? '已收藏' : '收藏' }}
          </button>
          <button class="btn-share" @click="shareBook">
            <i class="icon-share"></i>
            分享
          </button>
          <button class="btn-report" @click="reportIssue">
            <i class="icon-flag"></i>
            举报问题
          </button>
        </div>
      </div>

      <!-- 中间：图书详细信息和购买 -->
      <div class="book-center">
        <div class="book-header">
          <h1 class="book-title">{{ book.title }}</h1>
          <p class="book-subtitle">{{ book.subtitle }}</p>

          <div class="book-meta">
            <div class="meta-item">
              <span class="meta-label">作者：</span>
              <span class="meta-value">{{ book.author }}</span>
            </div>
            <div class="meta-item" v-if="book.publisher">
              <span class="meta-label">出版社：</span>
              <span class="meta-value">{{ book.publisher }}</span>
            </div>
            <div class="meta-item" v-if="book.publishDate">
              <span class="meta-label">出版日期：</span>
              <span class="meta-value">{{ formatDate(book.publishDate) }}</span>
            </div>
            <div class="meta-item" v-if="book.isbn">
              <span class="meta-label">ISBN：</span>
              <span class="meta-value">{{ book.isbn }}</span>
            </div>
          </div>

          <div class="rating-section">
            <div class="rating-stars">
              <span
                  v-for="star in 5"
                  :key="star"
                  class="star"
                  :class="{ filled: star <= Math.floor(book.rating) }"
              >
                ★
              </span>
              <span class="rating-score">{{ book.rating.toFixed(1) }}</span>
            </div>
            <span class="rating-count">({{ book.reviewCount }}条评价)</span>
          </div>
        </div>

        <div class="price-section">
          <div class="price-current">
            <span class="price-label">售价：</span>
            <span class="price-amount">¥{{ book.price.toFixed(2) }}</span>
            <span class="price-original" v-if="book.originalPrice > book.price">
              ¥{{ book.originalPrice.toFixed(2) }}
            </span>
          </div>
          <div class="discount" v-if="book.discount > 0">
            限时{{ book.discount }}折
          </div>
        </div>

        <div class="stock-section">
          <span class="stock-label">库存：</span>
          <span
              class="stock-status"
              :class="{
              'in-stock': book.stock > 10,
              'low-stock': book.stock > 0 && book.stock <= 10,
              'out-of-stock': book.stock === 0
            }"
          >
            {{ getStockStatus() }}
          </span>
          <span class="sales-count" v-if="book.salesCount">
            月售{{ book.salesCount }}本
          </span>
        </div>

        <div class="purchase-section">
          <div class="quantity-control">
            <label for="quantity">数量：</label>
            <div class="quantity-input">
              <button
                  class="btn-quantity minus"
                  @click="decreaseQuantity"
                  :disabled="quantity <= 1"
              >
                -
              </button>
              <input
                  id="quantity"
                  type="number"
                  v-model.number="quantity"
                  min="1"
                  :max="book.stock"
                  @input="validateQuantity"
              />
              <button
                  class="btn-quantity plus"
                  @click="increaseQuantity"
                  :disabled="quantity >= book.stock"
              >
                +
              </button>
            </div>
          </div>

          <div class="action-buttons-main">
            <button
                class="btn-add-cart"
                :disabled="book.stock === 0 || isAddingToCart"
                @click="addToCart"
            >
              <i class="icon-cart"></i>
              {{ isAddingToCart ? '添加中...' : '加入购物车' }}
            </button>
            <button
                class="btn-buy-now"
                :disabled="book.stock === 0"
                @click="buyNow"
            >
              立即购买
            </button>
          </div>
        </div>

        <div class="service-info">
          <div class="service-item">
            <i class="icon-truck"></i>
            <span>全场满¥99包邮</span>
          </div>
          <div class="service-item">
            <i class="icon-shield"></i>
            <span>正品保障</span>
          </div>
          <div class="service-item">
            <i class="icon-refund"></i>
            <span>7天无理由退货</span>
          </div>
        </div>
      </div>

      <!-- 右侧：推荐和店铺信息 -->
      <div class="book-right">
        <div class="seller-info" v-if="book.seller">
          <h3>店铺信息</h3>
          <div class="seller-details">
            <img :src="book.seller.avatar" class="seller-avatar" />
            <div class="seller-meta">
              <div class="seller-name">{{ book.seller.name }}</div>
              <div class="seller-rating">
                评分：{{ book.seller.rating }}
              </div>
            </div>
          </div>
          <button class="btn-contact">联系卖家</button>
        </div>

        <div class="recommendations">
          <h3>猜你喜欢</h3>
          <div
              v-for="item in recommendations"
              :key="item.id"
              class="recommendation-item"
              @click="goToBook(item.id)"
          >
            <img :src="item.cover" class="rec-cover" />
            <div class="rec-info">
              <div class="rec-title">{{ item.title }}</div>
              <div class="rec-price">¥{{ item.price.toFixed(2) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 标签页内容 -->
    <div class="book-tabs">
      <div class="tabs-header">
        <button
            v-for="tab in tabs"
            :key="tab.id"
            class="tab-btn"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
        >
          {{ tab.name }}
          <span v-if="tab.count" class="tab-count">({{ tab.count }})</span>
        </button>
      </div>

      <div class="tabs-content">
        <!-- 详情描述 -->
        <div v-show="activeTab === 'description'" class="tab-panel">
          <div class="description-content">
            <h3>内容简介</h3>
            <div class="description-text" v-html="book.description"></div>

            <div v-if="book.features && book.features.length" class="features">
              <h4>本书特色</h4>
              <ul>
                <li v-for="feature in book.features" :key="feature">
                  {{ feature }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 目录 -->
        <div v-show="activeTab === 'catalog'" class="tab-panel">
          <div class="catalog-content">
            <h3>图书目录</h3>
            <div class="catalog-chapters">
              <div
                  v-for="(chapter, index) in book.catalog"
                  :key="index"
                  class="chapter-item"
                  @click="toggleChapter(index)"
              >
                <div class="chapter-header">
                  <span class="chapter-title">{{ chapter.title }}</span>
                  <span class="chapter-page">第{{ chapter.page }}页</span>
                  <i
                      class="chapter-icon"
                      :class="{ expanded: expandedChapters.includes(index) }"
                  >
                    ▼
                  </i>
                </div>
                <div
                    v-if="expandedChapters.includes(index)"
                    class="chapter-sections"
                >
                  <div
                      v-for="(section, sIndex) in chapter.sections"
                      :key="sIndex"
                      class="section-item"
                  >
                    {{ section }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 评价 -->
        <div v-show="activeTab === 'reviews'" class="tab-panel">
          <div class="reviews-content">
            <div class="reviews-header">
              <h3>商品评价</h3>
              <div class="review-summary">
                <div class="overall-rating">
                  <div class="rating-number">{{ book.rating.toFixed(1) }}</div>
                  <div class="rating-stars-summary">
                    <span
                        v-for="star in 5"
                        :key="star"
                        class="star"
                        :class="{ filled: star <= Math.floor(book.rating) }"
                    >
                      ★
                    </span>
                  </div>
                  <div class="rating-count">{{ book.reviewCount }}人评价</div>
                </div>

                <div class="rating-distribution">
                  <div
                      v-for="rating in [5,4,3,2,1]"
                      :key="rating"
                      class="distribution-item"
                  >
                    <span class="dist-label">{{ rating }}星</span>
                    <div class="dist-bar">
                      <div
                          class="dist-fill"
                          :style="{ width: getRatingPercentage(rating) + '%' }"
                      ></div>
                    </div>
                    <span class="dist-percentage">
                      {{ getRatingPercentage(rating) }}%
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div class="reviews-filter">
              <button
                  v-for="filter in reviewFilters"
                  :key="filter.value"
                  class="filter-btn"
                  :class="{ active: activeFilter === filter.value }"
                  @click="activeFilter = filter.value"
              >
                {{ filter.label }}
              </button>
            </div>

            <div class="reviews-list">
              <div
                  v-for="review in filteredReviews"
                  :key="review.id"
                  class="review-item"
              >
                <div class="reviewer-info">
                  <img :src="review.avatar" class="reviewer-avatar" />
                  <div class="reviewer-meta">
                    <div class="reviewer-name">{{ review.username }}</div>
                    <div class="review-date">{{ formatDate(review.date) }}</div>
                  </div>
                </div>
                <div class="review-rating">
                  <span
                      v-for="star in 5"
                      :key="star"
                      class="star"
                      :class="{ filled: star <= review.rating }"
                  >
                    ★
                  </span>
                </div>
                <div class="review-content">{{ review.content }}</div>
                <div class="review-helpful">
                  <button
                      class="btn-helpful"
                      :class="{ active: review.helpful }"
                      @click="toggleHelpful(review.id)"
                  >
                    有帮助 ({{ review.helpfulCount }})
                  </button>
                </div>
              </div>
            </div>

            <div class="reviews-pagination">
              <button
                  class="btn-prev"
                  :disabled="currentPage === 1"
                  @click="changePage(currentPage - 1)"
              >
                上一页
              </button>
              <span class="page-info">
                第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
              </span>
              <button
                  class="btn-next"
                  :disabled="currentPage === totalPages"
                  @click="changePage(currentPage + 1)"
              >
                下一页
              </button>
            </div>
          </div>
        </div>

        <!-- 常见问题 -->
        <div v-show="activeTab === 'faq'" class="tab-panel">
          <div class="faq-content">
            <h3>常见问题</h3>
            <div class="faq-list">
              <div
                  v-for="(faq, index) in faqs"
                  :key="index"
                  class="faq-item"
                  @click="toggleFaq(index)"
              >
                <div class="faq-question">
                  <span>Q{{ index + 1 }}: {{ faq.question }}</span>
                  <i class="faq-icon" :class="{ expanded: expandedFaqs.includes(index) }">
                    ▼
                  </i>
                </div>
                <div
                    v-if="expandedFaqs.includes(index)"
                    class="faq-answer"
                >
                  {{ faq.answer }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 相关推荐 -->
    <div class="related-books" v-if="relatedBooks.length">
      <h3>相关推荐</h3>
      <div class="related-list">
        <div
            v-for="item in relatedBooks"
            :key="item.id"
            class="related-item"
            @click="goToBook(item.id)"
        >
          <img :src="item.cover" class="related-cover" />
          <div class="related-info">
            <div class="related-title">{{ item.title }}</div>
            <div class="related-author">{{ item.author }}</div>
            <div class="related-price">¥{{ item.price.toFixed(2) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预览模态框 -->
    <div v-if="showPreview" class="preview-modal">
      <div class="preview-content">
        <div class="preview-header">
          <h3>试读章节：{{ previewChapter.title }}</h3>
          <button class="btn-close" @click="showPreview = false">×</button>
        </div>
        <div class="preview-body">
          <div class="preview-text">
            {{ previewChapter.content }}
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 加载状态 -->
  <div v-else-if="isLoading" class="loading-container">
    <div class="spinner"></div>
    <p>加载中...</p>
  </div>

  <!-- 错误状态 -->
  <div v-else-if="error" class="error-container">
    <div class="error-icon">!</div>
    <h3>加载失败</h3>
    <p>{{ error }}</p>
    <button @click="retryLoading" class="btn-retry">重试</button>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/dateUtils'

export default {
  name: 'BookDetail',

  setup() {
    const route = useRoute()
    const router = useRouter()
    const cartStore = useCartStore()
    const userStore = useUserStore()

    // 数据状态
    const book = ref(null)
    const recommendations = ref([])
    const relatedBooks = ref([])
    const reviews = ref([])
    const faqs = ref([])
    const isLoading = ref(true)
    const error = ref('')

    // 交互状态
    const quantity = ref(1)
    const activeTab = ref('description')
    const activeFilter = ref('all')
    const expandedChapters = ref([])
    const expandedFaqs = ref([])
    const currentPage = ref(1)
    const pageSize = ref(5)
    const isAddingToCart = ref(false)
    const isFavorite = ref(false)
    const showPreview = ref(false)
    const previewChapter = ref(null)

    // 标签页配置
    const tabs = computed(() => [
      { id: 'description', name: '商品详情', count: null },
      { id: 'catalog', name: '目录', count: book.value?.catalog?.length || 0 },
      { id: 'reviews', name: '评价', count: book.value?.reviewCount || 0 },
      { id: 'faq', name: '常见问题', count: faqs.value.length }
    ])

    // 评价过滤器
    const reviewFilters = [
      { value: 'all', label: '全部' },
      { value: 'positive', label: '好评' },
      { value: 'neutral', label: '中评' },
      { value: 'negative', label: '差评' }
    ]

    // 默认封面
    const defaultCover = '/images/default-book-cover.jpg'

    // 计算属性
    const totalPages = computed(() => {
      return Math.ceil(reviews.value.length / pageSize.value)
    })

    const filteredReviews = computed(() => {
      let filtered = reviews.value

      switch (activeFilter.value) {
        case 'positive':
          filtered = filtered.filter(r => r.rating >= 4)
          break
        case 'neutral':
          filtered = filtered.filter(r => r.rating === 3)
          break
        case 'negative':
          filtered = filtered.filter(r => r.rating <= 2)
          break
      }

      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return filtered.slice(start, end)
    })

    // 方法
    const fetchBookDetail = async () => {
      isLoading.value = true
      error.value = ''

      try {
        // 这里应该是API调用
        const bookId = route.params.id

        // 模拟数据
        book.value = {
          id: bookId,
          title: 'Vue.js设计与实现',
          subtitle: '深入解析Vue.js框架设计思想',
          author: '霍春阳',
          publisher: '人民邮电出版社',
          publishDate: '2022-01-01',
          isbn: '9787115585624',
          cover: '/images/vue-book.jpg',
          price: 89.90,
          originalPrice: 99.90,
          discount: 9,
          stock: 15,
          salesCount: 234,
          rating: 4.7,
          reviewCount: 128,
          tags: ['前端开发', 'Vue.js', '框架原理'],
          description: `<p>本书以Vue.js 3.0为核心，从设计角度深入解析Vue.js的实现原理。书中详细介绍了响应式系统、编译器和运行时等核心模块的实现，并讨论了框架设计中的权衡与决策。</p>
            <p>通过阅读本书，你可以：</p>
            <ul>
              <li>深入理解Vue.js 3.0的响应式原理</li>
              <li>掌握虚拟DOM和diff算法的实现</li>
              <li>了解编译器的构建过程</li>
              <li>学习框架设计的最佳实践</li>
            </ul>`,
          catalog: [
            { title: '第1章 框架设计概览', page: 1, sections: ['1.1 命令式与声明式', '1.2 虚拟DOM的性能', '1.3 运行时与编译时'] },
            { title: '第2章 响应式系统', page: 25, sections: ['2.1 响应式数据与副作用', '2.2 响应式的基本实现', '2.3 分支切换与cleanup'] },
            { title: '第3章 渲染器', page: 68, sections: ['3.1 渲染器与响应式系统', '3.2 挂载与更新', '3.3 简单的diff算法'] }
          ],
          features: [
            '深入剖析Vue.js 3.0源码',
            '包含大量图表和示例代码',
            '适合想要深入理解Vue.js的开发者',
            '附赠在线示例和练习题'
          ],
          seller: {
            id: 'seller1',
            name: '前端书店',
            avatar: '/images/seller-avatar.jpg',
            rating: 4.9
          }
        }

        // 检查收藏状态
        isFavorite.value = userStore.favorites.includes(bookId)

        // 获取推荐
        recommendations.value = [
          { id: '2', title: 'JavaScript高级程序设计', cover: '/images/js-book.jpg', price: 99.80 },
          { id: '3', title: '深入浅出Node.js', cover: '/images/node-book.jpg', price: 69.90 },
          { id: '4', title: 'CSS权威指南', cover: '/images/css-book.jpg', price: 118.00 }
        ]

        // 获取相关图书
        relatedBooks.value = [
          { id: '5', title: 'Vue.js实战', author: '梁灏', cover: '/images/vue-practice.jpg', price: 79.90 },
          { id: '6', title: 'Vue.js技术揭秘', author: 'USTB Huang', cover: '/images/vue-deep.jpg', price: 59.90 },
          { id: '7', title: '前端工程化', author: '程柳锋', cover: '/images/fe-engineering.jpg', price: 89.00 },
          { id: '8', title: '现代JavaScript教程', author: 'Ilya Kantor', cover: '/images/modern-js.jpg', price: 109.00 }
        ]

        // 获取评价
        reviews.value = [
          { id: 'r1', username: '开发者小王', avatar: '/images/avatar1.jpg', date: '2023-10-15', rating: 5, content: '内容非常深入，对理解Vue.js原理有很大帮助！', helpful: false, helpfulCount: 24 },
          { id: 'r2', username: '前端工程师', avatar: '/images/avatar2.jpg', date: '2023-09-28', rating: 4, content: '书的质量很好，送货速度快，内容详实', helpful: true, helpfulCount: 18 },
          { id: 'r3', username: 'Vue学习者', avatar: '/images/avatar3.jpg', date: '2023-09-10', rating: 5, content: '看完对Vue的理解提升了一个层次，推荐！', helpful: false, helpfulCount: 32 }
        ]

        // 常见问题
        faqs.value = [
          { question: '这本书适合Vue.js初学者吗？', answer: '本书适合有一定Vue.js使用经验的开发者，初学者建议先学习Vue.js基础教程。' },
          { question: '书中的代码示例完整吗？', answer: '书中提供了完整的代码示例，并且可以在GitHub上找到所有示例代码。' },
          { question: '这本书涵盖Vue 3的哪些内容？', answer: '涵盖了Vue 3的响应式系统、组合式API、Teleport、Suspense等新特性。' }
        ]

      } catch (err) {
        error.value = '加载图书详情失败，请稍后重试'
        console.error('Error fetching book detail:', err)
      } finally {
        isLoading.value = false
      }
    }

    const getStockStatus = () => {
      if (!book.value) return ''
      if (book.value.stock === 0) return '缺货'
      if (book.value.stock <= 10) return `仅剩${book.value.stock}件`
      return '有货'
    }

    const validateQuantity = () => {
      if (!book.value) return
      if (quantity.value < 1) quantity.value = 1
      if (quantity.value > book.value.stock) quantity.value = book.value.stock
    }

    const increaseQuantity = () => {
      if (book.value && quantity.value < book.value.stock) {
        quantity.value++
      }
    }

    const decreaseQuantity = () => {
      if (quantity.value > 1) {
        quantity.value--
      }
    }

    const addToCart = async () => {
      if (!book.value || book.value.stock === 0) return

      isAddingToCart.value = true
      try {
        await cartStore.addToCart({
          ...book.value,
          quantity: quantity.value,
          selected: true
        })

        // 显示添加成功的消息
        showMessage('已添加到购物车')

      } catch (err) {
        showMessage('添加失败，请重试', 'error')
      } finally {
        isAddingToCart.value = false
      }
    }

    const buyNow = () => {
      if (!book.value || book.value.stock === 0) return

      // 先添加到购物车
      cartStore.addToCart({
        ...book.value,
        quantity: quantity.value,
        selected: true
      })

      // 跳转到结算页面
      router.push('/checkout')
    }

    const toggleFavorite = () => {
      if (!userStore.isLoggedIn) {
        router.push('/login')
        return
      }

      isFavorite.value = !isFavorite.value
      userStore.toggleFavorite(book.value.id)

      showMessage(isFavorite.value ? '已收藏' : '已取消收藏')
    }

    const shareBook = () => {
      if (navigator.share) {
        navigator.share({
          title: book.value.title,
          text: `推荐《${book.value.title}》`,
          url: window.location.href
        })
      } else {
        // 备用分享方式
        navigator.clipboard.writeText(window.location.href)
        showMessage('链接已复制到剪贴板')
      }
    }

    const goToBook = (bookId) => {
      router.push(`/book/${bookId}`)
    }

    const toggleChapter = (index) => {
      const idx = expandedChapters.value.indexOf(index)
      if (idx > -1) {
        expandedChapters.value.splice(idx, 1)
      } else {
        expandedChapters.value.push(index)
      }
    }

    const toggleFaq = (index) => {
      const idx = expandedFaqs.value.indexOf(index)
      if (idx > -1) {
        expandedFaqs.value.splice(idx, 1)
      } else {
        expandedFaqs.value.push(index)
      }
    }

    const getRatingPercentage = (rating) => {
      if (!book.value) return 0
      const count = reviews.value.filter(r => r.rating === rating).length
      return Math.round((count / reviews.value.length) * 100) || 0
    }

    const toggleHelpful = (reviewId) => {
      const review = reviews.value.find(r => r.id === reviewId)
      if (review) {
        review.helpful = !review.helpful
        review.helpfulCount += review.helpful ? 1 : -1
      }
    }

    const changePage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
        window.scrollTo(0, 0)
      }
    }

    const previewSample = (chapterIndex) => {
      if (book.value.catalog[chapterIndex]) {
        previewChapter.value = book.value.catalog[chapterIndex]
        previewChapter.value.content = `这是《${book.value.title}》第${chapterIndex + 1章的预览内容...}`
        showPreview.value = true
      }
    }

    const handleImageError = (event) => {
      event.target.src = defaultCover
    }

    const reportIssue = () => {
      router.push('/report?type=book&id=' + book.value.id)
    }

    const retryLoading = () => {
      fetchBookDetail()
    }

    const showMessage = (message, type = 'success') => {
      // 这里可以实现一个消息提示组件
      const messageEl = document.createElement('div')
      messageEl.className = `message message-${type}`
      messageEl.textContent = message
      document.body.appendChild(messageEl)

      setTimeout(() => {
        document.body.removeChild(messageEl)
      }, 3000)
    }

    // 监听路由变化
    watch(() => route.params.id, () => {
      fetchBookDetail()
    })

    // 初始化
    onMounted(() => {
      fetchBookDetail()
    })

    return {
      // 数据
      book,
      recommendations,
      relatedBooks,
      reviews,
      faqs,
      isLoading,
      error,

      // 状态
      quantity,
      activeTab,
      activeFilter,
      expandedChapters,
      expandedFaqs,
      currentPage,
      isAddingToCart,
      isFavorite,
      showPreview,
      previewChapter,

      // 计算属性
      tabs,
      reviewFilters,
      totalPages,
      filteredReviews,
      defaultCover,

      // 方法
      formatDate,
      getStockStatus,
      validateQuantity,
      increaseQuantity,
      decreaseQuantity,
      addToCart,
      buyNow,
      toggleFavorite,
      shareBook,
      goToBook,
      toggleChapter,
      toggleFaq,
      getRatingPercentage,
      toggleHelpful,
      changePage,
      previewSample,
      handleImageError,
      reportIssue,
      retryLoading
    }
  }
}
</script>

<style scoped>
.book-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.breadcrumb {
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.breadcrumb a {
  color: #1890ff;
  text-decoration: none;
}

.breadcrumb a:hover {
  text-decoration: underline;
}

.breadcrumb span {
  color: #333;
}

/* 主内容区 */
.book-detail-container {
  display: grid;
  grid-template-columns: 300px 1fr 250px;
  gap: 30px;
  margin-bottom: 40px;
}

/* 左侧区域 */
.book-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.book-cover-wrapper {
  position: relative;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-radius: 8px;
  overflow: hidden;
}

.book-cover {
  width: 100%;
  height: auto;
  display: block;
}

.book-tags {
  position: absolute;
  bottom: 10px;
  left: 10px;
  display: flex;
  gap: 5px;
}

.tag {
  background: rgba(24, 144, 255, 0.9);
  color: white;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-buttons button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.action-buttons button:hover {
  background: #f5f5f5;
}

.btn-favorite.active {
  background: #fff2f0;
  border-color: #ff7875;
  color: #ff7875;
}

.btn-favorite.active .icon-heart {
  color: #ff7875;
}

/* 中间区域 */
.book-center {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.book-title {
  font-size: 24px;
  margin: 0;
  color: #333;
}

.book-subtitle {
  font-size: 16px;
  color: #666;
  margin: 5px 0;
}

.book-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin: 15px 0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-label {
  color: #999;
  font-size: 14px;
}

.meta-value {
  color: #333;
  font-size: 14px;
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 5px;
}

.star {
  color: #ddd;
  font-size: 16px;
}

.star.filled {
  color: #faad14;
}

.rating-score {
  font-size: 18px;
  font-weight: bold;
  color: #faad14;
  margin-left: 5px;
}

.rating-count {
  color: #666;
  font-size: 14px;
}

/* 价格区域 */
.price-section {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
}

.price-current {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.price-label {
  color: #666;
}

.price-amount {
  font-size: 28px;
  color: #f5222d;
  font-weight: bold;
}

.price-original {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.discount {
  display: inline-block;
  background: #f5222d;
  color: white;
  padding: 2px 8px;
  border-radius: 2px;
  font-size: 12px;
  margin-top: 5px;
}

/* 库存区域 */
.stock-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stock-label {
  color: #666;
}

.stock-status.in-stock {
  color: #52c41a;
}

.stock-status.low-stock {
  color: #faad14;
}

.stock-status.out-of-stock {
  color: #f5222d;
}

.sales-count {
  color: #666;
  font-size: 14px;
}

/* 购买区域 */
.purchase-section {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 30px;
  align-items: center;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 15px;
}

.quantity-input {
  display: flex;
  align-items: center;
}

.btn-quantity {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: white;
  font-size: 16px;
  cursor: pointer;
}

.btn-quantity:hover:not(:disabled) {
  background: #f5f5f5;
}

.btn-quantity:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.quantity-input input {
  width: 50px;
  height: 32px;
  border: 1px solid #ddd;
  border-left: none;
  border-right: none;
  text-align: center;
  outline: none;
}

.action-buttons-main {
  display: flex;
  gap: 15px;
}

.btn-add-cart,
.btn-buy-now {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-add-cart {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #fa8c16;
}

.btn-add-cart:hover:not(:disabled) {
  background: #ffddb3;
}

.btn-buy-now {
  background: #f5222d;
  color: white;
}

.btn-buy-now:hover:not(:disabled) {
  background: #cf1322;
}

.btn-add-cart:disabled,
.btn-buy-now:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 服务信息 */
.service-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 15px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
}

/* 右侧区域 */
.book-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.seller-info,
.recommendations {
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 15px;
}

.seller-info h3,
.recommendations h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  color: #333;
}

.seller-details {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.seller-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.seller-meta {
  flex: 1;
}

.seller-name {
  font-weight: bold;
  margin-bottom: 2px;
}

.seller-rating {
  font-size: 12px;
  color: #faad14;
}

.btn-contact {
  width: 100%;
  padding: 8px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-contact:hover {
  background: #40a9ff;
}

.recommendation-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.recommendation-item:last-child {
  border-bottom: none;
}

.recommendation-item:hover {
  background: #fafafa;
}

.rec-cover {
  width: 50px;
  height: 65px;
  object-fit: cover;
}

.rec-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.rec-title {
  font-size: 12px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-price {
  font-size: 14px;
  color: #f5222d;
  font-weight: bold;
}

/* 标签页 */
.book-tabs {
  margin: 40px 0;
}

.tabs-header {
  display: flex;
  border-bottom: 2px solid #f0f0f0;
}

.tab-btn {
  padding: 12px 30px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  font-size: 16px;
  color: #666;
  cursor: pointer;
  position: relative;
  bottom: -2px;
}

.tab-btn:hover {
  color: #1890ff;
}

.tab-btn.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

.tab-count {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}

.tabs-content {
  padding: 20px 0;
}

.tab-panel {
  min-height: 200px;
}

/* 描述样式 */
.description-content {
  line-height: 1.8;
}

.description-content h3 {
  margin-bottom: 15px;
  color: #333;
}

.description-text {
  color: #555;
  margin-bottom: 20px;
}

.description-text :deep(p) {
  margin-bottom: 15px;
}

.description-text :deep(ul) {
  margin-bottom: 15px;
  padding-left: 20px;
}

.description-text :deep(li) {
  margin-bottom: 5px;
}

.features {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  margin-top: 20px;
}

.features h4 {
  margin-top: 0;
  color: #333;
}

.features ul {
  margin: 0;
  padding-left: 20px;
}

.features li {
  margin-bottom: 8px;
  color: #666;
}

/* 目录样式 */
.catalog-chapters {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.chapter-item {
  border-bottom: 1px solid #f0f0f0;
}

.chapter-item:last-child {
  border-bottom: none;
}

.chapter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.chapter-header:hover {
  background: #fafafa;
}

.chapter-title {
  flex: 1;
  color: #333;
}

.chapter-page {
  color: #999;
  font-size: 14px;
  margin-right: 10px;
}

.chapter-icon {
  transition: transform 0.3s;
  color: #999;
}

.chapter-icon.expanded {
  transform: rotate(180deg);
}

.chapter-sections {
  padding: 10px 15px 15px 30px;
  background: #f9f9f9;
}

.section-item {
  padding: 5px 0;
  color: #666;
  font-size: 14px;
}

/* 评价样式 */
.reviews-header {
  display: flex;
  gap: 50px;
  margin-bottom: 30px;
}

.reviews-header h3 {
  margin: 0;
}

.overall-rating {
  text-align: center;
  min-width: 120px;
}

.rating-number {
  font-size: 32px;
  color: #faad14;
  font-weight: bold;
}

.rating-stars-summary {
  margin: 5px 0;
}

.rating-count {
  color: #999;
  font-size: 14px;
}

.rating-distribution {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dist-label {
  width: 40px;
  color: #666;
  font-size: 14px;
}

.dist-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.dist-fill {
  height: 100%;
  background: #faad14;
  border-radius: 4px;
}

.dist-percentage {
  width: 50px;
  text-align: right;
  color: #666;
  font-size: 14px;
}

.reviews-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-btn {
  padding: 6px 15px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 15px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.filter-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.filter-btn.active {
  background: #1890ff;
  border-color: #1890ff;
  color: white;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.reviewer-name {
  font-weight: bold;
  color: #333;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-rating {
  margin-bottom: 10px;
}

.review-content {
  color: #555;
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-helpful {
  text-align: right;
}

.btn-helpful {
  padding: 4px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}

.btn-helpful:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-helpful.active {
  background: #1890ff;
  border-color: #1890ff;
  color: white;
}

.reviews-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.btn-prev,
.btn-next {
  padding: 8px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.btn-prev:hover:not(:disabled),
.btn-next:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-prev:disabled,
.btn-next:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
}

/* FAQ样式 */
.faq-list {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.faq-item {
  border-bottom: 1px solid #f0f0f0;
}

.faq-item:last-child {
  border-bottom: none;
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.faq-question:hover {
  background: #fafafa;
}

.faq-icon {
  transition: transform 0.3s;
  color: #999;
}

.faq-icon.expanded {
  transform: rotate(180deg);
}

.faq-answer {
  padding: 15px;
  padding-top: 0;
  color: #666;
  line-height: 1.6;
}

/* 相关推荐 */
.related-books {
  margin-top: 40px;
}

.related-books h3 {
  margin-bottom: 20px;
  color: #333;
}

.related-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.related-item {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.related-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.related-cover {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.related-info {
  padding: 10px;
}

.related-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  height: 39px;
}

.related-author {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.related-price {
  font-size: 16px;
  color: #f5222d;
  font-weight: bold;
}

/* 预览模态框 */
.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.preview-content {
  background: white;
  width: 80%;
  max-width: 800px;
  max-height: 80vh;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f9f9f9;
  border-bottom: 1px solid #f0f0f0;
}

.preview-header h3 {
  margin: 0;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.btn-close:hover {
  color: #333;
}

.preview-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.preview-text {
  line-height: 1.8;
  color: #333;
}

/* 加载和错误状态 */
.loading-container,
.error-container {
  text-align: center;
  padding: 100px 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon {
  width: 60px;
  height: 60px;
  background: #f5222d;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  font-weight: bold;
  margin: 0 auto 20px;
}

.error-container h3 {
  margin: 0 0 10px;
  color: #333;
}

.error-container p {
  color: #666;
  margin-bottom: 20px;
}

.btn-retry {
  padding: 10px 30px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.btn-retry:hover {
  background: #40a9ff;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .book-detail-container {
    grid-template-columns: 250px 1fr;
  }

  .book-right {
    grid-column: 1 / -1;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }

  .related-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .book-detail-container {
    grid-template-columns: 1fr;
  }

  .book-left {
    align-items: center;
  }

  .book-cover-wrapper {
    max-width: 300px;
  }

  .purchase-section {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .action-buttons-main {
    flex-direction: column;
  }

  .service-info {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .book-right {
    grid-template-columns: 1fr;
  }

  .related-list {
    grid-template-columns: 1fr;
  }

  .tabs-header {
    overflow-x: auto;
    white-space: nowrap;
  }

  .tab-btn {
    padding: 12px 15px;
  }
}
</style>