import request from './request'

export function getStudentList(params) {
  return request.get('/api/students', { params })
}

export function getStudentById(id) {
  return request.get(`/api/students/${id}`)
}

export function addStudent(data) {
  return request.post('/api/students', data)
}

export function updateStudent(id, data) {
  return request.put(`/api/students/${id}`, data)
}

export function deleteStudent(id) {
  return request.delete(`/api/students/${id}`)
}
