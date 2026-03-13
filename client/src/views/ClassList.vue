<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClassList, addClass, updateClass, deleteClass } from '../api/classInfo'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const query = ref({
  className: '',
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('Add Class')
const form = ref({})
const formRef = ref(null)

const rules = {
  className: [{ required: true, message: 'Class name is required', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getClassList(query.value)
    tableData.value = res.data.rows
    total.value = res.data.total
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.value.pageNum = 1
  loadData()
}

const handleReset = () => {
  query.value.className = ''
  query.value.pageNum = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = 'Add Class'
  form.value = {}
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = 'Edit Class'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete "${row.className}"?`,
    'Delete Class',
    { type: 'warning', confirmButtonText: 'Delete', cancelButtonText: 'Cancel' }
  ).then(async () => {
    await deleteClass(row.id)
    ElMessage.success('Deleted successfully')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  if (form.value.id) {
    await updateClass(form.value.id, form.value)
    ElMessage.success('Updated successfully')
  } else {
    await addClass(form.value)
    ElMessage.success('Added successfully')
  }
  dialogVisible.value = false
  loadData()
}

const handlePageChange = (page) => {
  query.value.pageNum = page
  loadData()
}

const handleSizeChange = (size) => {
  query.value.pageSize = size
  query.value.pageNum = 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page-container">
    <!-- Search bar -->
    <div class="filter-bar">
      <el-form :inline="true" :model="query">
        <el-form-item>
          <el-input
            v-model="query.className"
            placeholder="Search by class name..."
            clearable
            prefix-icon="Search"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> Search
          </el-button>
          <el-button @click="handleReset">Reset</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> Add Class
      </el-button>
    </div>

    <!-- Table -->
    <div class="table-wrapper">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#fafafa', color: '#374151', fontWeight: 600 }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="className" label="Class Name" width="200">
          <template #default="{ row }">
            <span class="name-cell">{{ row.className }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="teacher" label="Homeroom Teacher" width="180">
          <template #default="{ row }">
            <div class="teacher-cell" v-if="row.teacher">
              <div class="teacher-avatar">{{ row.teacher.charAt(0) }}</div>
              <span>{{ row.teacher }}</span>
            </div>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="Description" min-width="280" show-overflow-tooltip />
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
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <!-- Add / Edit dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="480px"
      destroy-on-close
      class="form-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="Class Name" prop="className">
          <el-input v-model="form.className" placeholder="e.g. Class 2024-01" />
        </el-form-item>
        <el-form-item label="Homeroom Teacher">
          <el-input v-model="form.teacher" placeholder="Teacher name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="Brief class description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-bar {
  background: #fff;
  border-radius: 14px;
  padding: 20px 24px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  border: 1px solid #f0f0f0;
}
.filter-bar :deep(.el-form-item) {
  margin-bottom: 0;
}

.table-wrapper {
  background: #fff;
  border-radius: 14px;
  padding: 0;
  border: 1px solid #f0f0f0;
  overflow: hidden;
}
.table-wrapper :deep(.el-table) {
  --el-table-border-color: #f5f5f5;
}
.table-wrapper :deep(.el-table th.el-table__cell) {
  border-bottom: 2px solid #f0f0f0;
}

.name-cell {
  font-weight: 600;
  color: #1e1b4b;
}

.teacher-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.teacher-avatar {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #4338ca;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}
.text-muted {
  color: #ccc;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #f5f5f5;
}
</style>
