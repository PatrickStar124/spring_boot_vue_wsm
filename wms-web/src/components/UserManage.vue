<template>
  <div class="user-manage-container">
    <div class="header-section">
      <h2 class="page-title">用户管理</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
        <el-button type="success" @click="exportData">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="用户姓名">
              <el-input
                  v-model="searchForm.name"
                  placeholder="请输入用户姓名"
                  clearable>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="用户账号">
              <el-input
                  v-model="searchForm.username"
                  placeholder="请输入用户账号"
                  clearable>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="性别">
              <el-select
                  v-model="searchForm.gender"
                  placeholder="请选择性别"
                  clearable
                  style="width: 100%;">
                <el-option label="全部" value=""></el-option>
                <el-option label="男" :value="1"></el-option>
                <el-option label="女" :value="0"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select
                  v-model="searchForm.status"
                  placeholder="请选择状态"
                  clearable
                  style="width: 100%;">
                <el-option label="全部" value=""></el-option>
                <el-option label="正常" :value="1"></el-option>
                <el-option label="禁用" :value="0"></el-option>
              </el-select>
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

    <!-- 用户列表 -->
    <el-card class="data-card">
      <el-table
          :data="userList"
          border
          style="width: 100%"
          v-loading="loading">
        <el-table-column prop="username" label="用户账号" width="120"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === 1 ? 'primary' : 'danger'">
              {{ scope.row.gender === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="points" label="积分" width="100">
          <template #default="scope">
            {{ scope.row.points || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="100">
          <template #default="scope">
            <el-tag :type="getLevelTagType(scope.row.level)">
              {{ scope.row.level || 1 }}级
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="action-buttons-cell">
              <el-button
                  type="primary"
                  size="small"
                  @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button
                  type="warning"
                  size="small"
                  @click="handleResetPassword(scope.row)">
                重置密码
              </el-button>
              <el-popconfirm
                  title="确定要删除这位用户吗？"
                  @confirm="handleDelete">
                <template #reference>
                  <el-button type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="form.id ? '编辑用户' : '新增用户'"
        width="500px">
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px">
        <el-form-item label="用户姓名" prop="name">
          <el-input
              v-model="form.name"
              placeholder="请输入用户姓名"
              style="width: 100%;">
          </el-input>
        </el-form-item>
        <el-form-item label="用户账号" prop="username">
          <el-input
              v-model="form.username"
              placeholder="请输入用户账号"
              style="width: 100%;"
              :disabled="!!form.id">
          </el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input
              v-model="form.phone"
              placeholder="请输入联系电话"
              style="width: 100%;">
          </el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
              v-model="form.email"
              placeholder="请输入邮箱"
              style="width: 100%;">
          </el-input>
        </el-form-item>
        <el-form-item label="初始密码" prop="password" v-if="!form.id">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入初始密码"
              style="width: 100%;">
          </el-input>
        </el-form-item>
        <el-form-item label="用户等级" prop="level">
          <el-input-number
              v-model="form.level"
              :min="1"
              :max="10"
              controls-position="right"
              style="width: 100%;">
          </el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
              v-model="form.status"
              :active-value="1"
              :inactive-value="0"
              active-text="正常"
              inactive-text="禁用">
          </el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
        v-model="resetPasswordDialogVisible"
        title="重置密码"
        width="400px">
      <el-form :model="resetPasswordForm" label-width="100px">
        <el-form-item label="用户姓名">
          <span>{{ currentUser?.name }}</span>
        </el-form-item>
        <el-form-item label="用户账号">
          <span>{{ currentUser?.username }}</span>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
              v-model="resetPasswordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              style="width: 100%;">
          </el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="resetPasswordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              style="width: 100%;">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResetPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Download
} from '@element-plus/icons-vue'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const resetPasswordDialogVisible = ref(false)
const formRef = ref()
const currentUser = ref(null)

const searchForm = reactive({
  name: '',
  username: '',
  gender: '',
  status: ''
})

const form = reactive({
  id: '',
  name: '',
  username: '',
  gender: 1,
  phone: '',
  email: '',
  password: '',
  level: 1,
  status: 1
})

const resetPasswordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const userList = ref([])

// 验证规则
const rules = {
  name: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户账号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入初始密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请输入用户等级', trigger: 'blur' }
  ]
}

// 等级标签类型
const getLevelTagType = (level) => {
  if (level >= 8) return 'danger'
  if (level >= 5) return 'warning'
  if (level >= 3) return 'primary'
  return 'success'
}

// 初始化数据
const initData = async () => {
  await loadUserList()
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    // 模拟数据
    userList.value = [
      {
        id: 1,
        username: 'admin',
        name: '管理员',
        gender: 1,
        phone: '13800138001',
        email: 'admin@example.com',
        points: 1000,
        level: 10,
        status: 1,
        createTime: '2023-01-15T10:30:00'
      },
      {
        id: 2,
        username: 'zhangsan',
        name: '张三',
        gender: 1,
        phone: '13800138002',
        email: 'zhangsan@example.com',
        points: 500,
        level: 5,
        status: 1,
        createTime: '2023-02-20T14:15:00'
      },
      {
        id: 3,
        username: 'lisi',
        name: '李四',
        gender: 0,
        phone: '13800138003',
        email: 'lisi@example.com',
        points: 200,
        level: 3,
        status: 1,
        createTime: '2023-03-10T09:45:00'
      },
      {
        id: 4,
        username: 'wangwu',
        name: '王五',
        gender: 1,
        phone: '13800138004',
        email: 'wangwu@example.com',
        points: 50,
        level: 1,
        status: 0,
        createTime: '2023-04-05T16:20:00'
      }
    ]
    total.value = userList.value.length
  } catch (error) {
    ElMessage.error('加载用户列表失败')
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

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadUserList()
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.username = ''
  searchForm.gender = ''
  searchForm.status = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadUserList()
}

// 显示新增对话框
const showAddDialog = () => {
  resetForm()
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: '',
    name: '',
    username: '',
    gender: 1,
    phone: '',
    email: '',
    password: '',
    level: 1,
    status: 1
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 处理编辑
const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

// 处理重置密码
const handleResetPassword = (row) => {
  currentUser.value = row
  resetPasswordForm.newPassword = ''
  resetPasswordForm.confirmPassword = ''
  resetPasswordDialogVisible.value = true
}

// 确认重置密码
const confirmResetPassword = async () => {
  if (!resetPasswordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }

  if (resetPasswordForm.newPassword !== resetPasswordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  try {
    // 这里应该是API调用
    ElMessage.success('密码重置成功')
    resetPasswordDialogVisible.value = false
  } catch (error) {
    ElMessage.error('密码重置失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该是API调用
        if (form.id) {
          ElMessage.success('更新成功')
        } else {
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadUserList()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 处理删除
const handleDelete = async () => {
  try {
    // 这里应该是API调用
    ElMessage.success('删除成功')
    loadUserList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 导出数据
const exportData = () => {
  // 这里实现导出逻辑
  ElMessage.success('导出功能开发中')
}

// 初始化
onMounted(() => {
  initData()
})
</script>

<style scoped>
.user-manage-container {
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

.action-buttons-cell {
  display: flex;
  gap: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>