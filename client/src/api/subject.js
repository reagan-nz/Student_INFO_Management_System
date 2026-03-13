import request from './request'

export function getSubjectList(params) {
  return request.get('/api/subjects', { params })
}

export function getAllSubjects() {
  return request.get('/api/subjects/all')
}

export function getSubjectById(id) {
  return request.get(`/api/subjects/${id}`)
}

export function addSubject(data) {
  return request.post('/api/subjects', data)
}

export function updateSubject(id, data) {
  return request.put(`/api/subjects/${id}`, data)
}

export function deleteSubject(id) {
  return request.delete(`/api/subjects/${id}`)
}
