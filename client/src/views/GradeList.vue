<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGradeList, addGrade, updateGrade, deleteGrade, getStudentGpa, getAllSemesters } from '../api/grade'
import { getAllClasses } from '../api/classInfo'
import { getStudentList } from '../api/student'
import { getAllSubjects } from '../api/subject'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const studentList = ref([])
const subjectList = ref([])
const semesterList = ref([])

const query = ref({ studentId: null, subjectId: null, semester: '', pageNum: 1, pageSize: 10 })
const dialogVisible = ref(false)
const dialogTitle = ref('Add Grade')
const form = ref({})
const formRef = ref(null)

const gpaDialogVisible = ref(false)
const gpaStudent = ref(null)
const gpaValue = ref(null)
const gpaLoading = ref(false)

const rules = {
  studentId: [{ required: true, message: 'Student is required', trigger: 'change' }],
  subjectId: [{ required: true, message: 'Subject is required', trigger: 'change' }],
  score: [{ required: true, message: 'Score is required', trigger: 'blur' }],
  semester: [{ required: true, message: 'Semester is required', trigger: 'blur' }]
}

const loadStudents = async () => {
  try {
    const res = await getStudentList({ pageNum: 1, pageSize: 1000 })
    studentList.value = res.data.rows
  } catch {}
}
const loadSubjects = async () => {
  try {
    const res = await getAllSubjects()
    subjectList.value = res.data
  } catch {}
}
const loadSemesters = async () => {
  try {
    const res = await getAllSemesters()
    semesterList.value = res.data
  } catch {}
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getGradeList(query.value)
    tableData.value = res.data.rows
    total.value = res.data.total
  } catch {} finally { loading.value = false }
}

const handleSearch = () => { query.value.pageNum = 1; loadData() }
const handleReset = () => {
  query.value.studentId = null
  query.value.subjectId = null
  query.value.semester = ''
  query.value.pageNum = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = 'Add Grade'
  form.value = { semester: semesterList.value[0] || '2024-Fall' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = 'Edit Grade'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Delete this grade record for "${row.studentName}"?`, 'Delete Grade',
    { type: 'warning', confirmButtonText: 'Delete' }
  ).then(async () => {
    await deleteGrade(row.id)
    ElMessage.success('Deleted')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  if (form.value.id) {
    await updateGrade(form.value.id, form.value)
    ElMessage.success('Updated')
  } else {
    await addGrade(form.value)
    ElMessage.success('Added')
  }
  dialogVisible.value = false
  loadData()
}

const handleViewGpa = async (row) => {
  gpaStudent.value = row
  gpaDialogVisible.value = true
  gpaLoading.value = true
  try {
    const res = await getStudentGpa(row.studentId)
    gpaValue.value = res.data
  } catch {} finally { gpaLoading.value = false }
}

const scoreColor = (score) => {
  if (score >= 90) return '#10b981'
  if (score >= 80) return '#6366f1'
  if (score >= 70) return '#f59e0b'
  if (score >= 60) return '#f97316'
  return '#ef4444'
}

const scoreLabel = (score) => {
  if (score >= 90) return 'A'
  if (score >= 80) return 'B'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

const gpaLetterGrade = computed(() => {
  if (!gpaValue.value) return '-'
  const g = parseFloat(gpaValue.value)
  if (g >= 90) return 'A'
  if (g >= 80) return 'B'
  if (g >= 70) return 'C'
  if (g >= 60) return 'D'
  return 'F'
})

const handlePageChange = (p) => { query.value.pageNum = p; loadData() }
const handleSizeChange = (s) => { query.value.pageSize = s; query.value.pageNum = 1; loadData() }

onMounted(() => {
  loadStudents()
  loadSubjects()
  loadSemesters()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <!-- Filters -->
    <div class="filter-bar">
      <el-form :inline="true" :model="query">
        <el-form-item>
          <el-select v-model="query.studentId" placeholder="All students" clearable filterable style="width: 180px">
            <el-option v-for="s in studentList" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="query.subjectId" placeholder="All subjects" clearable style="width: 170px">
            <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="query.semester" placeholder="All semesters" clearable style="width: 150px">
            <el-option v-for="s in semesterList" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> Search</el-button>
          <el-button @click="handleReset">Reset</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> Add Grade</el-button>
    </div>

    <!-- Table -->
    <div class="table-wrapper">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%"
                :header-cell-style="{ background: '#fafafa', color: '#374151', fontWeight: 600 }">
        <el-table-column prop="studentName" label="Student" width="140">
          <template #default="{ row }">
            <div class="student-cell">
              <span class="name-cell">{{ row.studentName }}</span>
              <span class="student-no-sub">{{ row.studentNo }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="subjectName" label="Subject" width="160" />
        <el-table-column prop="credit" label="Credits" width="85">
          <template #default="{ row }">
            <span>{{ row.credit }} cr</span>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="Score" width="120">
          <template #default="{ row }">
            <div class="score-cell">
              <span class="score-value" :style="{ color: scoreColor(row.score) }">{{ row.score }}</span>
              <el-tag :color="scoreColor(row.score)" effect="dark" size="small" round
                      style="border: none; color: #fff; margin-left: 6px;">
                {{ scoreLabel(row.score) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="Semester" width="130">
          <template #default="{ row }">
            <el-tag type="info" effect="plain" size="small">{{ row.semester }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="success" text size="small" @click="handleViewGpa(row)">
              <el-icon><TrophyBase /></el-icon> GPA
            </el-button>
            <el-button type="primary" text size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> Edit
            </el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
                       :page-sizes="[10,20,50,100]" :total="total"
                       layout="total, sizes, prev, pager, next, jumper"
                       @current-change="handlePageChange" @size-change="handleSizeChange" />
      </div>
    </div>

    <!-- Add / Edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="Student" prop="studentId">
            <el-select v-model="form.studentId" filterable placeholder="Select student" style="width: 100%">
              <el-option v-for="s in studentList" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Subject" prop="subjectId">
            <el-select v-model="form.subjectId" placeholder="Select subject" style="width: 100%">
              <el-option v-for="s in subjectList" :key="s.id" :label="s.subjectName" :value="s.id" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-grid">
          <el-form-item label="Score" prop="score">
            <el-input-number v-model="form.score" :min="0" :max="100" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Semester" prop="semester">
            <el-select v-model="form.semester" filterable allow-create placeholder="e.g. 2024-Fall" style="width: 100%">
              <el-option v-for="s in semesterList" :key="s" :label="s" :value="s" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">Save</el-button>
      </template>
    </el-dialog>

    <!-- GPA dialog -->
    <el-dialog v-model="gpaDialogVisible" title="Student GPA" width="380px">
      <div v-loading="gpaLoading" class="gpa-card">
        <div class="gpa-student-name">{{ gpaStudent?.studentName }}</div>
        <div class="gpa-circle" :style="{ borderColor: gpaValue ? scoreColor(gpaValue) : '#ccc' }">
          <span class="gpa-number">{{ gpaValue ?? '-' }}</span>
          <span class="gpa-letter" :style="{ color: gpaValue ? scoreColor(gpaValue) : '#ccc' }">
            {{ gpaLetterGrade }}
          </span>
        </div>
        <p class="gpa-note">Weighted GPA (score x credits / total credits)</p>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.filter-bar { background: #fff; border-radius: 14px; padding: 20px 24px; display: flex; align-items: flex-start; justify-content: space-between; border: 1px solid #f0f0f0; }
.filter-bar :deep(.el-form-item) { margin-bottom: 0; }
.table-wrapper { background: #fff; border-radius: 14px; border: 1px solid #f0f0f0; overflow: hidden; }
.table-wrapper :deep(.el-table) { --el-table-border-color: #f5f5f5; }
.table-wrapper :deep(.el-table th.el-table__cell) { border-bottom: 2px solid #f0f0f0; }
.name-cell { font-weight: 600; color: #1e1b4b; }
.student-cell { display: flex; flex-direction: column; }
.student-no-sub { font-size: 12px; color: #9ca3af; }
.score-cell { display: flex; align-items: center; }
.score-value { font-weight: 700; font-size: 15px; }
.pagination-wrapper { display: flex; justify-content: flex-end; padding: 16px 24px; border-top: 1px solid #f5f5f5; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 20px; }

.gpa-card { text-align: center; padding: 20px 0; }
.gpa-student-name { font-size: 18px; font-weight: 600; color: #1e1b4b; margin-bottom: 20px; }
.gpa-circle {
  width: 140px; height: 140px; border-radius: 50%; border: 5px solid;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  margin: 0 auto 16px;
}
.gpa-number { font-size: 32px; font-weight: 800; color: #1e1b4b; line-height: 1.1; }
.gpa-letter { font-size: 18px; font-weight: 700; }
.gpa-note { font-size: 12px; color: #9ca3af; margin: 0; }
</style>
