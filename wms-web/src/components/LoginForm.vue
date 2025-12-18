<template>
  <div class="loginBody">
    <div class="loginDiv">
      <div class="login-content">
        <h1 class="login-title">图书管理系统</h1>

        <!-- 登录表单 -->
        <el-form v-show="activeTab === 'login'"
                 :model="loginForm"
                 label-width="80px"
                 :rules="loginRules"
                 ref="loginFormRef">
          <el-form-item label="账号" prop="no">
            <el-input
                style="width: 200px"
                type="text"
                v-model="loginForm.no"
                autocomplete="off"
                size="small"
                placeholder="请输入账号">
            </el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
                style="width: 200px"
                type="password"
                v-model="loginForm.password"
                show-password
                autocomplete="off"
                size="small"
                placeholder="请输入密码"
                @keyup.enter="handleLogin">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLogin" :disabled="loginLoading">
              登录
            </el-button>
            <el-button type="text" @click="activeTab = 'register'" style="margin-left: 20px;">
              立即注册
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 注册表单 -->
        <el-form v-show="activeTab === 'register'"
                 :model="registerForm"
                 label-width="100px"
                 :rules="registerRules"
                 ref="registerFormRef">
          <h3 class="register-title">用户注册</h3>

          <el-form-item label="账号" prop="no">
            <el-input
                style="width: 200px"
                type="text"
                v-model="registerForm.no"
                autocomplete="off"
                size="small"
                placeholder="请输入账号">
            </el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
                style="width: 200px"
                type="password"
                v-model="registerForm.password"
                show-password
                autocomplete="off"
                size="small"
                placeholder="请输入密码">
            </el-input>
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
                style="width: 200px"
                type="password"
                v-model="registerForm.confirmPassword"
                show-password
                autocomplete="off"
                size="small"
                placeholder="请再次输入密码">
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleRegister" :disabled="registerLoading">
              注册
            </el-button>
            <el-button type="text" @click="activeTab = 'login'" style="margin-left: 20px;">
              返回登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: "LoginForm",
  setup() {
    const { proxy } = getCurrentInstance()
    const router = useRouter()
    const store = useStore()

    const loginLoading = ref(false)
    const registerLoading = ref(false)
    const activeTab = ref('login')
    const loginFormRef = ref()
    const registerFormRef = ref()

    // 登录表单数据
    const loginForm = reactive({
      no: '',
      password: ''
    })

    // 注册表单数据
    const registerForm = reactive({
      no: '',
      password: '',
      confirmPassword: ''
    })

    // 登录验证规则
    const loginRules = reactive({
      no: [
        { required: true, message: '请输入账号', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    })

    // 注册验证规则
    const registerRules = reactive({
      no: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 3, max: 20, message: '账号长度在3到20个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== registerForm.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    })

    // 登录处理
    const handleLogin = () => {
      loginLoading.value = true

      loginFormRef.value.validate((valid) => {
        if (valid) {
          // 去后台验证用户名密码
          proxy.$axios.post(proxy.$httpUrl + '/user/login', loginForm).then(res => res.data).then(res => {
            console.log(res)
            if (res.code == 200) {
              // 存储用户信息
              sessionStorage.setItem("CurUser", JSON.stringify(res.data.user))

              console.log(res.data.menu)
              store.commit("setMenu", res.data.menu)
              // 跳转到主页
              router.replace('/Home')
            } else {
              loginLoading.value = false
              ElMessage.error(res.msg || '用户名或密码错误！')
              return false
            }
          }).catch(error => {
            loginLoading.value = false
            ElMessage.error('登录请求失败，请检查网络连接')
            console.error('登录请求失败:', error)
          })
        } else {
          loginLoading.value = false
          console.log('登录校验失败')
          return false
        }
      })
    }

    // 注册处理
    const handleRegister = () => {
      registerLoading.value = true

      registerFormRef.value.validate(async (valid) => {
        if (!valid) {
          registerLoading.value = false
          return false
        }

        try {
          // 先检查账号是否已存在
          const checkRes = await proxy.$axios.get(proxy.$httpUrl + "/user/findByNo?no=" + registerForm.no)

          if (checkRes.data.code === 200) {
            ElMessage.warning('该账号已存在，请使用其他账号')
            registerLoading.value = false
            return
          }

          // 准备注册数据
          const registerData = {
            no: registerForm.no,
            password: registerForm.password,
            name: registerForm.no, // 默认用户名为账号
            sex: 1, // 默认性别为男
            age: 20, // 默认年龄
            phone: '', // 默认为空
            roleId: 2 // 默认注册为读者
          }

          // 调用注册接口
          const registerRes = await proxy.$axios.post(proxy.$httpUrl + '/user/register', registerData)

          if (registerRes.data.code === 200) {
            ElMessage.success('注册成功！')

            // 注册成功后自动填充登录表单并切换到登录界面
            loginForm.no = registerForm.no
            loginForm.password = ''
            activeTab.value = 'login'

            // 清空注册表单
            registerFormRef.value.resetFields()
            Object.assign(registerForm, {
              no: '',
              password: '',
              confirmPassword: ''
            })

            ElMessage.success('请使用注册的账号登录')
          } else {
            ElMessage.error(registerRes.data.msg || '注册失败')
          }
        } catch (error) {
          console.error('注册请求失败:', error)
          ElMessage.error('注册请求失败，请检查网络连接')
        } finally {
          registerLoading.value = false
        }
      })
    }

    return {
      loginLoading,
      registerLoading,
      activeTab,
      loginForm,
      registerForm,
      loginRules,
      registerRules,
      loginFormRef,
      registerFormRef,
      handleLogin,
      handleRegister
    }
  }
}
</script>

<style scoped>
.loginBody {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.loginDiv {
  width: 450px;
  height: auto;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.login-content {
  padding: 40px 30px;
}

.login-title {
  margin: 0 0 30px 0;
  text-align: center;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.register-title {
  margin: 0 0 25px 0;
  text-align: center;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item:last-child {
  margin-bottom: 0;
}

.el-button--primary {
  width: 120px;
}

.el-button--text {
  color: #409eff;
}

.el-button--text:hover {
  color: #66b1ff;
}

/* 响应式调整 */
@media (max-width: 480px) {
  .loginDiv {
    width: 90%;
    margin: 0 5%;
  }

  .login-content {
    padding: 30px 20px;
  }

  .login-title {
    font-size: 24px;
  }
}
</style>