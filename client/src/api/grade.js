import request from './request'

export function getGradeList(params) {
  return request.get('/api/grades', { params })
}

export function getGradeById(id) {
  return request.get(`/api/grades/${id}`)
}

export function addGrade(data) {
  return request.post('/api/grades', data)
}

export function updateGrade(id, data) {
  return request.put(`/api/grades/${id}`, data)
}

export function deleteGrade(id) {
  return request.delete(`/api/grades/${id}`)
}

export function getStudentGpa(studentId) {
  return request.get(`/api/grades/gpa/${studentId}`)
}

export function getAllSemesters() {
  return request.get('/api/grades/semesters')
}
