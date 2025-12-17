<template>
  <div>
    <div style="margin-bottom: 20px;">
      <el-input v-model="searchParam.productName" placeholder="请输入商品名称" suffix-icon="Search"
                style="width: 200px;" @keyup.enter="loadPost"></el-input>
      <el-input v-model="searchParam.author" placeholder="请输入作者" style="width: 200px; margin-left: 10px;"
                @keyup.enter="loadPost"></el-input>
      <el-select v-model="searchParam.category" filterable placeholder="请选择分类"
                 style="margin-left: 10px; width: 150px;">
        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value">
        </el-option>
      </el-select>
      <el-button type="primary" style="margin-left: 10px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
      <el-button type="primary" style="margin-left: 10px;" @click="add">新增商品</el-button>
    </div>

    <el-table :data="tableData" :header-cell-style="{ background: '#f2f5fc', color: '#555555' }" border>
      <el-table-column prop="id" label="ID" width="60"></el-table-column>
      <el-table-column prop="isbn" label="ISBN" width="180"></el-table-column>
      <el-table-column prop="productName" label="商品名称" width="200"></el-table-column>
      <el-table-column prop="author" label="作者" width="120"></el-table-column>
      <el-table-column prop="publisher" label="出版社" width="180"></el-table-column>
      <el-table-column prop="publishDate" label="出版日期" width="120">
        <template #default="scope">
          {{ formatDate(scope.row.publishDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100">
        <template #default="scope">
          <el-tag :type="getCategoryTagType(scope.row.category)">
            {{ getCategoryLabel(scope.row.category) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template #default="scope">
          <span class="price">¥{{ scope.row.price?.toFixed(2) }}</span>
          <div v-if="scope.row.discount < 1" class="discount-info">
            <span class="original-price">¥{{ (scope.row.price / scope.row.discount).toFixed(2) }}</span>
            <el-tag size="small" type="danger">{{ (scope.row.discount * 10).toFixed(1) }}折</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80">
        <template #default="scope">
          <span :class="getStockClass(scope.row.stock)">
            {{ scope.row.stock }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '在售' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" type="success" @click="mod(scope.row)">编辑</el-button>
          <el-button size="small" type="primary" @click="addToCart(scope.row)"
                     :disabled="scope.row.stock <= 0">
            加入购物车
          </el-button>
          <el-popconfirm title="确定删除这个商品吗？" @confirm="del(scope.row.id)" style="margin-left: 5px;">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pageNum" :page-sizes="[5, 10, 20, 30]" :page-size="pageSize"
                   layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="form.id ? '编辑商品' : '新增商品'" v-model="centerDialogVisible" width="40%" center>
      <el-form ref="formRef" :rules="rules" :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="form.isbn" placeholder="请输入ISBN号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入商品名称"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="请输入作者"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版社" prop="publisher">
              <el-input v-model="form.publisher" placeholder="请输入出版社"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出版日期" prop="publishDate">
              <el-date-picker v-model="form.publishDate" type="date" placeholder="选择出版日期"
                              format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
                <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :min="0" :step="0.01" :precision="2"
                               placeholder="请输入价格" style="width: 100%;">
                <template #prepend>¥</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="折扣" prop="discount">
              <el-input-number v-model="form.discount" :min="0.1" :max="1" :step="0.1"
                               placeholder="请输入折扣" style="width: 100%;">
                <template #append>折</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" placeholder="请输入库存数量"
                               style="width: 100%;">
                <template #append>本</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="4"
                    placeholder="请输入商品描述"></el-input>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在售</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="centerDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 加入购物车对话框 -->
    <el-dialog v-model="cartDialogVisible" title="加入购物车" width="300px">
      <div style="text-align: center; padding: 20px;">
        <div style="margin-bottom: 15px; font-size: 16px;">{{ selectedProduct?.productName }}</div>
        <div style="margin-bottom: 20px; color: #e6a23c; font-size: 20px;">
          ¥{{ (selectedProduct?.price * selectedProduct?.discount).toFixed(2) }}
        </div>
        <div style="margin-bottom: 20px;">
          <span style="margin-right: 10px;">数量：</span>
          <el-input-number v-model="cartQuantity" :min="1" :max="selectedProduct?.stock || 1"
                           size="small"></el-input-number>
        </div>
        <div style="margin-bottom: 25px; color: #999;">
          库存：{{ selectedProduct?.stock }}本
        </div>
        <el-button type="primary" @click="confirmAddToCart" style="width: 100%;">加入购物车</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

export default {
  name: "ProductManage",
  setup() {
    const { proxy } = getCurrentInstance()

    const tableData = ref([])
    const pageSize = ref(10)
    const pageNum = ref(1)
    const total = ref(0)
    const centerDialogVisible = ref(false)
    const cartDialogVisible = ref(false)
    const formRef = ref()
    const selectedProduct = ref(null)
    const cartQuantity = ref(1)

    const searchParam = reactive({
      productName: '',
      author: '',
      category: ''
    })

    const form = reactive({
      id: '',
      isbn: '',
      productName: '',
      author: '',
      publisher: '',
      publishDate: '',
      category: '',
      price: 0,
      discount: 1.0,
      stock: 0,
      description: '',
      status: 1
    })

    const categories = ref([
      { value: 'literature', label: '文学' },
      { value: 'technology', label: '科技' },
      { value: 'history', label: '历史' },
      { value: 'philosophy', label: '哲学' },
      { value: 'art', label: '艺术' },
      { value: 'economy', label: '经济' },
      { value: 'novel', label: '小说' },
      { value: 'children', label: '儿童' },
      { value: 'education', label: '教育' },
      { value: 'other', label: '其他' }
    ])

    // 验证规则
    const rules = reactive({
      isbn: [
        { required: true, message: '请输入ISBN号', trigger: 'blur' },
        { pattern: /^(?:\d{13}|\d{10})$/, message: 'ISBN格式不正确(10或13位数字)', trigger: 'blur' }
      ],
      productName: [
        { required: true, message: '请输入商品名称', trigger: 'blur' },
        { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
      ],
      author: [
        { required: true, message: '请输入作者', trigger: 'blur' }
      ],
      publisher: [
        { required: true, message: '请输入出版社', trigger: 'blur' }
      ],
      publishDate: [
        { required: true, message: '请选择出版日期', trigger: 'change' }
      ],
      category: [
        { required: true, message: '请选择分类', trigger: 'change' }
      ],
      price: [
        { required: true, message: '请输入价格', trigger: 'blur' },
        { type: 'number', min: 0, message: '价格不能为负数', trigger: 'blur' }
      ],
      stock: [
        { required: true, message: '请输入库存数量', trigger: 'blur' },
        { type: 'integer', min: 0, message: '库存不能为负数', trigger: 'blur' }
      ],
      discount: [
        { required: true, message: '请输入折扣', trigger: 'blur' },
        { type: 'number', min: 0.1, max: 1, message: '折扣在0.1到1之间', trigger: 'blur' }
      ]
    })

    // 分类标签类型
    const getCategoryTagType = (category) => {
      const typeMap = {
        'literature': 'primary',
        'technology': 'success',
        'history': 'warning',
        'philosophy': 'info',
        'art': 'danger'
      }
      return typeMap[category] || ''
    }

    const getCategoryLabel = (category) => {
      const item = categories.value.find(item => item.value === category)
      return item ? item.label : category
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      return dateStr.split('T')[0]
    }

    const getStockClass = (stock) => {
      if (stock <= 0) return 'out-of-stock'
      if (stock <= 5) return 'low-stock'
      return ''
    }

    // 方法
    const resetForm = () => {
      if (!formRef.value) return
      formRef.value.resetFields()
      Object.assign(form, {
        id: '',
        isbn: '',
        productName: '',
        author: '',
        publisher: '',
        publishDate: '',
        category: '',
        price: 0,
        discount: 1.0,
        stock: 0,
        description: '',
        status: 1
      })
    }

    const addToCart = (product) => {
      selectedProduct.value = product
      cartQuantity.value = 1
      cartDialogVisible.value = true
    }

    const confirmAddToCart = () => {
      if (!selectedProduct.value) return

      // 获取购物车数据
      let cart = JSON.parse(localStorage.getItem('shoppingCart') || '[]')

      // 检查是否已存在
      const existingItem = cart.find(item => item.productId === selectedProduct.value.id)

      if (existingItem) {
        // 更新数量
        existingItem.quantity += cartQuantity.value
        if (existingItem.quantity > selectedProduct.value.stock) {
          ElMessage.warning('超过库存数量')
          return
        }
      } else {
        // 添加新商品
        cart.push({
          productId: selectedProduct.value.id,
          productName: selectedProduct.value.productName,
          price: selectedProduct.value.price,
          discount: selectedProduct.value.discount || 1,
          quantity: cartQuantity.value,
          stock: selectedProduct.value.stock,
          image: selectedProduct.value.coverUrl
        })
      }

      // 保存到本地存储
      localStorage.setItem('shoppingCart', JSON.stringify(cart))

      ElMessage.success('已加入购物车')
      cartDialogVisible.value = false

      // 更新库存显示
      const productIndex = tableData.value.findIndex(p => p.id === selectedProduct.value.id)
      if (productIndex !== -1) {
        tableData.value[productIndex].stock -= cartQuantity.value
      }
    }

    const del = (id) => {
      proxy.$axios.post(proxy.$httpUrl + '/product/delete', { id }).then(res => res.data).then(res => {
        if (res.code == 200) {
          ElMessage.success('删除成功！')
          loadPost()
        } else {
          ElMessage.error(res.msg || '删除失败！')
        }
      })
    }

    const mod = (row) => {
      centerDialogVisible.value = true
      nextTick(() => {
        Object.assign(form, {
          id: row.id,
          isbn: row.isbn,
          productName: row.productName,
          author: row.author,
          publisher: row.publisher,
          publishDate: row.publishDate,
          category: row.category,
          price: row.price,
          discount: row.discount || 1.0,
          stock: row.stock,
          description: row.description || '',
          status: row.status
        })
      })
    }

    const add = () => {
      centerDialogVisible.value = true
      nextTick(() => {
        resetForm()
      })
    }

    const doSave = () => {
      proxy.$axios.post(proxy.$httpUrl + '/product/save', form).then(res => res.data).then(res => {
        if (res.code == 200) {
          ElMessage.success('保存成功！')
          centerDialogVisible.value = false
          loadPost()
          resetForm()
        } else {
          ElMessage.error(res.msg || '保存失败！')
        }
      })
    }

    const doMod = () => {
      proxy.$axios.post(proxy.$httpUrl + '/product/update', form).then(res => res.data).then(res => {
        if (res.code == 200) {
          ElMessage.success('更新成功！')
          centerDialogVisible.value = false
          loadPost()
          resetForm()
        } else {
          ElMessage.error(res.msg || '更新失败！')
        }
      })
    }

    const save = () => {
      if (!formRef.value) return

      formRef.value.validate((valid) => {
        if (valid) {
          if (form.id) {
            doMod()
          } else {
            doSave()
          }
        } else {
          ElMessage.warning('请检查表单填写是否正确')
          return false
        }
      })
    }

    const handleSizeChange = (val) => {
      pageNum.value = 1
      pageSize.value = val
      loadPost()
    }

    const handleCurrentChange = (val) => {
      pageNum.value = val
      loadPost()
    }

    const resetParam = () => {
      searchParam.productName = ''
      searchParam.author = ''
      searchParam.category = ''
    }

    const loadPost = () => {
      proxy.$axios.post(proxy.$httpUrl + '/product/listPage', {
        pageSize: pageSize.value,
        pageNum: pageNum.value,
        ...searchParam
      }).then(res => res.data).then(res => {
        if (res.code == 200) {
          tableData.value = res.data.map(item => ({
            ...item,
            discount: item.discount || 1.0
          }))
          total.value = res.total
        } else {
          ElMessage.error(res.msg || '获取数据失败')
        }
      })
    }

    // 生命周期
    onMounted(() => {
      loadPost()
    })

    return {
      tableData,
      pageSize,
      pageNum,
      total,
      searchParam,
      categories,
      centerDialogVisible,
      cartDialogVisible,
      form,
      rules,
      formRef,
      selectedProduct,
      cartQuantity,
      resetForm,
      addToCart,
      confirmAddToCart,
      del,
      mod,
      add,
      save,
      handleSizeChange,
      handleCurrentChange,
      resetParam,
      loadPost,
      getCategoryTagType,
      getCategoryLabel,
      formatDate,
      getStockClass,
      Search
    }
  }
}
</script>

<style scoped>
.out-of-stock {
  color: #f56c6c;
  font-weight: bold;
}

.low-stock {
  color: #e6a23c;
  font-weight: bold;
}

.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 12px;
  margin-right: 5px;
}

.discount-info {
  margin-top: 5px;
}

.el-table {
  margin-bottom: 20px;
}

.el-pagination {
  justify-content: center;
  margin-top: 20px;
}
</style>