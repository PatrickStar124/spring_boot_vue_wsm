<template>
  <div class="main-header">
    <el-dropdown trigger="click" @command="handleCommand">
      <span class="user-name">
        王小虎
        <i class="el-icon-arrow-down" style="margin-left: 5px"></i>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">个人中心</el-dropdown-item>
          <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  name: "MainHeader",
  methods: {
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$message('点击了个人中心');
          // 这里可以跳转到个人中心页面
          break;
        case 'logout':
          this.$confirm('确定要退出登录吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.$message({
              type: 'success',
              message: '退出成功!'
            });
            // 这里执行退出登录的逻辑
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消退出'
            });
          });
          break;
      }
    }
  }
}
</script>

<style scoped>
.main-header {
  padding: 20px;
}

.user-name {
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}

.user-name:hover {
  color: #409EFF;
}
</style>