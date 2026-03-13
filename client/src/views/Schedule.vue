<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScheduleSlots, addSlot, updateSlot, deleteSlot, getScheduleSemesters } from '../api/schedule'

const slots = ref([])
const semesters = ref([])
const selectedSemester = ref('2024-Fall')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const subjects = ref([])
const form = ref({ subjectId: '', dayOfWeek: 1, startTime: '', endTime: '', room: '', semester: '2024-Fall' })

const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']
const hours = Array.from({ length: 11 }, (_, i) => i + 8) // 8 AM - 6 PM

const colors = [
  { bg: '#ede9fe', border: '#8b5cf6', text: '#5b21b6' },
  { bg: '#dbeafe', border: '#3b82f6', text: '#1e40af' },
  { bg: '#d1fae5', border: '#10b981', text: '#065f46' },
  { bg: '#fef3c7', border: '#f59e0b', text: '#92400e' },
  { bg: '#fce7f3', border: '#ec4899', text: '#9d174d' },
  { bg: '#e0e7ff', border: '#6366f1', text: '#3730a3' },
]

const colorMap = computed(() => {
  const map = {}
  const uniqueSubjects = [...new Set(slots.value.map(s => s.subjectId))]
  uniqueSubjects.forEach((id, i) => { map[id] = colors[i % colors.length] })
  return map
})

const parsedSlots = computed(() => {
  return slots.value.map(s => {
    const startParts = s.startTime.split(':')
    const endParts = s.endTime.split(':')
    const startHour = parseInt(startParts[0]) + parseInt(startParts[1]) / 60
    const endHour = parseInt(endParts[0]) + parseInt(endParts[1]) / 60
    const top = (startHour - 8) * 60
    const height = (endHour - startHour) * 60
    return { ...s, top, height, color: colorMap.value[s.subjectId] }
  })
}
)

const slotsForDay = (dayIndex) => {
  return parsedSlots.value.filter(s => s.dayOfWeek === dayIndex + 1)
}

const loadData = async () => {
  loading.value = true
  try {
    const [slotsRes, semRes] = await Promise.all([
      getScheduleSlots({ semester: selectedSemester.value }),
      getScheduleSemesters()
    ])
    slots.value = slotsRes.data
    semesters.value = semRes.data
    // Build subjects list for the add dialog
    const seen = new Set()
    subjects.value = slots.value.filter(s => { if (seen.has(s.subjectId)) return false; seen.add(s.subjectId); return true })
      .map(s => ({ id: s.subjectId, name: s.subjectName }))
  } catch {} finally { loading.value = false }
}

const openAdd = () => {
  isEdit.value = false
  form.value = { subjectId: '', dayOfWeek: 1, startTime: '09:00', endTime: '09:50', room: '', semester: selectedSemester.value }
  dialogVisible.value = true
}

const openEdit = (slot) => {
  isEdit.value = true
  form.value = {
    id: slot.id, subjectId: slot.subjectId, dayOfWeek: slot.dayOfWeek,
    startTime: slot.startTime?.substring(0, 5), endTime: slot.endTime?.substring(0, 5),
    room: slot.room, semester: slot.semester
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.subjectId || !form.value.startTime || !form.value.endTime) {
    ElMessage.warning('Please fill in required fields'); return
  }
  if (isEdit.value) {
    await updateSlot(form.value.id, form.value)
    ElMessage.success('Updated')
  } else {
    await addSlot(form.value)
    ElMessage.success('Added')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (slot) => {
  await ElMessageBox.confirm(`Delete ${slot.subjectName} slot?`, 'Confirm')
  await deleteSlot(slot.id)
  ElMessage.success('Deleted')
  loadData()
}

const formatSlotTime = (t) => {
  if (!t) return ''
  const parts = t.split(':')
  const h = parseInt(parts[0])
  const m = parts[1]
  const ampm = h >= 12 ? 'PM' : 'AM'
  const h12 = h > 12 ? h - 12 : (h === 0 ? 12 : h)
  return `${h12}:${m} ${ampm}`
}

onMounted(() => loadData())
</script>

<template>
  <div class="page-container">
    <div class="filter-bar">
      <div class="filter-left">
        <el-select v-model="selectedSemester" placeholder="Semester" style="width: 180px" @change="loadData">
          <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
        </el-select>
      </div>
      <el-button type="primary" @click="openAdd">
        <el-icon><Plus /></el-icon> Add Slot
      </el-button>
    </div>

    <div v-loading="loading" class="timetable-wrapper">
      <div class="timetable">
        <!-- Time labels column -->
        <div class="time-col">
          <div class="header-cell"></div>
          <div v-for="h in hours" :key="h" class="time-label">
            {{ h > 12 ? (h - 12) : h }}:00 {{ h >= 12 ? 'PM' : 'AM' }}
          </div>
        </div>
        <!-- Day columns -->
        <div v-for="(day, di) in days" :key="di" class="day-col">
          <div class="header-cell day-header">{{ day }}</div>
          <div class="day-body">
            <div v-for="h in hours" :key="h" class="hour-line" :style="{ top: (h - 8) * 60 + 'px' }"></div>
            <div v-for="slot in slotsForDay(di)" :key="slot.id"
                 class="slot-block" :style="{
                   top: slot.top + 'px', height: slot.height + 'px',
                   background: slot.color?.bg, borderLeft: '3px solid ' + slot.color?.border,
                   color: slot.color?.text
                 }"
                 @click="openEdit(slot)" @contextmenu.prevent="handleDelete(slot)">
              <div class="slot-name">{{ slot.subjectName?.split(' - ')[0] }}</div>
              <div class="slot-room">{{ slot.room }}</div>
              <div class="slot-time">{{ formatSlotTime(slot.startTime) }} - {{ formatSlotTime(slot.endTime) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Slot' : 'Add Slot'" width="460px" destroy-on-close>
      <el-form label-position="top">
        <el-form-item label="Course">
          <el-select v-model="form.subjectId" placeholder="Select course" style="width: 100%">
            <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Day">
          <el-select v-model="form.dayOfWeek" style="width: 100%">
            <el-option v-for="(d, i) in days" :key="i+1" :label="d" :value="i+1" />
          </el-select>
        </el-form-item>
        <div style="display: flex; gap: 12px">
          <el-form-item label="Start Time" style="flex:1">
            <el-input v-model="form.startTime" placeholder="HH:MM" />
          </el-form-item>
          <el-form-item label="End Time" style="flex:1">
            <el-input v-model="form.endTime" placeholder="HH:MM" />
          </el-form-item>
        </div>
        <el-form-item label="Room">
          <el-input v-model="form.room" placeholder="e.g. Science Hall 204" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSave">{{ isEdit ? 'Save' : 'Add' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.filter-bar { display: flex; justify-content: space-between; align-items: center; }
.timetable-wrapper { background: #fff; border-radius: 16px; border: 1px solid #f0f0f0; overflow-x: auto; }
.timetable { display: flex; min-width: 800px; }
.time-col { width: 80px; flex-shrink: 0; }
.header-cell { height: 48px; display: flex; align-items: center; justify-content: center; font-weight: 600; font-size: 13px; color: #6b7280; border-bottom: 1px solid #f0f0f0; }
.day-header { background: linear-gradient(135deg, #6366f1, #8b5cf6); color: #fff; font-size: 13px; }
.time-label { height: 60px; display: flex; align-items: flex-start; justify-content: flex-end; padding: 2px 10px 0 0; font-size: 11px; color: #9ca3af; }
.day-col { flex: 1; min-width: 0; border-left: 1px solid #f0f0f0; }
.day-body { position: relative; height: 660px; }
.hour-line { position: absolute; left: 0; right: 0; border-top: 1px solid #f5f5f5; }
.slot-block {
  position: absolute; left: 3px; right: 3px; border-radius: 8px; padding: 6px 8px;
  cursor: pointer; overflow: hidden; transition: transform 0.15s, box-shadow 0.15s;
  display: flex; flex-direction: column; justify-content: center;
}
.slot-block:hover { transform: scale(1.02); box-shadow: 0 4px 12px rgba(0,0,0,0.1); z-index: 2; }
.slot-name { font-weight: 700; font-size: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.slot-room { font-size: 11px; opacity: 0.8; }
.slot-time { font-size: 10px; opacity: 0.6; }
</style>
