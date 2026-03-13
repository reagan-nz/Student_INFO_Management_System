<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSubjectList, addSubject, updateSubject, deleteSubject } from '../api/subject'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const query = ref({ subjectName: '', pageNum: 1, pageSize: 10 })
const dialogVisible = ref(false)
const dialogTitle = ref('Add Subject')
const form = ref({})
const formRef = ref(null)

const rules = {
  subjectName: [{ required: true, message: 'Subject name is required', trigger: 'blur' }],
  credit: [{ required: true, message: 'Credit is required', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSubjectList(query.value)
    tableData.value = res.data.rows
    total.value = res.data.total
  } catch { /* interceptor */ } finally { loading.value = false }
}

const handleSearch = () => { query.value.pageNum = 1; loadData() }
const handleReset = () => { query.value.subjectName = ''; query.value.pageNum = 1; loadData() }

const handleAdd = () => {
  dialogTitle.value = 'Add Subject'
  form.value = { credit: 3.0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = 'Edit Subject'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`Delete subject "${row.subjectName}"?`, 'Delete Subject', {
    type: 'warning', confirmButtonText: 'Delete'
  }).then(async () => {
    await deleteSubject(row.id)
    ElMessage.success('Deleted')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  if (form.value.id) {
    await updateSubject(form.value.id, form.value)
    ElMessage.success('Updated')
  } else {
    await addSubject(form.value)
    ElMessage.success('Added')
  }
  dialogVisible.value = false
  loadData()
}

const handlePageChange = (p) => { query.value.pageNum = p; loadData() }
const handleSizeChange = (s) => { query.value.pageSize = s; query.value.pageNum = 1; loadData() }

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <div class="filter-bar">
      <el-form :inline="true" :model="query">
        <el-form-item>
          <el-input v-model="query.subjectName" placeholder="Search by subject name..." clearable
                    prefix-icon="Search" style="width: 260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> Search</el-button>
          <el-button @click="handleReset">Reset</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> Add Subject</el-button>
    </div>

    <div class="table-wrapper">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%"
                :header-cell-style="{ background: '#fafafa', color: '#374151', fontWeight: 600 }">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="subjectName" label="Subject Name" width="200">
          <template #default="{ row }">
            <div class="subject-name-cell">
              <div class="subject-dot" :style="{ background: subjectColor(row.id) }"></div>
              <span class="name-cell">{{ row.subjectName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="Credits" width="100">
          <template #default="{ row }">
            <el-tag effect="plain" round size="small">{{ row.credit }} cr</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="Description" min-width="300" show-overflow-tooltip />
        <el-table-column label="Actions" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> Edit
            </el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
                       :page-sizes="[5,10,20,50]" :total="total"
                       layout="total, sizes, prev, pager, next, jumper"
                       @current-change="handlePageChange" @size-change="handleSizeChange" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="Subject Name" prop="subjectName">
          <el-input v-model="form.subjectName" placeholder="e.g. Mathematics" />
        </el-form-item>
        <el-form-item label="Credits" prop="credit">
          <el-input-number v-model="form.credit" :min="0.5" :max="10" :step="0.5" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
const colors = ['#6366f1','#8b5cf6','#ec4899','#f59e0b','#10b981','#06b6d4']
function subjectColor(id) { return colors[(id - 1) % colors.length] }
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.filter-bar { background: #fff; border-radius: 14px; padding: 20px 24px; display: flex; align-items: flex-start; justify-content: space-between; border: 1px solid #f0f0f0; }
.filter-bar :deep(.el-form-item) { margin-bottom: 0; }
.table-wrapper { background: #fff; border-radius: 14px; border: 1px solid #f0f0f0; overflow: hidden; }
.table-wrapper :deep(.el-table) { --el-table-border-color: #f5f5f5; }
.table-wrapper :deep(.el-table th.el-table__cell) { border-bottom: 2px solid #f0f0f0; }
.name-cell { font-weight: 600; color: #1e1b4b; }
.subject-name-cell { display: flex; align-items: center; gap: 10px; }
.subject-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.pagination-wrapper { display: flex; justify-content: flex-end; padding: 16px 24px; border-top: 1px solid #f5f5f5; }
</style>
