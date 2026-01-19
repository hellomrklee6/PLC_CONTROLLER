import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 设备相关API
export const deviceApi = {
  getDevices: () => apiClient.get('/devices'),
  getDevice: (id) => apiClient.get(`/devices/${id}`),
  createDevice: (device) => apiClient.post('/devices', device),
  updateDevice: (id, device) => apiClient.put(`/devices/${id}`, device),
  deleteDevice: (id) => apiClient.delete(`/devices/${id}`),
  toggleEnabled: (id, enabled) => apiClient.put(`/devices/${id}/enabled`, enabled),
  connect: (id) => apiClient.post(`/devices/${id}/connect`),
  disconnect: (id) => apiClient.post(`/devices/${id}/disconnect`),
  getStatus: (id) => apiClient.get(`/devices/${id}/status`)
}

// 地址相关API
export const addressApi = {
  getAddresses: () => apiClient.get('/addresses'),
  getAddress: (id) => apiClient.get(`/addresses/${id}`),
  createAddress: (address) => apiClient.post('/addresses', address),
  updateAddress: (id, address) => apiClient.put(`/addresses/${id}`, address),
  deleteAddress: (id) => apiClient.delete(`/addresses/${id}`),
  batchUpdateDevice: (addressIds, deviceId) => apiClient.put('/addresses/batch/device', { addressIds, deviceId })
}

// 控制相关API
export const controlApi = {
  getValue: (addressId) => apiClient.get(`/control/${addressId}`),
  setValue: (addressId, value) => apiClient.post(`/control/${addressId}`, { value })
}

export default apiClient
