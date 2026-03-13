<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getConversations, getMessages, sendMessage, markAsRead, getUnreadCount, getAllUsers } from '../api/message'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const conversations = ref([])
const messages = ref([])
const selectedUser = ref(null)
const newContent = ref('')
const loading = ref(false)
const chatLoading = ref(false)
const newMsgDialog = ref(false)
const allUsers = ref([])
const selectedNewUser = ref(null)
const msgContainer = ref(null)

const loadConversations = async () => {
  loading.value = true
  try {
    const res = await getConversations()
    conversations.value = res.data
  } catch {} finally { loading.value = false }
}

const selectConversation = async (conv) => {
  selectedUser.value = { id: conv.userId, nickname: conv.nickname }
  await loadMessages(conv.userId)
}

const loadMessages = async (otherUserId) => {
  chatLoading.value = true
  try {
    const res = await getMessages(otherUserId)
    messages.value = res.data
    await nextTick()
    scrollToBottom()
    loadConversations()
  } catch {} finally { chatLoading.value = false }
}

const handleSend = async () => {
  if (!newContent.value.trim()) return
  await sendMessage({ receiverId: selectedUser.value.id, content: newContent.value })
  newContent.value = ''
  await loadMessages(selectedUser.value.id)
}

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}

const scrollToBottom = () => {
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

const openNewMsgDialog = async () => {
  const res = await getAllUsers()
  allUsers.value = res.data.filter(u => u.id !== currentUser.value.id)
  selectedNewUser.value = null
  newMsgDialog.value = true
}

const startNewConversation = () => {
  if (!selectedNewUser.value) return
  const u = allUsers.value.find(a => a.id === selectedNewUser.value)
  selectedUser.value = { id: u.id, nickname: u.nickname }
  messages.value = []
  newMsgDialog.value = false
}

const formatTime = (dateStr) => {
  const d = new Date(dateStr)
  const now = new Date()
  const sameDay = d.toDateString() === now.toDateString()
  if (sameDay) return d.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' })
  return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) + ' ' +
         d.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' })
}

onMounted(() => loadConversations())
</script>

<template>
  <div class="msg-page">
    <!-- Left sidebar -->
    <div class="sidebar-panel">
      <div class="sidebar-header">
        <h3>Messages</h3>
        <el-button size="small" type="primary" circle @click="openNewMsgDialog">
          <el-icon><Edit /></el-icon>
        </el-button>
      </div>
      <div v-loading="loading" class="conversation-list">
        <div v-for="conv in conversations" :key="conv.userId"
             class="conv-item" :class="{ active: selectedUser?.id === conv.userId }"
             @click="selectConversation(conv)">
          <div class="conv-avatar">{{ (conv.nickname || 'U').charAt(0) }}</div>
          <div class="conv-body">
            <div class="conv-top">
              <span class="conv-name">{{ conv.nickname }}</span>
              <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
            </div>
            <p class="conv-preview">{{ conv.lastMessage }}</p>
          </div>
          <div v-if="conv.unreadCount > 0" class="unread-dot">{{ conv.unreadCount }}</div>
        </div>
        <div v-if="!loading && !conversations.length" class="empty-sidebar">No conversations yet</div>
      </div>
    </div>

    <!-- Right chat area -->
    <div class="chat-panel">
      <template v-if="selectedUser">
        <div class="chat-header">
          <div class="chat-header-avatar">{{ (selectedUser.nickname || 'U').charAt(0) }}</div>
          <span class="chat-header-name">{{ selectedUser.nickname }}</span>
        </div>
        <div ref="msgContainer" class="chat-messages" v-loading="chatLoading">
          <div v-for="msg in messages" :key="msg.id"
               class="msg-row" :class="{ sent: msg.senderId === currentUser.id }">
            <div class="msg-bubble">
              <p class="msg-text">{{ msg.content }}</p>
              <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
            </div>
          </div>
          <div v-if="!chatLoading && !messages.length" class="empty-chat">
            No messages yet. Say hi!
          </div>
        </div>
        <div class="chat-input">
          <el-input v-model="newContent" type="textarea" :rows="2"
                    placeholder="Type a message..." resize="none"
                    @keydown="handleKeydown" />
          <el-button type="primary" @click="handleSend" :disabled="!newContent.trim()">
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-else class="no-selection">
        <el-icon :size="48" color="#d1d5db"><ChatDotRound /></el-icon>
        <p>Select a conversation or start a new one</p>
      </div>
    </div>

    <!-- New message dialog -->
    <el-dialog v-model="newMsgDialog" title="New Message" width="400px" destroy-on-close>
      <el-select v-model="selectedNewUser" placeholder="Select a user" style="width: 100%" filterable>
        <el-option v-for="u in allUsers" :key="u.id" :label="u.nickname" :value="u.id" />
      </el-select>
      <template #footer>
        <el-button @click="newMsgDialog = false">Cancel</el-button>
        <el-button type="primary" @click="startNewConversation" :disabled="!selectedNewUser">Start Chat</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.msg-page { display: flex; height: calc(100vh - 120px); gap: 0; border-radius: 16px; overflow: hidden; border: 1px solid #f0f0f0; background: #fff; }

/* Sidebar */
.sidebar-panel { width: 300px; border-right: 1px solid #f0f0f0; display: flex; flex-direction: column; flex-shrink: 0; background: #fafbfc; }
.sidebar-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 18px; border-bottom: 1px solid #f0f0f0; }
.sidebar-header h3 { margin: 0; font-size: 17px; color: #1e1b4b; }
.conversation-list { flex: 1; overflow-y: auto; }
.conv-item { display: flex; align-items: center; gap: 10px; padding: 12px 16px; cursor: pointer; transition: background 0.15s; position: relative; }
.conv-item:hover { background: #f1f0ff; }
.conv-item.active { background: #ede9fe; }
.conv-avatar {
  width: 38px; height: 38px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, #6366f1, #8b5cf6); color: #fff;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px;
}
.conv-body { flex: 1; min-width: 0; }
.conv-top { display: flex; justify-content: space-between; align-items: center; }
.conv-name { font-weight: 600; font-size: 13px; color: #1e1b4b; }
.conv-time { font-size: 11px; color: #9ca3af; }
.conv-preview { margin: 2px 0 0; font-size: 12px; color: #9ca3af; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.unread-dot {
  background: #ef4444; color: #fff; border-radius: 20px; min-width: 20px; height: 20px;
  display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; padding: 0 5px;
}
.empty-sidebar { text-align: center; color: #ccc; padding: 40px 0; font-size: 13px; }

/* Chat area */
.chat-panel { flex: 1; display: flex; flex-direction: column; }
.chat-header {
  display: flex; align-items: center; gap: 10px; padding: 14px 20px; border-bottom: 1px solid #f0f0f0;
}
.chat-header-avatar {
  width: 34px; height: 34px; border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #8b5cf6); color: #fff;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 13px;
}
.chat-header-name { font-weight: 600; font-size: 15px; color: #1e1b4b; }
.chat-messages { flex: 1; overflow-y: auto; padding: 20px; display: flex; flex-direction: column; gap: 8px; }
.msg-row { display: flex; }
.msg-row.sent { justify-content: flex-end; }
.msg-bubble { max-width: 65%; padding: 10px 14px; border-radius: 16px; position: relative; }
.msg-row:not(.sent) .msg-bubble { background: #f3f4f6; border-bottom-left-radius: 4px; }
.msg-row.sent .msg-bubble { background: #6366f1; color: #fff; border-bottom-right-radius: 4px; }
.msg-text { margin: 0; font-size: 14px; line-height: 1.5; white-space: pre-wrap; }
.msg-time { font-size: 10px; opacity: 0.6; margin-top: 4px; display: block; }
.msg-row.sent .msg-time { text-align: right; }
.empty-chat { text-align: center; color: #ccc; padding: 40px 0; font-size: 14px; margin: auto; }
.chat-input { display: flex; gap: 8px; padding: 12px 16px; border-top: 1px solid #f0f0f0; align-items: flex-end; }
.chat-input .el-input { flex: 1; }
.no-selection { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #9ca3af; gap: 12px; }
.no-selection p { margin: 0; font-size: 14px; }
</style>
