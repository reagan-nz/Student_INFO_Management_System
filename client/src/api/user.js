import request from './request'

export function login(data) {
  return request.post('/api/login', data)
}

export function logout() {
  return request.post('/api/logout')
}

export function getUserInfo() {
  return request.get('/api/userinfo')
}
