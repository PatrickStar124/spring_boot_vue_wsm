<template>
  <div class="cart-manage-container">
    <div class="header-section">
      <h2 class="page-title">购物车</h2>
      <div class="action-buttons">
        <el-button type="danger" @click="clearCart" :disabled="cartItems.length === 0">
          <el-icon><Delete /></el-icon>
          清空购物车
        </el-button>
        <el-button type="success" @click="checkout" :disabled="cartItems.length === 0">
          <el-icon><ShoppingCart /></el-icon>
          去结算
        </el-button>
      </div>
    </div>

    <!-- 购物车为空提示 -->
    <div v-if="cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车空空如也" image-size="200">
        <el-button type="primary" @click="goToProducts">去逛逛</el-button>
      </el-empty>
    </div>

    <!-- 购物车商品列表 -->
    <div v-else class="cart-content">
      <!-- 商品列表 -->
      <el-card class="cart-list-card">
        <el-table :data="cartItems" border style="width: 100%">
          <el-table-column width="60">
            <template #header>
              <el-checkbox v-model="selectAll" @change="handleSelectAll" />
            </template>
            <template #default="scope">
              <el-checkbox v-model="scope.row.selected" @change="updateSelected" />
            </template>
          </el-table-column>
          <el-table-column label="商品信息" width="350">
            <template #default="scope">
              <div class="product-info">
                <div class="product-image">
                  <img :src="scope.row.image || defaultImage" :alt="scope.row.productName" />
                </div>
                <div class="product-details">
                  <div class="product-name">{{ scope.row.productName }}</div>
                  <div class="product-author">作者：{{ scope.row.author || '未知' }}</div>
                  <div class="product-price">
                    <span class="current-price">¥{{ (scope.row.price * scope.row.discount).toFixed(2) }}</span>
                    <span v-if="scope.row.discount < 1" class="original-price">
                      ¥{{ scope.row.price.toFixed(2) }}
                    </span>
                    <el-tag v-if="scope.row.discount < 1" size="small" type="danger">
                      {{ (scope.row.discount * 10).toFixed(1) }}折
                    </el-tag>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="scope">
              ¥{{ scope.row.price.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="scope">
              <div class="quantity-control">
                <el-button :disabled="scope.row.quantity <= 1" @click="decreaseQuantity(scope.row)" circle size="small">
                  <el-icon><Minus /></el-icon>
                </el-button>
                <el-input-number v-model="scope.row.quantity" :min="1" :max="scope.row.stock"
                                 size="small" @change="updateQuantity(scope.row)" />
                <el-button :disabled="scope.row.quantity >= scope.row.stock"
                           @click="increaseQuantity(scope.row)" circle size="small">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="scope">
              <div class="subtotal">
                ¥{{ (scope.row.price * scope.row.discount * scope.row.quantity).toFixed(2) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" size="small" @click="removeItem(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 结算栏 -->
      <el-card class="checkout-card">
        <div class="checkout-content">
          <div class="selected-info">
            已选择 {{ selectedCount }} 件商品
          </div>
          <div class="total-info">
            <div class="total-amount">
              合计：<span class="amount">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="discount-info" v-if="totalDiscount > 0">
              已优惠：<span class="discount">¥{{ totalDiscount.toFixed(2) }}</span>
            </div>
          </div>
          <div class="checkout-action">
            <el-button type="success" size="large" @click="checkout">
              <el-icon><ShoppingCart /></el-icon>
              结算({{ selectedCount }})
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 结算对话框 -->
    <el-dialog v-model="checkoutDialogVisible" title="确认订单" width="600px">
      <el-form :model="orderForm" label-width="100px">
        <div class="order-summary">
          <h4>订单商品</h4>
          <div v-for="selectedItem in selectedItems" :key="selectedItem.productId" class="order-item">
            <div class="order-item-name">{{ selectedItem.productName }}</div>
            <div class="order-item-price">¥{{ (selectedItem.price * selectedItem.discount).toFixed(2) }} × {{ selectedItem.quantity }}</div>
            <div class="order-item-subtotal">¥{{ (selectedItem.price * selectedItem.discount * selectedItem.quantity).toFixed(2) }}</div>
          </div>
        </div>

        <el-divider />

        <h4>收货信息</h4>
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="orderForm.consignee" placeholder="请输入收货人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="orderForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="orderForm.address" type="textarea" :rows="2"
                    placeholder="请输入详细收货地址"></el-input>
        </el-form-item>

        <el-divider />

        <h4>支付方式</h4>
        <el-radio-group v-model="orderForm.paymentMethod" style="width: 100%;">
          <div class="payment-options">
            <el-radio label="alipay" style="margin-bottom: 10px;">
              <div class="payment-option">
                <el-icon><Goods /></el-icon>
                <span>支付宝支付</span>
              </div>
            </el-radio>
            <el-radio label="wechat" style="margin-bottom: 10px;">
              <div class="payment-option">
                <el-icon><ChatDotSquare /></el-icon>
                <span>微信支付</span>
              </div>
            </el-radio>
            <el-radio label="balance">
              <div class="payment-option">
                <el-icon><Wallet /></el-icon>
                <span>余额支付</span>
              </div>
            </el-radio>
          </div>
        </el-radio-group>

        <el-divider />

        <div class="order-total">
          <div class="order-total-item">
            <span>商品金额：</span>
            <span>¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <div class="order-total-item">
            <span>运费：</span>
            <span>¥0.00</span>
          </div>
          <div class="order-total-item total">
            <span>应付总额：</span>
            <span class="final-amount">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="checkoutDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmOrder">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Delete,
  ShoppingCart,
  Minus,
  Plus,
  Goods,
  ChatDotSquare,
  Wallet
} from '@element-plus/icons-vue'

const router = useRouter()

// 默认商品图片
const defaultImage = 'https://via.placeholder.com/80x100?text=Book'

// 购物车数据
const cartItems = ref([])
const selectAll = ref(false)
const checkoutDialogVisible = ref(false)

// 订单表单
const orderForm = reactive({
  consignee: '',
  phone: '',
  address: '',
  paymentMethod: 'alipay'
})

// 初始化购物车
const initCart = () => {
  const cartData = localStorage.getItem('shoppingCart')
  if (cartData) {
    cartItems.value = JSON.parse(cartData).map(item => ({
      ...item,
      selected: true
    }))
  }
}

// 计算属性
const selectedItems = computed(() => {
  return cartItems.value.filter(item => item.selected)
})

const selectedCount = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (item.price * item.discount * item.quantity)
  }, 0)
})

const totalDiscount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.price * (1 - item.discount) * item.quantity)
  }, 0)
})

// 方法
const handleSelectAll = (val) => {
  cartItems.value.forEach(item => {
    item.selected = val
  })
  saveCart()
}

const updateSelected = () => {
  selectAll.value = cartItems.value.every(item => item.selected)
  saveCart()
}

const decreaseQuantity = (row) => {
  if (row.quantity > 1) {
    row.quantity--
    updateQuantity(row)
  }
}

const increaseQuantity = (row) => {
  if (row.quantity < row.stock) {
    row.quantity++
    updateQuantity(row)
  } else {
    ElMessage.warning('库存不足')
  }
}

const updateQuantity = () => {
  saveCart()
}

const removeItem = (row) => {
  ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = cartItems.value.findIndex(cartItem => cartItem.productId === row.productId)
    if (index !== -1) {
      cartItems.value.splice(index, 1)
      saveCart()
      ElMessage.success('删除成功')
    }
  })
}

const clearCart = () => {
  ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartItems.value = []
    localStorage.removeItem('shoppingCart')
    ElMessage.success('购物车已清空')
  })
}

const saveCart = () => {
  localStorage.setItem('shoppingCart', JSON.stringify(cartItems.value))
}

const goToProducts = () => {
  router.push('/ProductManage')
}

const checkout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }

  // 检查库存
  const outOfStockItems = selectedItems.value.filter(item => item.quantity > item.stock)
  if (outOfStockItems.length > 0) {
    ElMessage.error('部分商品库存不足，请调整数量')
    return
  }

  // 获取当前用户信息
  const user = JSON.parse(sessionStorage.getItem('CurUser') || '{}')
  if (user && user.name) {
    orderForm.consignee = user.name
    orderForm.phone = user.phone || ''
  }

  checkoutDialogVisible.value = true
}

const confirmOrder = () => {
  if (!orderForm.consignee || !orderForm.phone || !orderForm.address) {
    ElMessage.warning('请填写完整的收货信息')
    return
  }

  // 构建订单数据
  const orderData = {
    orderNo: 'ORD' + Date.now(),
    userId: JSON.parse(sessionStorage.getItem('CurUser') || '{}').id || 0,
    items: selectedItems.value.map(item => ({
      productId: item.productId,
      productName: item.productName,
      price: item.price,
      discount: item.discount,
      quantity: item.quantity,
      subtotal: item.price * item.discount * item.quantity
    })),
    totalAmount: totalAmount.value,
    consignee: orderForm.consignee,
    phone: orderForm.phone,
    address: orderForm.address,
    paymentMethod: orderForm.paymentMethod
  }

  // 保存订单到本地存储（模拟后端API）
  let orders = JSON.parse(localStorage.getItem('userOrders') || '[]')
  orders.push({
    ...orderData,
    id: orders.length + 1,
    status: 'pending',
    createTime: new Date().toISOString()
  })
  localStorage.setItem('userOrders', JSON.stringify(orders))

  // 从购物车移除已下单商品
  cartItems.value = cartItems.value.filter(cartItem =>
      !selectedItems.value.some(selectedItem => selectedItem.productId === cartItem.productId)
  )
  saveCart()

  checkoutDialogVisible.value = false
  ElMessage.success('订单提交成功！')

  // 跳转到订单页面
  router.push('/OrderManage')
}

// 初始化
onMounted(() => {
  initCart()
})
</script>

<style scoped>
/* 样式保持不变，与之前的代码相同 */
.cart-manage-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.empty-cart {
  text-align: center;
  padding: 100px 0;
  background-color: #fff;
  border-radius: 10px;
}

.cart-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-list-card {
  flex: 1;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 100px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 5px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
  font-size: 16px;
}

.product-author {
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 14px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-control .el-input-number {
  width: 80px;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

.checkout-card {
  position: sticky;
  bottom: 0;
  background-color: #fff;
}

.checkout-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-info {
  font-size: 16px;
  color: #333;
}

.total-info {
  text-align: right;
}

.total-amount {
  font-size: 20px;
  color: #333;
  margin-bottom: 5px;
}

.total-amount .amount {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}

.discount-info {
  color: #67c23a;
  font-size: 14px;
}

.checkout-action .el-button {
  height: 48px;
  padding: 0 30px;
  font-size: 16px;
}

.order-summary {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.order-item:last-child {
  border-bottom: none;
}

.order-item-name {
  flex: 2;
}

.order-item-price {
  flex: 1;
  text-align: center;
  color: #999;
}

.order-item-subtotal {
  flex: 1;
  text-align: right;
  color: #f56c6c;
  font-weight: bold;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 5px;
  cursor: pointer;
}

.payment-option:hover {
  border-color: #409eff;
}

.order-total {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 5px;
}

.order-total-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #666;
}

.order-total-item.total {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 2px solid #eee;
  color: #333;
  font-size: 18px;
}

.final-amount {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .checkout-content {
    flex-direction: column;
    gap: 20px;
  }

  .total-info {
    text-align: center;
  }
}
</style>