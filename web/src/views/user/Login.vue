<template>
  <div class="login-page">
    <div class="login-card">
      <h2>公考智学助手</h2>
      <p class="subtitle">登录后开始学习</p>
      <el-form @submit.prevent="handleLogin" class="login-form">
        <el-form-item>
          <el-input v-model="phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="code" placeholder="请输入验证码" maxlength="6" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const phone = ref('')
const code = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!phone.value || !code.value) {
    ElMessage.warning('请填写手机号和验证码')
    return
  }
  loading.value = true
  try {
    await userStore.login(phone.value, code.value)
    ElMessage.success('登录成功')
    router.push('/plan')
  } catch (e) {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
}
.login-card h2 {
  font-size: 24px;
  margin-bottom: 8px;
  color: #303133;
}
.subtitle {
  color: #909399;
  margin-bottom: 30px;
  font-size: 14px;
}
.login-form {
  text-align: left;
}
</style>
