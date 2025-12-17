<template>
  <div class="order-manage-container">
    <div class="header-section">
      <h2 class="page-title">订单管理</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="exportData">
          <el-icon><Download /></el-icon>
          导出订单
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="订单号">
              <el-input
                  v-model="searchForm.orderNo"
                  placeholder="请输入订单号"
                  clearable>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="用户姓名">
              <el-input
                  v-model="searchForm.userName"
                  placeholder="请输入用户姓名"
                  clearable>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="订单状态">
              <el-select
                  v-model="searchForm.status"
                  placeholder="请选择状态"
                  clearable
                  style="width: 100%;">
                <el-option label="全部" value=""></el-option>
                <el-option label="待支付" value="pending"></el-option>
                <el-option label="已支付" value="paid"></el-option>
                <el-option label="已发货" value="shipped"></el-option>
                <el-option label="已完成" value="completed"></el-option>
                <el-option label="已取消" value="cancelled"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="下单日期">
              <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="search-buttons">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card class="data-card">
      <el-table
          :data="orderList"
          border
          style="width: 100%"
          v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column prop="userName" label="用户" width="120">
          <template #default="scope">
            <div>{{ scope.row.userName }}</div>
            <div class="user-no">({{ scope.row.userNo }})</div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="scope">
            <div class="amount">¥{{ scope.row.totalAmount?.toFixed(2) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="itemCount" label="商品数量" width="100">
          <template #default="scope">
            {{ scope.row.itemCount }}件
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="action-buttons-cell">
              <el-button type="primary" size="small" @click="viewDetail(scope.row)">
                详情
              </el-button>
              <el-button v-if="scope.row.status === 'pending'" type="success" size="small" @click="handlePay(scope.row)">
                确认支付
              </el-button>
              <el-button v-if="scope.row.status === 'paid'" type="warning" size="small" @click="handleShip(scope.row)">
                发货
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange">
        </el-pagination>
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
        v-model="detailDialogVisible"
        title="订单详情"
        width="700px">
      <div v-if="currentOrder" class="order-detail">
        <div class="order-info">
          <div class="info-row">
            <span class="label">订单号：</span>
            <span class="value">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">用户信息：</span>
            <span class="value">{{ currentOrder.userName }} ({{ currentOrder.userNo }})</span>
          </div>
          <div class="info-row">
            <span class="label">下单时间：</span>
            <span class="value">{{ formatDateTime(currentOrder.createTime) }}</span>
          </div>
          <div class="info-row">
            <span class="label">订单状态：</span>
            <el-tag :type="getStatusTagType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </div>
          <div class="info-row">
            <span class="label">收货信息：</span>
            <span class="value">{{ currentOrder.consignee }} {{ currentOrder.phone }} {{ currentOrder.address }}</span>
          </div>
          <div class="info-row">
            <span class="label">支付方式：</span>
            <span class="value">{{ getPaymentMethodText(currentOrder.paymentMethod) }}</span>
          </div>
        </div>

        <el-divider />

        <div class="order-items">
          <h4>商品清单</h4>
          <el-table :data="currentOrder.items" border style="width: 100%">
            <el-table-column prop="productName" label="商品名称"></el-table-column>
            <el-table-column prop="price" label="单价" width="100">
              <template #default="scope">
                ¥{{ scope.row.price?.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80">
              <template #default="scope">
                {{ scope.row.quantity }}
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="scope">
                ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-divider />

        <div class="order-summary">
          <div class="summary-row">
            <span class="label">商品金额：</span>
            <span class="value">¥{{ currentOrder.totalAmount?.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span class="label">运费：</span>
            <span class="value">¥0.00</span>
          </div>
          <div class="summary-row total">
            <span class="label">应付总额：</span>
            <span class="value total-amount">¥{{ currentOrder.totalAmount?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button v-if="currentOrder?.status === 'pending'" type="primary" @click="handlePay(currentOrder)">
          确认支付
        </el-button>
        <el-button v-if="currentOrder?.status === 'paid'" type="warning" @click="handleShip(currentOrder)">
          发货
        </el-button>
        <el-button v-if="currentOrder?.status === 'shipped'" type="success" @click="handleComplete(currentOrder)">
          确认收货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download
} from '@element-plus/icons-vue'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)

const searchForm = reactive({
  orderNo: '',
  userName: '',
  status: '',
  dateRange: []
})

const orderList = ref([])

// 初始化数据
const initData = async () => {
  await loadOrderList()
}

// 加载订单列表
const loadOrderList = async () => {
  loading.value = true
  try {
    // 从本地存储获取订单数据
    const orders = JSON.parse(localStorage.getItem('userOrders') || '[]')

    // 模拟一些数据
    if (orders.length === 0) {
      orderList.value = [
        {
          id: 1,
          orderNo: 'ORD2023100001',
          userName: '张三',
          userNo: 'user001',
          totalAmount: 125.50,
          itemCount: 2,
          status: 'completed',
          createTime: '2023-10-01T14:30:00',
          consignee: '张三',
          phone: '13800138001',
          address: '北京市海淀区',
          paymentMethod: 'alipay',
          items: [
            { productName: '红楼梦', price: 59.70, quantity: 1 },
            { productName: '三国演义', price: 65.80, quantity: 1 }
          ]
        },
        {
          id: 2,
          orderNo: 'ORD2023100002',
          userName: '李四',
          userNo: 'user002',
          totalAmount: 89.90,
          itemCount: 1,
          status: 'shipped',
          createTime: '2023-10-02T09:15:00',
          consignee: '李四',
          phone: '13800138002',
          address: '上海市浦东新区',
          paymentMethod: 'wechat',
          items: [
            { productName: 'Java编程思想', price: 89.90, quantity: 1 }
          ]
        },
        {
          id: 3,
          orderNo: 'ORD2023100003',
          userName: '王五',
          userNo: 'user003',
          totalAmount: 256.40,
          itemCount: 3,
          status: 'paid',
          createTime: '2023-10-03T11:20:00',
          consignee: '王五',
          phone: '13800138003',
          address: '广州市天河区',
          paymentMethod: 'alipay',
          items: [
            { productName: '三体', price: 68.80, quantity: 2 },
            { productName: '百年孤独', price: 45.80, quantity: 1 }
          ]
        }
      ]
    } else {
      orderList.value = orders.map(order => ({
        ...order,
        userName: order.consignee || '未知',
        userNo: 'user' + order.userId?.toString().padStart(3, '0') || 'unknown',
        itemCount: order.items?.length || 0
      }))
    }

    total.value = orderList.value.length
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN')
}

// 状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'paid': 'primary',
    'shipped': 'info',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return typeMap[status] || ''
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待支付',
    'paid': '已支付',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

const getPaymentMethodText = (method) => {
  const methodMap = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'balance': '余额支付'
  }
  return methodMap[method] || method
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadOrderList()
}

const resetSearch = () => {
  searchForm.orderNo = ''
  searchForm.userName = ''
  searchForm.status = ''
  searchForm.dateRange = []
  handleSearch()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadOrderList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadOrderList()
}

// 查看详情
const viewDetail = (order) => {
  currentOrder.value = order
  detailDialogVisible.value = true
}

// 处理支付
const handlePay = (order) => {
  ElMessageBox.confirm('确认该订单已支付吗？', '确认支付', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 更新订单状态
    order.status = 'paid'
    updateOrder(order)
    ElMessage.success('订单状态已更新为已支付')
  })
}

// 处理发货
const handleShip = (order) => {
  ElMessageBox.confirm('确认该订单已发货吗？', '确认发货', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    order.status = 'shipped'
    updateOrder(order)
    ElMessage.success('订单状态已更新为已发货')
  })
}

// 处理完成
const handleComplete = (order) => {
  ElMessageBox.confirm('确认该订单已完成吗？', '确认收货', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    order.status = 'completed'
    updateOrder(order)
    ElMessage.success('订单状态已更新为已完成')
  })
}

// 更新订单
const updateOrder = (order) => {
  // 更新本地存储中的订单
  let orders = JSON.parse(localStorage.getItem('userOrders') || '[]')
  const index = orders.findIndex(o => o.orderNo === order.orderNo)
  if (index !== -1) {
    orders[index] = order
    localStorage.setItem('userOrders', JSON.stringify(orders))
  }

  // 更新列表
  const listIndex = orderList.value.findIndex(o => o.orderNo === order.orderNo)
  if (listIndex !== -1) {
    orderList.value[listIndex] = order
  }

  if (currentOrder.value && currentOrder.value.orderNo === order.orderNo) {
    currentOrder.value = order
  }
}

// 导出数据
const exportData = () => {
  ElMessage.success('导出功能开发中')
}

// 初始化
onMounted(() => {
  initData()
})
</script>

<style scoped>
.order-manage-container {
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

.search-card {
  margin-bottom: 20px;
}

.search-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.data-card {
  margin-bottom: 20px;
}

.user-no {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
}

.action-buttons-cell {
  display: flex;
  gap: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.order-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.order-info .info-row {
  margin-bottom: 15px;
  display: flex;
}

.order-info .label {
  color: #666;
  width: 100px;
  flex-shrink: 0;
  font-weight: 500;
}

.order-info .value {
  color: #333;
  flex: 1;
}

.order-items h4 {
  margin-bottom: 15px;
  color: #333;
}

.order-summary {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 5px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #666;
}

.summary-row.total {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 2px solid #eee;
  color: #333;
  font-size: 18px;
}

.total-amount {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
</style>