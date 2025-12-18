<template>
  <div class="register">
    <div class="register-container">
      <h2>用户注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>账号：</label>
          <input v-model="form.no" type="text" required placeholder="请输入账号">
        </div>
        <div class="form-group">
          <label>密码：</label>
          <input v-model="form.password" type="password" required placeholder="请输入密码">
        </div>
        <div class="form-group">
          <label>姓名：</label>
          <input v-model="form.name" type="text" required placeholder="请输入姓名">
        </div>
        <button type="submit" :disabled="loading" class="register-button">
          {{ loading ? '注册中...' : '注册' }}
        </button>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'RegisterPage',  // 修改这里：改为多单词
  data() {
    return {
      form: {
        no: '',
        password: '',
        name: ''
      },
      loading: false
    }
  },
  methods: {
    async handleRegister() {
      if (!this.form.no || !this.form.password || !this.form.name) {
        alert('请填写完整信息')
        return
      }

      this.loading = true
      try {
        const response = await axios.post('http://localhost:8090/user/register', this.form)

        if (response.data.code === 200) {
          alert('注册成功')
          this.$router.push('/login')
        } else {
          alert(response.data.msg || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        alert('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-container {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.register-button {
  width: 100%;
  padding: 12px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.register-button:hover {
  background: #73d13d;
}

.register-button:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

.login-link {
  margin-top: 20px;
  text-align: center;
  color: #666;
}

.login-link a {
  color: #1890ff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>