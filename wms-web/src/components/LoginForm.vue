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
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: "LoginForm",
  setup() {
    const { proxy } = getCurrentInstance()
    const router = useRouter()
    const store = useStore()

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
        { required: true, message: '请输密码', trigger: 'blur' }
      ]
    })

    const confirm = () => {
      confirm_disabled.value = true
      loginFormRef.value.validate((valid) => {
        if (valid) { // valid成功为true，失败为false
          // 去后台验证用户名密码
          proxy.$axios.post(proxy.$httpUrl + '/user/login', loginForm).then(res => res.data).then(res => {
            console.log(res)
            if (res.code == 200) {
              // 存储
              sessionStorage.setItem("CurUser", JSON.stringify(res.data.user))

              console.log(res.data.menu)
              store.commit("setMenu", res.data.menu)
              // 跳转到主页
              router.replace('/Index')
            } else {
              confirm_disabled.value = false
              ElMessage.error('校验失败，用户名或密码错误！')
              return false
            }
          }).catch(error => {
            confirm_disabled.value = false
            ElMessage.error('登录请求失败，请检查网络连接')
            console.error('登录请求失败:', error)
          })
        } else {
          confirm_disabled.value = false
          console.log('校验失败')
          return false
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