<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getForumPosts, createPost, deletePost } from '../api/forum'

const router = useRouter()
const posts = ref([])
const total = ref(0)
const loading = ref(false)
const query = ref({ search: '', pageNum: 1, pageSize: 10 })
const dialogVisible = ref(false)
const form = ref({ title: '', content: '' })
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getForumPosts(query.value)
    posts.value = res.data.rows
    total.value = res.data.total
  } catch {} finally { loading.value = false }
}

const handleSearch = () => { query.value.pageNum = 1; loadData() }

const handleNewPost = () => {
  form.value = { title: '', content: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.title.trim()) { ElMessage.warning('Title is required'); return }
  await createPost(form.value)
  ElMessage.success('Post created!')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id) => {
  await deletePost(id)
  ElMessage.success('Deleted')
  loadData()
}

const timeAgo = (dateStr) => {
  const d = new Date(dateStr)
  const now = new Date()
  const diff = Math.floor((now - d) / 1000)
  if (diff < 60) return 'just now'
  if (diff < 3600) return Math.floor(diff / 60) + 'm ago'
  if (diff < 86400) return Math.floor(diff / 3600) + 'h ago'
  if (diff < 2592000) return Math.floor(diff / 86400) + 'd ago'
  return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <div class="filter-bar">
      <el-input v-model="query.search" placeholder="Search discussions..." clearable
                prefix-icon="Search" style="width: 320px" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleNewPost">
        <el-icon><EditPen /></el-icon> New Post
      </el-button>
    </div>

    <div v-loading="loading" class="post-list">
      <div v-for="post in posts" :key="post.id" class="post-card" @click="router.push(`/forum/${post.id}`)">
        <div class="post-avatar">{{ (post.authorName || 'U').charAt(0) }}</div>
        <div class="post-body">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-meta">
            <span class="author">{{ post.authorName }}</span>
            <span class="dot">·</span>
            <span>{{ timeAgo(post.createdAt) }}</span>
          </p>
        </div>
        <div class="post-stats">
          <div class="reply-badge">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ post.replyCount }}</span>
          </div>
        </div>
      </div>
      <div v-if="!loading && !posts.length" class="empty">No posts yet. Start a discussion!</div>
    </div>

    <div class="pagination-wrapper" v-if="total > query.pageSize">
      <el-pagination v-model:current-page="query.pageNum" :page-size="query.pageSize" :total="total"
                     layout="prev, pager, next" @current-change="loadData" />
    </div>

    <el-dialog v-model="dialogVisible" title="Create New Post" width="560px" destroy-on-close>
      <el-form label-position="top">
        <el-form-item label="Title">
          <el-input v-model="form.title" placeholder="What's on your mind?" maxlength="200" />
        </el-form-item>
        <el-form-item label="Content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="Share your thoughts..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">Post</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.filter-bar { display: flex; justify-content: space-between; align-items: center; }
.post-list { display: flex; flex-direction: column; gap: 10px; }
.post-card {
  background: #fff; border: 1px solid #f0f0f0; border-radius: 14px; padding: 20px 24px;
  display: flex; align-items: center; gap: 16px; cursor: pointer; transition: all 0.2s;
}
.post-card:hover { border-color: #c7d2fe; box-shadow: 0 4px 16px rgba(0,0,0,0.05); transform: translateY(-1px); }
.post-avatar {
  width: 42px; height: 42px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, #6366f1, #8b5cf6); color: #fff;
  display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 16px;
}
.post-body { flex: 1; min-width: 0; }
.post-title { margin: 0 0 4px; font-size: 15px; font-weight: 600; color: #1e1b4b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.post-meta { margin: 0; font-size: 13px; color: #9ca3af; display: flex; gap: 6px; align-items: center; }
.author { color: #6366f1; font-weight: 500; }
.dot { color: #d1d5db; }
.post-stats { flex-shrink: 0; }
.reply-badge {
  display: flex; align-items: center; gap: 4px; font-size: 13px; color: #9ca3af;
  background: #f3f4f6; padding: 4px 10px; border-radius: 20px;
}
.empty { text-align: center; color: #ccc; padding: 60px 0; font-size: 15px; }
.pagination-wrapper { display: flex; justify-content: center; }
</style>
