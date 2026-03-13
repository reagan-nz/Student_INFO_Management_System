import request from './request'

export function getClassList(params) {
  return request.get('/api/classes', { params })
}

export function getAllClasses() {
  return request.get('/api/classes/all')
}

export function getClassById(id) {
  return request.get(`/api/classes/${id}`)
}

export function addClass(data) {
  return request.post('/api/classes', data)
}

export function updateClass(id, data) {
  return request.put(`/api/classes/${id}`, data)
}

export function deleteClass(id) {
  return request.delete(`/api/classes/${id}`)
}
