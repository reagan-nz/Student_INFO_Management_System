import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '',
  timeout: 10000,
  withCredentials: true
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    ElMessage.error(res.message || 'Request failed')
    return Promise.reject(new Error(res.message))
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('user')
      router.push('/login')
    } else {
      ElMessage.error(error.message || 'Network error')
    }
    return Promise.reject(error)
  }
)

export default request
