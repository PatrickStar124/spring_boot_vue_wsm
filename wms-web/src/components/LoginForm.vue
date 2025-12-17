<template>
  <div class="loginBody">
    <div class="loginDiv">
      <div class="login-content">
        <h1 class="login-title">用户登录</h1>
        <el-form :model="loginForm" label-width="100px"
                 :rules="rules" ref="loginForm">
          <el-form-item label="账号" prop="no">
            <el-input style="width: 200px" type="text" v-model="loginForm.no"
                      autocomplete="off" size="small"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <!-- 移除 .native 修饰符 -->
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
export default {
  name: "LoginForm", // 组件名改为多单词 LoginForm
  data(){
    return{
      confirm_disabled:false,
      loginForm:{
        no:'',
        password:''
      },
      rules:{
        no: [
          { required: true, message: '请输入账号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输密码', trigger: 'blur' }
        ],
      }
    }
  },
  methods:{
    confirm(){
      this.confirm_disabled=true;
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // 调用登录接口（注意：Vue 3 中全局变量通过 app.config.globalProperties 定义，需确保已正确配置 $axios 和 $httpUrl）
          this.$axios.post(this.$httpUrl+'/user/login', this.loginForm)
              .then(res => res.data)
              .then(res => {
                console.log(res);
                if(res.code === 200){
                  // 存储用户信息
                  sessionStorage.setItem("CurUser", JSON.stringify(res.data.user));
                  console.log(res.data.menu);
                  this.$store.commit("setMenu", res.data.menu);
                  // 跳转到主页（确保路由中已定义 '/Index' 路径）
                  this.$router.replace('/Index');
                } else {
                  this.confirm_disabled = false;
                  alert('校验失败，用户名或密码错误！');
                  return false;
                }
              })
              .catch(error => {
                // 捕获请求异常（如网络错误）
                this.confirm_disabled = false;
                console.error('登录请求失败：', error);
                alert('登录失败，请稍后重试');
              });
        } else {
          this.confirm_disabled = false;
          console.log('校验失败');
          return false;
        }
      });
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