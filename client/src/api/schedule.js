import request from './request'

export function getScheduleSlots(params) {
  return request.get('/api/schedule', { params })
}

export function getStudentSchedule(studentId, params) {
  return request.get(`/api/schedule/student/${studentId}`, { params })
}

export function addSlot(data) {
  return request.post('/api/schedule', data)
}

export function updateSlot(id, data) {
  return request.put(`/api/schedule/${id}`, data)
}

export function deleteSlot(id) {
  return request.delete(`/api/schedule/${id}`)
}

export function getScheduleSemesters() {
  return request.get('/api/schedule/semesters')
}
