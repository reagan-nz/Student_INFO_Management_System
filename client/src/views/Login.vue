<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'

const router = useRouter()

const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)
const visible = ref(false)

onMounted(() => {
  setTimeout(() => { visible.value = true }, 100)
})

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('Please enter username and password')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    localStorage.setItem('user', JSON.stringify(res.data))
    ElMessage.success('Login successful')
    router.push('/dashboard')
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- Animated background blobs -->
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>
    <div class="bg-blob blob-3"></div>

    <div class="login-card" :class="{ visible }">
      <div class="login-header">
        <div class="login-logo">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h2>StudentSys</h2>
        <p>Student Information Management System</p>
      </div>

      <el-form :model="form" @keyup.enter="handleLogin" class="login-form">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="Username"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="Password"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            Sign In
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span class="divider-text">Demo Accounts</span>
        <div class="demo-accounts">
          <code>admin / admin</code>
          <code>teacher1 / 123456</code>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f0b2e;
  position: relative;
  overflow: hidden;
}

.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 8s ease-in-out infinite;
}
.blob-1 {
  width: 400px;
  height: 400px;
  background: #6366f1;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}
.blob-2 {
  width: 350px;
  height: 350px;
  background: #8b5cf6;
  bottom: -80px;
  right: -80px;
  animation-delay: -3s;
}
.blob-3 {
  width: 250px;
  height: 250px;
  background: #a78bfa;
  top: 50%;
  left: 60%;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.login-card {
  width: 420px;
  padding: 44px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
  opacity: 0;
  transform: translateY(20px) scale(0.97);
  transition: all 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
.login-card.visible {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-logo {
  width: 52px;
  height: 52px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.login-logo svg {
  width: 28px;
  height: 28px;
}
.login-header h2 {
  margin: 0 0 6px;
  font-size: 24px;
  font-weight: 700;
  color: #1e1b4b;
}
.login-header p {
  margin: 0;
  font-size: 13px;
  color: #9ca3af;
}

.login-form {
  margin-bottom: 8px;
}
.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
}
.login-btn {
  width: 100%;
  border-radius: 10px;
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 0.3px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  height: 44px;
}
.login-btn:hover {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
}

.login-footer {
  text-align: center;
}
.divider-text {
  font-size: 12px;
  color: #b0b0b0;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.demo-accounts {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 10px;
}
.demo-accounts code {
  font-size: 12px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 4px 10px;
  border-radius: 6px;
}
</style>
