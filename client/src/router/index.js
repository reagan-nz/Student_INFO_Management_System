import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import StudentList from '../views/StudentList.vue'
import ClassList from '../views/ClassList.vue'
import SubjectList from '../views/SubjectList.vue'
import GradeList from '../views/GradeList.vue'
import Forum from '../views/Forum.vue'
import ForumPost from '../views/ForumPost.vue'
import Messages from '../views/Messages.vue'
import Schedule from '../views/Schedule.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: 'Dashboard' }
      },
      {
        path: 'students',
        name: 'StudentList',
        component: StudentList,
        meta: { title: 'Student Management' }
      },
      {
        path: 'classes',
        name: 'ClassList',
        component: ClassList,
        meta: { title: 'Class Management' }
      },
      {
        path: 'subjects',
        name: 'SubjectList',
        component: SubjectList,
        meta: { title: 'Subject Management' }
      },
      {
        path: 'grades',
        name: 'GradeList',
        component: GradeList,
        meta: { title: 'Grade Management' }
      },
      {
        path: 'forum',
        name: 'Forum',
        component: Forum,
        meta: { title: 'Forum' }
      },
      {
        path: 'forum/:id',
        name: 'ForumPost',
        component: ForumPost,
        meta: { title: 'Discussion' }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: Messages,
        meta: { title: 'Messages' }
      },
      {
        path: 'schedule',
        name: 'Schedule',
        component: Schedule,
        meta: { title: 'Schedule' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if (to.path !== '/login' && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
