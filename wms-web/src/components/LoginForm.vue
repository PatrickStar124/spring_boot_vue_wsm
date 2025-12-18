<template>
  <div class="loginBody">
    <div class="loginDiv">
      <div class="login-content">
        <h1 class="login-title">用户登录</h1>
        <el-form :model="loginForm" label-width="100px"
                 :rules="rules" ref="loginFormRef">
          <el-form-item label="账号" prop="no">
            <el-input style="width: 200px" type="text" v-model="loginForm.no"
                      autocomplete="off" size="small"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input style="width: 200px" type="password" v-model="loginForm.password"
                      show-password autocomplete="off" size="small" @keyup.enter="confirm"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="confirm" :disabled="confirm_disabled">确 定</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: "LoginForm",
  setup() {
    const { proxy } = getCurrentInstance()
    const router = useRouter()

    const confirm_disabled = ref(false)
    const loginFormRef = ref()

    const loginForm = reactive({
      no: '',
      password: ''
    })

    const rules = reactive({
      no: [
        { required: true, message: '请输入账号', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    })

    const confirm = () => {
      confirm_disabled.value = true
      loginFormRef.value.validate((valid) => {
        if (valid) {
          // 调用后端登录接口（使用全局注册的axios和基础地址）
          proxy.$axios.post(proxy.$httpUrl + '/user/login', loginForm)
              .then(res => res.data)
              .then(res => {
                console.log("后端返回数据：", res)
                if (res.code === 200) {
                  // 存储用户信息到本地会话
                  sessionStorage.setItem("CurUser", JSON.stringify(res.data))
                  // 跳转到主页（路由地址与你的配置一致）
                  router.replace('/Index')
                  ElMessage.success('登录成功！')
                } else {
                  confirm_disabled.value = false
                  ElMessage.error('用户名或密码错误！')
                }
              })
              .catch(error => {
                confirm_disabled.value = false
                ElMessage.error('登录失败：' + error.message)
                console.error('登录请求异常：', error)
              })
        } else {
          confirm_disabled.value = false
          ElMessage.warning('请填写完整账号密码！')
        }
      })
    }

    return {
      confirm_disabled,
      loginForm,
      rules,
      loginFormRef,
      confirm
    }
  }
}
</script>

<style scoped>
.loginBody {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: #B3C0D1;
}
.loginDiv {
  position: absolute;
  top: 50%;
  left: 50%;
  margin-top: -200px;
  margin-left: -250px;
  width: 450px;
  height: 330px;
  background: #fff;
  border-radius: 5%;
}
.login-title {
  margin: 20px 0;
  text-align: center;
}
.login-content {
  width: 400px;
  height: 250px;
  position: absolute;
  top: 25px;
  left: 25px;
}
</style>