<template>
  <div class="container-fluid">
    <!-- 操作按钮 -->
    <div class="mb-4">
      <button class="btn btn-primary" @click="addDevice">添加设备</button>
    </div>
    
    <!-- 设备列表 -->
    <div class="mb-4">
      <div class="card">
        <div class="card-header">
          <h6 class="mb-0">设备列表</h6>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>名称</th>
                  <th>IP地址</th>
                  <th>端口</th>
                  <th>协议</th>
                  <th>状态</th>
                  <th>启用</th>
                  <th>描述</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody id="deviceList">
                <tr v-if="devices.length === 0">
                  <td colspan="9" class="text-center">暂无设备</td>
                </tr>
                <tr v-for="device in devices" :key="device.id" :class="{ 'text-muted': !device.enabled || !device.connected }" :style="{ opacity: (!device.enabled || !device.connected) ? '0.6' : '1' }">
                  <td>{{ device.id }}</td>
                  <td>{{ device.name }}</td>
                  <td>{{ device.ip }}</td>
                  <td>{{ device.port }}</td>
                  <td>{{ device.protocol }}</td>
                  <td><span :class="deviceStatusClass(device)">{{ deviceStatusText(device) }}</span></td>
                  <td>
                    <div class="form-check form-switch">
                      <input class="form-check-input" type="checkbox" :id="`enabled-${device.id}`" :checked="device.enabled" @change="toggleDeviceEnabled(device.id, $event.target.checked)">
                    </div>
                  </td>
                  <td>{{ device.description || '-' }}</td>
                  <td>
                    <button class="btn btn-sm btn-primary" @click="editDevice(device)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteDevice(device.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 添加/编辑模态框 -->
  <div class="modal fade" id="addModal" tabindex="-1" aria-labelledby="addModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="addModalLabel">{{ editingDevice.id ? '编辑PLC设备' : '添加PLC设备' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form id="deviceForm">
            <input type="hidden" v-model="editingDevice.id">
            <div class="mb-3">
              <label for="deviceName" class="form-label">设备名称</label>
              <input type="text" class="form-control" id="deviceName" v-model="editingDevice.name" required>
            </div>
            <div class="mb-3">
              <label for="deviceIp" class="form-label">IP地址</label>
              <input type="text" class="form-control" id="deviceIp" v-model="editingDevice.ip" required>
            </div>
            <div class="mb-3">
              <label for="devicePort" class="form-label">端口</label>
              <input type="number" class="form-control" id="devicePort" v-model="editingDevice.port" required>
            </div>
            <div class="mb-3">
              <label for="deviceProtocol" class="form-label">协议</label>
              <select class="form-select" id="deviceProtocol" v-model="editingDevice.protocol" required>
                <option value="ModbusTCP">Modbus TCP</option>
                <option value="OPCUA">OPC UA</option>
                <!-- 可添加其他协议选项 -->
              </select>
            </div>
            <div class="mb-3">
              <label for="deviceDescription" class="form-label">描述</label>
              <textarea class="form-control" id="deviceDescription" v-model="editingDevice.description"></textarea>
            </div>
            <div class="mb-3 form-check">
              <input type="checkbox" class="form-check-input" id="deviceEnabled" v-model="editingDevice.enabled">
              <label class="form-check-label" for="deviceEnabled">启用设备</label>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn btn-primary" @click="saveDevice()">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import * as bootstrap from 'bootstrap'

// 响应式数据
const devices = ref([])
const editingDevice = ref({
  id: null,
  name: '',
  ip: '',
  port: '',
  protocol: 'ModbusTCP',
  description: '',
  enabled: true
})

// 方法
const loadDevices = async () => {
  try {
    const response = await axios.get('/api/devices')
    const deviceList = response.data
    
    // 获取每个设备的连接状态
    for (const device of deviceList) {
      if (device.enabled) {
        try {
          const statusResponse = await axios.get(`/api/devices/${device.id}/status`)
          device.connected = statusResponse.data
        } catch (error) {
          device.connected = false
        }
      } else {
        device.connected = false
      }
    }
    
    devices.value = deviceList
  } catch (error) {
    // 加载设备列表失败
  }
}

const deviceStatusClass = (device) => {
  if (!device.enabled) {
    return 'badge bg-secondary'
  }
  return device.connected ? 'badge bg-success' : 'badge bg-danger'
}

const deviceStatusText = (device) => {
  if (!device.enabled) {
    return '禁用'
  }
  return device.connected ? '在线' : '掉线'
}

const editDevice = (device) => {
  editingDevice.value = { ...device }
  // 显示模态框
  const modal = new bootstrap.Modal(document.getElementById('addModal'))
  modal.show()
}

// 添加设备按钮点击事件
const addDevice = () => {
  resetEditingDevice()
  const modal = new bootstrap.Modal(document.getElementById('addModal'))
  modal.show()
}

const saveDevice = async () => {
  try {
    let url = '/api/devices'
    let method = 'POST'
    
    if (editingDevice.value.id) {
      url = `/api/devices/${editingDevice.value.id}`
      method = 'PUT'
    }
    
    await axios({
      url,
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      data: editingDevice.value
    })
    
    // 关闭模态框
    const modal = bootstrap.Modal.getInstance(document.getElementById('addModal'))
    modal.hide()
    
    // 重新加载设备列表
    loadDevices()
    
    // 重置编辑设备
    resetEditingDevice()
    
    // 保存成功
  } catch (error) {
    // 保存失败
  }
}

const deleteDevice = async (id) => {
  if (confirm('确定要删除此设备吗？')) {
    try {
      const response = await axios.delete(`/api/devices/${id}`)
      if (response.status === 204) {
        loadDevices()
        // 删除成功
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        alert(error.response.data)
      } else {
        // 删除失败
      }
    }
  }
}

const toggleDeviceEnabled = async (id, enabled) => {
  try {
    await axios.put(`/api/devices/${id}/enabled`, enabled, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    loadDevices()
  } catch (error) {
    // 如果失败，恢复原来的状态
    const device = devices.value.find(d => d.id === id)
    if (device) {
      device.enabled = !enabled
    }
    // 更新设备启用状态失败
  }
}

const resetEditingDevice = () => {
  editingDevice.value = {
    id: null,
    name: '',
    ip: '',
    port: '',
    protocol: 'ModbusTCP',
    description: '',
    enabled: true
  }
}

// 生命周期钩子
onMounted(() => {
  loadDevices()
})
</script>

<style scoped>
</style>
