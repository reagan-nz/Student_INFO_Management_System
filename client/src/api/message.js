import request from './request'

export function getConversations() {
  return request.get('/api/messages/conversations')
}

export function getMessages(otherUserId) {
  return request.get(`/api/messages/${otherUserId}`)
}

export function sendMessage(data) {
  return request.post('/api/messages', data)
}

export function markAsRead(otherUserId) {
  return request.put(`/api/messages/read/${otherUserId}`)
}

export function getUnreadCount() {
  return request.get('/api/messages/unread-count')
}

export function getAllUsers() {
  return request.get('/api/messages/users')
}
