<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboardStats } from '../api/dashboard'

const router = useRouter()
const stats = ref({
  studentCount: 0, classCount: 0, subjectCount: 0, overallAverage: 0,
  scoreDistribution: [], topStudents: [], avgScorePerSubject: []
})
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const loaded = ref(false)

const gradeColor = (score) => {
  if (score >= 90) return '#10b981'
  if (score >= 80) return '#6366f1'
  if (score >= 70) return '#f59e0b'
  if (score >= 60) return '#f97316'
  return '#ef4444'
}

const distColors = {
  'A (90-100)': '#10b981',
  'B (80-89)': '#6366f1',
  'C (70-79)': '#f59e0b',
  'D (60-69)': '#f97316',
  'F (Below 60)': '#ef4444'
}

const maxDistCount = ref(1)

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    stats.value = res.data
    const dist = stats.value.scoreDistribution || []
    maxDistCount.value = Math.max(...dist.map(d => d.cnt), 1)
  } catch {}
  setTimeout(() => { loaded.value = true }, 100)
})
</script>

<template>
  <div class="dashboard" :class="{ loaded }">
    <!-- Welcome banner -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h1>Welcome back, {{ user.nickname || user.username }}!</h1>
        <p>Here's an overview of your student management system.</p>
      </div>
    </div>

    <!-- Stats row -->
    <div class="stats-grid">
      <div class="stat-card c1" @click="router.push('/students')">
        <div class="stat-icon"><el-icon :size="26"><User /></el-icon></div>
        <div class="stat-info">
          <span class="stat-number">{{ stats.studentCount }}</span>
          <span class="stat-label">Students</span>
        </div>
      </div>
      <div class="stat-card c2" @click="router.push('/classes')">
        <div class="stat-icon"><el-icon :size="26"><School /></el-icon></div>
        <div class="stat-info">
          <span class="stat-number">{{ stats.classCount }}</span>
          <span class="stat-label">Classes</span>
        </div>
      </div>
      <div class="stat-card c3" @click="router.push('/subjects')">
        <div class="stat-icon"><el-icon :size="26"><Reading /></el-icon></div>
        <div class="stat-info">
          <span class="stat-number">{{ stats.subjectCount }}</span>
          <span class="stat-label">Subjects</span>
        </div>
      </div>
      <div class="stat-card c4" @click="router.push('/grades')">
        <div class="stat-icon"><el-icon :size="26"><TrophyBase /></el-icon></div>
        <div class="stat-info">
          <span class="stat-number">{{ stats.overallAverage || '-' }}</span>
          <span class="stat-label">Avg Score</span>
        </div>
      </div>
    </div>

    <!-- Charts row -->
    <div class="charts-row">
      <!-- Grade distribution -->
      <div class="chart-card">
        <h3>Grade Distribution</h3>
        <div class="bar-chart">
          <div v-for="item in stats.scoreDistribution" :key="item.grade_level" class="bar-row">
            <span class="bar-label">{{ item.grade_level }}</span>
            <div class="bar-track">
              <div class="bar-fill" :style="{
                width: (item.cnt / maxDistCount * 100) + '%',
                background: distColors[item.grade_level] || '#6366f1'
              }"></div>
            </div>
            <span class="bar-value">{{ item.cnt }}</span>
          </div>
        </div>
      </div>

      <!-- Top students -->
      <div class="chart-card">
        <h3>Top 5 Students (by GPA)</h3>
        <div class="top-list">
          <div v-for="(s, i) in stats.topStudents" :key="s.student_no" class="top-item">
            <div class="rank" :class="'rank-' + (i + 1)">{{ i + 1 }}</div>
            <div class="top-info">
              <span class="top-name">{{ s.name }}</span>
              <span class="top-no">{{ s.student_no }}</span>
            </div>
            <div class="top-gpa" :style="{ color: gradeColor(s.gpa) }">
              {{ s.gpa }}
            </div>
          </div>
          <div v-if="!stats.topStudents?.length" class="empty-hint">No grade data yet</div>
        </div>
      </div>
    </div>

    <!-- Subject averages -->
    <div class="chart-card full-width">
      <h3>Average Score by Subject</h3>
      <div class="subject-bars">
        <div v-for="item in stats.avgScorePerSubject" :key="item.subject_name" class="subject-bar-item">
          <div class="subject-bar-header">
            <span class="subject-bar-name">{{ item.subject_name }}</span>
            <span class="subject-bar-score" :style="{ color: gradeColor(item.avg_score) }">{{ item.avg_score }}</span>
          </div>
          <div class="subject-bar-track">
            <div class="subject-bar-fill" :style="{
              width: item.avg_score + '%',
              background: `linear-gradient(90deg, ${gradeColor(item.avg_score)}, ${gradeColor(item.avg_score)}88)`
            }"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex; flex-direction: column; gap: 22px;
  opacity: 0; transform: translateY(12px); transition: all 0.5s ease;
}
.dashboard.loaded { opacity: 1; transform: translateY(0); }

.welcome-banner {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a78bfa 100%);
  border-radius: 16px; padding: 32px 36px; color: #fff; position: relative; overflow: hidden;
}
.welcome-banner::before {
  content: ''; position: absolute; top: -50px; right: -50px;
  width: 220px; height: 220px; background: rgba(255,255,255,0.06); border-radius: 50%;
}
.welcome-banner::after {
  content: ''; position: absolute; bottom: -30px; right: 80px;
  width: 120px; height: 120px; background: rgba(255,255,255,0.04); border-radius: 50%;
}
.welcome-text h1 { margin: 0 0 6px; font-size: 24px; font-weight: 700; }
.welcome-text p { margin: 0; opacity: 0.85; font-size: 14px; }

/* Stats grid */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.stat-card {
  background: #fff; border-radius: 14px; padding: 22px; display: flex;
  align-items: center; gap: 14px; cursor: pointer; transition: all 0.3s;
  border: 1px solid #f0f0f0; position: relative; overflow: hidden;
}
.stat-card::before {
  content: ''; position: absolute; top: 0; left: 0; width: 4px; height: 100%;
  border-radius: 4px 0 0 4px;
}
.stat-card.c1::before { background: #6366f1; }
.stat-card.c2::before { background: #8b5cf6; }
.stat-card.c3::before { background: #ec4899; }
.stat-card.c4::before { background: #10b981; }
.stat-card:hover { transform: translateY(-3px); box-shadow: 0 12px 24px rgba(0,0,0,0.07); }
.stat-icon {
  width: 48px; height: 48px; border-radius: 12px; display: flex;
  align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-card.c1 .stat-icon { background: #eef2ff; color: #6366f1; }
.stat-card.c2 .stat-icon { background: #f5f3ff; color: #8b5cf6; }
.stat-card.c3 .stat-icon { background: #fdf2f8; color: #ec4899; }
.stat-card.c4 .stat-icon { background: #ecfdf5; color: #10b981; }
.stat-info { display: flex; flex-direction: column; }
.stat-number { font-size: 26px; font-weight: 700; color: #1e1b4b; line-height: 1.2; }
.stat-label { font-size: 13px; color: #6b7280; margin-top: 2px; }

/* Charts row */
.charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.chart-card {
  background: #fff; border-radius: 14px; padding: 24px;
  border: 1px solid #f0f0f0;
}
.chart-card.full-width { grid-column: 1 / -1; }
.chart-card h3 { margin: 0 0 18px; font-size: 16px; font-weight: 600; color: #1e1b4b; }

/* Bar chart */
.bar-chart { display: flex; flex-direction: column; gap: 12px; }
.bar-row { display: flex; align-items: center; gap: 12px; }
.bar-label { width: 100px; font-size: 13px; color: #6b7280; flex-shrink: 0; }
.bar-track { flex: 1; height: 24px; background: #f3f4f6; border-radius: 6px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 6px; transition: width 0.8s ease; min-width: 4px; }
.bar-value { width: 30px; text-align: right; font-size: 14px; font-weight: 600; color: #374151; }

/* Top students */
.top-list { display: flex; flex-direction: column; gap: 10px; }
.top-item {
  display: flex; align-items: center; gap: 14px; padding: 10px 14px;
  background: #fafafa; border-radius: 10px; transition: background 0.2s;
}
.top-item:hover { background: #f0f0f5; }
.rank {
  width: 30px; height: 30px; border-radius: 8px; display: flex;
  align-items: center; justify-content: center; font-weight: 700;
  font-size: 13px; flex-shrink: 0; background: #e5e7eb; color: #6b7280;
}
.rank-1 { background: linear-gradient(135deg, #fbbf24, #f59e0b); color: #fff; }
.rank-2 { background: linear-gradient(135deg, #9ca3af, #6b7280); color: #fff; }
.rank-3 { background: linear-gradient(135deg, #d97706, #b45309); color: #fff; }
.top-info { flex: 1; display: flex; flex-direction: column; }
.top-name { font-weight: 600; color: #1e1b4b; font-size: 14px; }
.top-no { font-size: 12px; color: #9ca3af; }
.top-gpa { font-size: 20px; font-weight: 800; }
.empty-hint { text-align: center; color: #ccc; padding: 20px; }

/* Subject bars */
.subject-bars { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.subject-bar-item { }
.subject-bar-header { display: flex; justify-content: space-between; margin-bottom: 6px; }
.subject-bar-name { font-size: 13px; color: #374151; font-weight: 500; }
.subject-bar-score { font-size: 14px; font-weight: 700; }
.subject-bar-track { height: 10px; background: #f3f4f6; border-radius: 5px; overflow: hidden; }
.subject-bar-fill { height: 100%; border-radius: 5px; transition: width 0.8s ease; }
</style>
