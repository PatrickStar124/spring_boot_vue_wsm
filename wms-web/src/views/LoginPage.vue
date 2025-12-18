<template>
  <div class="login">
    <div class="login-container">
      <h2>用户登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>账号：</label>
          <input v-model="form.no" type="text" required placeholder="请输入账号">
        </div>
        <div class="form-group">
          <label>密码：</label>
          <input v-model="form.password" type="password" required placeholder="请输入密码">
        </div>
        <button type="submit" :disabled="loading" class="login-button">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <div class="register-link">
          没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        no: '',
        password: ''
      },
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      if (!this.form.no || !this.form.password) {
        alert('请填写完整信息')
        return
      }

      this.loading = true
      try {
        const response = await axios.post('http://localhost:8090/user/login', this.form)

        if (response.data.code === 200) {
          const user = response.data.data
          localStorage.setItem('user', JSON.stringify(user))
          alert('登录成功')
          this.$router.push('/')
        } else {
          alert(response.data.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        alert('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
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

.login-button {
  width: 100%;
  padding: 12px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.login-button:hover {
  background: #40a9ff;
}

.login-button:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

.register-link {
  margin-top: 20px;
  text-align: center;
  color: #666;
}

.register-link a {
  color: #1890ff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>