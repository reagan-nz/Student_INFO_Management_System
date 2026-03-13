<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '../api/user'
import { getUnreadCount } from '../api/message'

const router = useRouter()
const route = useRoute()

const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const activeMenu = computed(() => {
  if (route.path.startsWith('/forum')) return '/forum'
  return route.path
})
const isCollapsed = ref(false)
const unreadCount = ref(0)
let unreadTimer = null

const fetchUnread = async () => {
  try { const res = await getUnreadCount(); unreadCount.value = res.data } catch {}
}

onMounted(() => {
  fetchUnread()
  unreadTimer = setInterval(fetchUnread, 15000)
})
onUnmounted(() => { if (unreadTimer) clearInterval(unreadTimer) })

const handleLogout = async () => {
  try {
    await logout()
  } catch {}
  localStorage.removeItem('user')
  ElMessage.success('Logged out')
  router.push('/login')
}
</script>

<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="layout-aside">
      <div class="logo" @click="router.push('/dashboard')">
        <svg viewBox="0 0 24 24" fill="none" class="logo-icon">
          <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <transition name="fade">
          <span v-if="!isCollapsed" class="logo-text">StudentSys</span>
        </transition>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        router
        background-color="transparent"
        text-color="rgba(255,255,255,0.65)"
        active-text-color="#fff"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>Dashboard</template>
        </el-menu-item>
        <el-menu-item index="/students">
          <el-icon><Notebook /></el-icon>
          <template #title>Students</template>
        </el-menu-item>
        <el-menu-item index="/classes">
          <el-icon><School /></el-icon>
          <template #title>Classes</template>
        </el-menu-item>
        <el-menu-item index="/subjects">
          <el-icon><Reading /></el-icon>
          <template #title>Subjects</template>
        </el-menu-item>
        <el-menu-item index="/grades">
          <el-icon><TrophyBase /></el-icon>
          <template #title>Grades</template>
        </el-menu-item>
        <el-menu-item index="/forum">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>Forum</template>
        </el-menu-item>
        <el-menu-item index="/messages" @click="fetchUnread">
          <el-icon><Message /></el-icon>
          <template #title>
            <span>Messages</span>
            <el-badge v-if="unreadCount > 0" :value="unreadCount" class="msg-badge" />
          </template>
        </el-menu-item>
        <el-menu-item index="/schedule">
          <el-icon><Calendar /></el-icon>
          <template #title>Schedule</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-bottom">
        <el-button
          text
          class="collapse-btn"
          @click="isCollapsed = !isCollapsed"
        >
          <el-icon :size="18">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </el-button>
      </div>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <span class="header-title">{{ route.meta.title }}</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-avatar-wrapper">
              <div class="user-avatar">
                {{ (user.nickname || user.username || 'U').charAt(0).toUpperCase() }}
              </div>
              <span class="user-name">{{ user.nickname || user.username }}</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  Logout
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.layout-aside {
  background: linear-gradient(180deg, #1e1b4b 0%, #312e81 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  padding: 0 16px;
  flex-shrink: 0;
}
.logo-icon {
  width: 28px;
  height: 28px;
  color: #a78bfa;
  flex-shrink: 0;
}
.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none !important;
  flex: 1;
  padding: 8px;
}
.sidebar-menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 4px;
  height: 44px;
  line-height: 44px;
}
.sidebar-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.12) !important;
}
.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

.sidebar-bottom {
  padding: 12px;
  display: flex;
  justify-content: center;
  flex-shrink: 0;
}
.collapse-btn {
  color: rgba(255,255,255,0.4) !important;
}
.collapse-btn:hover {
  color: rgba(255,255,255,0.8) !important;
}

.layout-header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  height: 64px;
}
.header-title {
  font-size: 17px;
  font-weight: 600;
  color: #1e1b4b;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 10px;
  transition: background 0.2s;
}
.user-avatar-wrapper:hover {
  background: #f5f3ff;
}
.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
}
.user-name {
  color: #374151;
  font-size: 14px;
  font-weight: 500;
}
.arrow {
  color: #9ca3af;
  font-size: 12px;
}

.layout-main {
  background: #f8f9fc;
  padding: 24px;
}

/* Page transition */
.page-enter-active,
.page-leave-active {
  transition: all 0.25s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.msg-badge {
  margin-left: 8px;
}
.msg-badge :deep(.el-badge__content) {
  font-size: 10px;
}
</style>
