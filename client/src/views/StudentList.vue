<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentList, addStudent, updateStudent, deleteStudent } from '../api/student'
import { getAllClasses } from '../api/classInfo'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const classList = ref([])

const query = ref({
  name: '',
  classId: null,
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('Add Student')
const form = ref({})
const formRef = ref(null)

const rules = {
  studentNo: [{ required: true, message: 'Student No is required', trigger: 'blur' }],
  name: [{ required: true, message: 'Name is required', trigger: 'blur' }]
}

const loadClasses = async () => {
  try {
    const res = await getAllClasses()
    classList.value = res.data
  } catch {
    // handled by interceptor
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudentList(query.value)
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
  query.value.name = ''
  query.value.classId = null
  query.value.pageNum = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = 'Add Student'
  form.value = { gender: 'Male' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = 'Edit Student'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete "${row.name}"?`,
    'Delete Student',
    { type: 'warning', confirmButtonText: 'Delete', cancelButtonText: 'Cancel' }
  ).then(async () => {
    await deleteStudent(row.id)
    ElMessage.success('Deleted successfully')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  if (form.value.id) {
    await updateStudent(form.value.id, form.value)
    ElMessage.success('Updated successfully')
  } else {
    await addStudent(form.value)
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
  loadClasses()
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
            v-model="query.name"
            placeholder="Search by name..."
            clearable
            prefix-icon="Search"
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="query.classId" placeholder="All classes" clearable style="width: 180px">
            <el-option
              v-for="c in classList"
              :key="c.id"
              :label="c.className"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> Search
          </el-button>
          <el-button @click="handleReset">Reset</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> Add Student
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
        <el-table-column prop="studentNo" label="Student No" width="130" />
        <el-table-column prop="name" label="Name" width="110">
          <template #default="{ row }">
            <span class="name-cell">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="Gender" width="85">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'Male' ? '' : 'danger'" size="small" effect="plain" round>
              {{ row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="Age" width="70" />
        <el-table-column prop="phone" label="Phone" width="140" />
        <el-table-column prop="className" label="Class" width="150">
          <template #default="{ row }">
            <el-tag type="info" size="small" effect="plain">{{ row.className || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="Address" min-width="180" show-overflow-tooltip />
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
      width="520px"
      destroy-on-close
      class="form-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" label-position="top">
        <div class="form-grid">
          <el-form-item label="Student No" prop="studentNo">
            <el-input v-model="form.studentNo" placeholder="e.g. S20240101" />
          </el-form-item>
          <el-form-item label="Name" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
        </div>
        <div class="form-grid">
          <el-form-item label="Gender">
            <el-radio-group v-model="form.gender">
              <el-radio value="Male">Male</el-radio>
              <el-radio value="Female">Female</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="Age">
            <el-input-number v-model="form.age" :min="1" :max="100" style="width: 100%" />
          </el-form-item>
        </div>
        <div class="form-grid">
          <el-form-item label="Phone">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="Class">
            <el-select v-model="form.classId" placeholder="Select class" clearable style="width: 100%">
              <el-option
                v-for="c in classList"
                :key="c.id"
                :label="c.className"
                :value="c.id"
              />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="Address">
          <el-input v-model="form.address" type="textarea" :rows="2" />
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #f5f5f5;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 20px;
}
</style>
