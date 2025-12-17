<template>
  <div class="statistic-page-container">
    <div class="header-section">
      <h2 class="page-title">数据统计</h2>
      <div class="action-buttons">
        <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="dateRangeChange"
            style="width: 240px; margin-right: 10px;">
        </el-date-picker>
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="success" @click="exportReport">
          <el-icon><Document /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.bookCount }}</div>
              <div class="stat-label">图书总数</div>
              <div class="stat-change">
                <span class="change-up">
                  <el-icon><Top /></el-icon>
                  {{ stats.bookIncrease }} 本
                </span>
                <span class="change-text">较上月</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.readerCount }}</div>
              <div class="stat-label">读者总数</div>
              <div class="stat-change">
                <span class="change-up">
                  <el-icon><Top /></el-icon>
                  {{ stats.readerIncrease }} 人
                </span>
                <span class="change-text">较上月</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon><Collection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.borrowCount }}</div>
              <div class="stat-label">本月借阅</div>
              <div class="stat-change">
                <span :class="stats.borrowChange >= 0 ? 'change-up' : 'change-down'">
                  <el-icon v-if="stats.borrowChange >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(stats.borrowChange) }} 次
                </span>
                <span class="change-text">较上月</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.returnRate }}%</div>
              <div class="stat-label">归还率</div>
              <div class="stat-change">
                <span :class="stats.returnRateChange >= 0 ? 'change-up' : 'change-down'">
                  <el-icon v-if="stats.returnRateChange >= 0"><Top /></el-icon>
                  <el-icon v-else><Bottom /></el-icon>
                  {{ Math.abs(stats.returnRateChange) }}%
                </span>
                <span class="change-text">较上月</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 借阅趋势图 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>借阅趋势</span>
              <el-select
                  v-model="trendType"
                  size="small"
                  style="width: 120px;"
                  @change="loadBorrowTrend">
                <el-option label="按日" value="day"></el-option>
                <el-option label="按周" value="week"></el-option>
                <el-option label="按月" value="month"></el-option>
              </el-select>
            </div>
          </template>
          <div ref="borrowTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 图书分类统计 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>图书分类统计</span>
            </div>
          </template>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 热门图书排行 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>热门图书排行</span>
            </div>
          </template>
          <div class="hot-books">
            <el-table :data="hotBooks" style="width: 100%">
              <el-table-column label="排名" width="60">
                <template #default="scope">
                  <div class="rank-cell">
                    <span :class="getRankClass(scope.$index + 1)">
                      {{ scope.$index + 1 }}
                    </span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="bookName" label="图书名称"></el-table-column>
              <el-table-column prop="author" label="作者" width="120"></el-table-column>
              <el-table-column prop="borrowCount" label="借阅次数" width="100" sortable>
                <template #default="scope">
                  {{ scope.row.borrowCount }} 次
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

      <!-- 活跃读者排行 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>活跃读者排行</span>
            </div>
          </template>
          <div class="active-readers">
            <el-table :data="activeReaders" style="width: 100%">
              <el-table-column label="排名" width="60">
                <template #default="scope">
                  <div class="rank-cell">
                    <span :class="getRankClass(scope.$index + 1)">
                      {{ scope.$index + 1 }}
                    </span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="readerName" label="读者姓名" width="120"></el-table-column>
              <el-table-column prop="readerNo" label="读者编号" width="120"></el-table-column>
              <el-table-column prop="borrowCount" label="借阅次数" width="100" sortable>
                <template #default="scope">
                  {{ scope.row.borrowCount }} 次
                </template>
              </el-table-column>
              <el-table-column prop="lastBorrowDate" label="最近借阅" width="150">
                <template #default="scope">
                  {{ formatDate(scope.row.lastBorrowDate) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  Refresh,
  Document,
  Reading,
  User,
  Collection,
  Clock,
  Top,
  Bottom
} from '@element-plus/icons-vue'

const dateRange = ref([])
const trendType = ref('month')

const stats = reactive({
  bookCount: 1256,
  bookIncrease: 24,
  readerCount: 289,
  readerIncrease: 15,
  borrowCount: 356,
  borrowChange: 12,
  returnRate: 94.5,
  returnRateChange: 1.2
})

const hotBooks = ref([])
const activeReaders = ref([])

let borrowTrendChart = null
let categoryChart = null

// 初始化图表
const initCharts = () => {
  // 销毁已有图表
  if (borrowTrendChart) {
    borrowTrendChart.dispose()
  }
  if (categoryChart) {
    categoryChart.dispose()
  }

  // 初始化借阅趋势图
  const trendChartDom = document.querySelector('.chart-container')
  if (trendChartDom) {
    borrowTrendChart = echarts.init(trendChartDom)
    loadBorrowTrend()
  }

  // 初始化分类统计图
  const categoryChartDom = document.querySelector('.chart-container:last-child')
  if (categoryChartDom) {
    categoryChart = echarts.init(categoryChartDom)
    loadCategoryData()
  }
}

// 加载借阅趋势数据
const loadBorrowTrend = () => {
  // 模拟数据
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['借阅量', '归还量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendType.value === 'day'
          ? ['10-01', '10-02', '10-03', '10-04', '10-05', '10-06', '10-07']
          : trendType.value === 'week'
              ? ['第1周', '第2周', '第3周', '第4周']
              : ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '借阅量',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210],
        smooth: true,
        lineStyle: {
          color: '#409eff'
        },
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '归还量',
        type: 'line',
        data: [110, 125, 95, 130, 85, 220, 200],
        smooth: true,
        lineStyle: {
          color: '#67c23a'
        },
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }

  if (borrowTrendChart) {
    borrowTrendChart.setOption(option)
  }
}

// 加载分类数据
const loadCategoryData = () => {
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '图书分类',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 335, name: '文学' },
          { value: 310, name: '科技' },
          { value: 234, name: '历史' },
          { value: 135, name: '哲学' },
          { value: 154, name: '艺术' },
          { value: 88, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  if (categoryChart) {
    categoryChart.setOption(option)
  }
}

// 加载热门图书
const loadHotBooks = () => {
  hotBooks.value = [
    { id: 1, bookName: '红楼梦', author: '曹雪芹', borrowCount: 156 },
    { id: 2, bookName: '三体', author: '刘慈欣', borrowCount: 142 },
    { id: 3, bookName: 'Java编程思想', author: 'Bruce Eckel', borrowCount: 128 },
    { id: 4, bookName: '活着', author: '余华', borrowCount: 115 },
    { id: 5, bookName: '百年孤独', author: '加西亚·马尔克斯', borrowCount: 98 }
  ]
}

// 加载活跃读者
const loadActiveReaders = () => {
  activeReaders.value = [
    { id: 1, readerName: '张三', readerNo: 'R2023001', borrowCount: 24, lastBorrowDate: '2023-10-15' },
    { id: 2, readerName: '李四', readerNo: 'R2023002', borrowCount: 18, lastBorrowDate: '2023-10-12' },
    { id: 3, readerName: '王五', readerNo: 'R2023003', borrowCount: 15, lastBorrowDate: '2023-10-10' },
    { id: 4, readerName: '赵六', readerNo: 'R2023004', borrowCount: 12, lastBorrowDate: '2023-10-08' },
    { id: 5, readerName: '钱七', readerNo: 'R2023005', borrowCount: 10, lastBorrowDate: '2023-10-05' }
  ]
}

// 排名样式
const getRankClass = (rank) => {
  if (rank === 1) return 'rank-first'
  if (rank === 2) return 'rank-second'
  if (rank === 3) return 'rank-third'
  return 'rank-other'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split('T')[0]
}

// 日期范围变化
const dateRangeChange = () => {
  refreshData()
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据已刷新')
  // 这里应该重新加载所有数据
  loadHotBooks()
  loadActiveReaders()
  loadBorrowTrend()
  loadCategoryData()
}

// 导出报表
const exportReport = () => {
  ElMessage.success('报表导出功能开发中')
}

// 初始化
onMounted(() => {
  // 设置默认日期范围为最近一个月
  const end = new Date()
  const start = new Date()
  start.setMonth(start.getMonth() - 1)
  dateRange.value = [start, end]

  initCharts()
  loadHotBooks()
  loadActiveReaders()

  // 监听窗口大小变化，重绘图表
  window.addEventListener('resize', () => {
    if (borrowTrendChart) borrowTrendChart.resize()
    if (categoryChart) categoryChart.resize()
  })
})

// 组件销毁前清理
onBeforeUnmount(() => {
  if (borrowTrendChart) {
    borrowTrendChart.dispose()
  }
  if (categoryChart) {
    categoryChart.dispose()
  }
  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.statistic-page-container {
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
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
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
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin: 5px 0;
}

.stat-change {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
}

.change-up {
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 2px;
}

.change-down {
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 2px;
}

.change-text {
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 10px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.hot-books, .active-readers {
  padding: 10px 0;
}

.rank-cell {
  text-align: center;
}

.rank-first, .rank-second, .rank-third, .rank-other {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  border-radius: 50%;
  text-align: center;
  font-weight: bold;
}

.rank-first {
  background-color: #fde2e2;
  color: #f56c6c;
}

.rank-second {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.rank-third {
  background-color: #ecf5ff;
  color: #409eff;
}

.rank-other {
  background-color: #f0f2f5;
  color: #909399;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .chart-row .el-col {
    width: 100%;
  }

  .stats-row .el-col {
    width: 50%;
  }
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .stats-row .el-col {
    width: 100%;
  }
}
</style>