<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getForumPost, addReply, deleteReply, deletePost, updatePost } from '../api/forum'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const replies = ref([])
const loading = ref(true)
const replyContent = ref('')
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const editMode = ref(false)
const editForm = ref({ title: '', content: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getForumPost(route.params.id)
    post.value = res.data.post
    replies.value = res.data.replies
  } catch {} finally { loading.value = false }
}

const handleReply = async () => {
  if (!replyContent.value.trim()) { ElMessage.warning('Reply cannot be empty'); return }
  await addReply(post.value.id, { content: replyContent.value })
  replyContent.value = ''
  ElMessage.success('Reply added!')
  loadData()
}

const handleDeleteReply = async (id) => {
  await ElMessageBox.confirm('Delete this reply?', 'Confirm')
  await deleteReply(id)
  ElMessage.success('Reply deleted')
  loadData()
}

const handleDeletePost = async () => {
  await ElMessageBox.confirm('Delete this entire post and all replies?', 'Confirm', { type: 'warning' })
  await deletePost(post.value.id)
  ElMessage.success('Post deleted')
  router.push('/forum')
}

const startEdit = () => {
  editForm.value = { title: post.value.title, content: post.value.content }
  editMode.value = true
}

const saveEdit = async () => {
  await updatePost(post.value.id, editForm.value)
  ElMessage.success('Updated')
  editMode.value = false
  loadData()
}

const formatTime = (dateStr) => {
  return new Date(dateStr).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: 'numeric', minute: '2-digit'
  })
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div v-if="post" class="post-detail">
      <el-button text @click="router.push('/forum')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> Back to Forum
      </el-button>

      <div v-if="!editMode" class="post-content-card">
        <div class="post-header">
          <div class="author-avatar">{{ (post.authorName || 'U').charAt(0) }}</div>
          <div class="author-info">
            <span class="author-name">{{ post.authorName }}</span>
            <span class="post-time">{{ formatTime(post.createdAt) }}</span>
          </div>
          <div class="post-actions" v-if="currentUser.id === post.userId">
            <el-button size="small" text @click="startEdit">Edit</el-button>
            <el-button size="small" text type="danger" @click="handleDeletePost">Delete</el-button>
          </div>
        </div>
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-body-text" v-html="post.content?.replace(/\n/g, '<br>')"></div>
      </div>

      <div v-else class="post-content-card edit-card">
        <h3 style="margin: 0 0 16px;">Edit Post</h3>
        <el-input v-model="editForm.title" placeholder="Title" style="margin-bottom: 12px" />
        <el-input v-model="editForm.content" type="textarea" :rows="5" placeholder="Content" />
        <div style="margin-top: 16px; text-align: right">
          <el-button @click="editMode = false">Cancel</el-button>
          <el-button type="primary" @click="saveEdit">Save</el-button>
        </div>
      </div>

      <div class="replies-section">
        <h3 class="replies-heading">
          <el-icon><ChatLineSquare /></el-icon>
          {{ replies.length }} {{ replies.length === 1 ? 'Reply' : 'Replies' }}
        </h3>

        <div v-for="reply in replies" :key="reply.id" class="reply-card">
          <div class="reply-avatar">{{ (reply.authorName || 'U').charAt(0) }}</div>
          <div class="reply-body">
            <div class="reply-header">
              <span class="reply-author">{{ reply.authorName }}</span>
              <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
              <el-button v-if="currentUser.id === reply.userId" size="small" text type="danger"
                         @click="handleDeleteReply(reply.id)" class="delete-reply">Delete</el-button>
            </div>
            <div class="reply-text" v-html="reply.content?.replace(/\n/g, '<br>')"></div>
          </div>
        </div>
        <div v-if="!replies.length" class="no-replies">No replies yet. Be the first!</div>
      </div>

      <div class="reply-input-card">
        <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="Write a reply..." />
        <div style="margin-top: 10px; text-align: right">
          <el-button type="primary" @click="handleReply" :disabled="!replyContent.trim()">
            <el-icon><Promotion /></el-icon> Reply
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container { max-width: 780px; margin: 0 auto; }
.back-btn { margin-bottom: 12px; font-size: 14px; color: #6366f1; padding: 0; }
.post-content-card {
  background: #fff; border-radius: 16px; padding: 28px 32px; margin-bottom: 20px;
  border: 1px solid #f0f0f0;
}
.post-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.author-avatar {
  width: 40px; height: 40px; border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #8b5cf6); color: #fff;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 15px;
}
.author-info { display: flex; flex-direction: column; flex: 1; }
.author-name { font-weight: 600; font-size: 14px; color: #1e1b4b; }
.post-time { font-size: 12px; color: #9ca3af; }
.post-title { margin: 0 0 12px; font-size: 22px; font-weight: 700; color: #1e1b4b; }
.post-body-text { font-size: 15px; line-height: 1.7; color: #374151; }
.replies-section { margin-bottom: 20px; }
.replies-heading { display: flex; align-items: center; gap: 6px; font-size: 16px; color: #1e1b4b; margin-bottom: 14px; }
.reply-card {
  background: #fff; border: 1px solid #f0f0f0; border-radius: 12px; padding: 16px 20px;
  display: flex; gap: 12px; margin-bottom: 10px;
}
.reply-avatar {
  width: 32px; height: 32px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, #10b981, #34d399); color: #fff;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 13px;
}
.reply-body { flex: 1; }
.reply-header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.reply-author { font-weight: 600; font-size: 13px; color: #1e1b4b; }
.reply-time { font-size: 12px; color: #9ca3af; flex: 1; }
.reply-text { font-size: 14px; line-height: 1.6; color: #374151; }
.delete-reply { padding: 0; }
.no-replies { text-align: center; color: #ccc; padding: 30px 0; font-size: 14px; }
.reply-input-card {
  background: #fff; border: 1px solid #f0f0f0; border-radius: 14px; padding: 20px;
}
</style>
