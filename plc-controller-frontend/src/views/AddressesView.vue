<template>
  <div class="container-fluid">
    <!-- 操作按钮和筛选器 -->
    <div class="mb-4 d-flex flex-wrap align-items-center gap-2">
      <button class="btn btn-primary" id="addAddressBtn" @click="addAddress">添加地址</button>
      <button class="btn btn-warning" id="batchUpdateDeviceBtn" @click="showBatchUpdateModal">批量修改设备</button>
      
      <!-- 设备筛选 -->
      <div class="d-flex align-items-center flex-shrink-0">
        <label for="filterDeviceSelect" class="form-label me-2 mb-0">筛选:</label>
        <select id="filterDeviceSelect" class="form-select form-select-sm w-auto" style="max-width: 250px;" v-model="filterDeviceId">
          <option value="">全部设备</option>
          <option value="null">无关联设备</option>
          <option v-for="device in devices" :key="device.id" :value="device.id">{{ device.name }} ({{ device.ip }}:{{ device.port }})</option>
        </select>
      </div>
    </div>
    
    <!-- 地址列表 -->
    <div class="card">
      <div class="card-body">
        <table class="table table-striped">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAll" @change="toggleSelectAll($event.target.checked)"></th>
              <th>ID</th>
              <th>名称</th>
              <th>地址</th>
              <th>类型</th>
              <th>数据类型</th>
              <th>关联设备</th>
              <th>按钮方案</th>
              <th>是否入库</th>
              <th>描述</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody id="addressList">
            <tr v-if="filteredAddresses.length === 0">
              <td colspan="11" class="text-center">没有符合条件的地址</td>
            </tr>
            <tr v-for="address in filteredAddresses" :key="address.id">
              <td><input type="checkbox" class="addressCheckbox" :value="address.id" v-model="selectedAddressIds"></td>
              <td>{{ address.id }}</td>
              <td>{{ address.name }}</td>
              <td>{{ address.address }}</td>
              <td>{{ address.type === 'INPUT' ? '输入' : '输出' }}</td>
              <td>{{ address.dataType === 'BOOLEAN' ? '布尔' : '数值' }}</td>
              <td>{{ address.deviceId ? `${getDeviceById(address.deviceId)?.name || ''} (${getDeviceById(address.deviceId)?.ip || ''}:${getDeviceById(address.deviceId)?.port || ''})` : '-' }}</td>
              <td>{{ address.buttonSchemeId ? `${getButtonSchemeById(address.buttonSchemeId)?.name || ''}` : '-' }}</td>
              <td>{{ address.isStoreInDb ? '是' : '否' }}</td>
              <td>{{ address.description || '-' }}</td>
              <td>
                <button class="btn btn-sm btn-primary" @click="editAddress(address)">编辑</button>
                <button class="btn btn-sm btn-danger" @click="deleteAddress(address.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  
  <!-- 添加/编辑模态框 -->
  <div class="modal fade" id="addModal" tabindex="-1" aria-labelledby="addModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="addModalLabel">{{ editingAddress.id ? '编辑PLC地址' : '添加PLC地址' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form id="addressForm">
            <input type="hidden" v-model="editingAddress.id">
            <div class="mb-3">
              <label for="name" class="form-label">名称</label>
              <input type="text" class="form-control" id="name" v-model="editingAddress.name" required>
            </div>
            <div class="mb-3">
              <label for="address" class="form-label">地址</label>
              <input type="text" class="form-control" id="address" v-model="editingAddress.address" required>
            </div>
            <div class="mb-3">
              <label class="form-label">类型</label>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="type" id="typeOutput" value="OUTPUT" v-model="editingAddress.type">
                <label class="form-check-label" for="typeOutput">
                  输出
                </label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="type" id="typeInput" value="INPUT" v-model="editingAddress.type">
                <label class="form-check-label" for="typeInput">
                  输入
                </label>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">数据类型</label>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="dataType" id="dataTypeNumber" value="NUMBER" v-model="editingAddress.dataType">
                <label class="form-check-label" for="dataTypeNumber">
                  数值
                </label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="dataType" id="dataTypeBoolean" value="BOOLEAN" v-model="editingAddress.dataType">
                <label class="form-check-label" for="dataTypeBoolean">
                  布尔
                </label>
              </div>
            </div>
            <div class="mb-3">
              <label for="deviceId" class="form-label">关联设备</label>
              <select class="form-control" id="deviceId" v-model="editingAddress.deviceId">
                <option value="">无关联设备</option>
                <option v-for="device in devices" :key="device.id" :value="device.id">{{ device.name }} ({{ device.ip }}:{{ device.port }})</option>
              </select>
            </div>
            <div class="mb-3">
              <label for="description" class="form-label">描述</label>
              <textarea class="form-control" id="description" v-model="editingAddress.description"></textarea>
            </div>
            <div class="mb-3">
              <label for="buttonSchemeId" class="form-label">按钮方案</label>
              <select class="form-control" id="buttonSchemeId" v-model="editingAddress.buttonSchemeId">
                <option value="">无方案</option>
                <option v-for="scheme in buttonSchemes" :key="scheme.id" :value="scheme.id">{{ scheme.name }}</option>
              </select>
            </div>
            <div class="mb-3">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" id="storeInDb" v-model="editingAddress.isStoreInDb">
                <label class="form-check-label" for="storeInDb">
                  是否入库
                </label>
              </div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn btn-primary" id="saveAddress" @click="saveAddress()">保存</button>
        </div>
      </div>
    </div>
  </div>

  <!-- 批量修改设备模态框 -->
  <div class="modal fade" id="batchUpdateModal" tabindex="-1" aria-labelledby="batchUpdateModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="batchUpdateModalLabel">批量修改设备</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <p class="mb-2">已选择 <strong>{{ selectedAddressIds.length }}</strong> 个地址</p>
            <label for="batchDeviceSelect" class="form-label">选择设备</label>
            <select id="batchDeviceSelect" class="form-select" v-model="selectedBatchDeviceId">
              <option value="">选择设备</option>
              <option value="null">清除设备关联</option>
              <option v-for="device in devices" :key="device.id" :value="device.id">{{ device.name }} ({{ device.ip }}:{{ device.port }})</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn btn-warning" @click="confirmBatchUpdate">确认修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import * as bootstrap from 'bootstrap'

// 响应式数据
const addresses = ref([])
const devices = ref([])
const buttonSchemes = ref([])
const selectedAddressIds = ref([])
const selectedBatchDeviceId = ref('')
const filterDeviceId = ref('')
const editingAddress = ref({
  id: null,
  name: '',
  address: '',
  type: 'OUTPUT',
  dataType: 'NUMBER',
  deviceId: '',
  buttonSchemeId: '',
  description: '',
  isStoreInDb: false
})

// 计算属性 - 筛选后的地址列表
const filteredAddresses = computed(() => {
  if (!filterDeviceId.value) {
    return addresses.value
  } else if (filterDeviceId.value === 'null') {
    return addresses.value.filter(address => !address.deviceId)
  } else {
    return addresses.value.filter(address => address.deviceId === parseInt(filterDeviceId.value))
  }
})

// 方法
const getDeviceById = (deviceId) => {
  return devices.value.find(device => device.id === deviceId)
}

const getButtonSchemeById = (schemeId) => {
  return buttonSchemes.value.find(scheme => scheme.id === schemeId)
}

const loadDevices = async () => {
  try {
    const response = await axios.get('/api/devices')
    devices.value = response.data
  } catch (error) {
    // 加载设备列表失败
  }
}

const loadButtonSchemes = async () => {
  try {
    const response = await axios.get('/api/button-schemes')
    buttonSchemes.value = response.data
  } catch (error) {
    // 加载按钮方案列表失败
  }
}

const loadAddresses = async () => {
  try {
    const response = await axios.get('/api/addresses')
    addresses.value = response.data
    console.log('Loaded addresses:', addresses.value)
  } catch (error) {
    console.error('Error loading addresses:', error)
    // 加载地址列表失败
  }
}

const addAddress = () => {
  resetEditingAddress()
  // 显示模态框
  const modal = new bootstrap.Modal(document.getElementById('addModal'))
  modal.show()
}

const editAddress = (address) => {
  console.log('Editing address:', address)
  editingAddress.value = { ...address }
  // 确保isStoreInDb字段存在
  if (editingAddress.value.isStoreInDb === undefined) {
    editingAddress.value.isStoreInDb = false
  }
  console.log('Editing address after assignment:', editingAddress.value)
  // 显示模态框
  const modal = new bootstrap.Modal(document.getElementById('addModal'))
  modal.show()
}

const saveAddress = async () => {
  try {
    const addressData = {
      name: editingAddress.value.name,
      address: editingAddress.value.address,
      type: editingAddress.value.type,
      dataType: editingAddress.value.dataType,
      deviceId: editingAddress.value.deviceId || null,
      buttonSchemeId: editingAddress.value.buttonSchemeId || null,
      description: editingAddress.value.description,
      isStoreInDb: editingAddress.value.isStoreInDb
    }
    
    let url = '/api/addresses'
    let method = 'POST'
    
    if (editingAddress.value.id) {
      url = `/api/addresses/${editingAddress.value.id}`
      method = 'PUT'
    }
    
    await axios({
      url,
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      data: addressData
    })
    
    // 关闭模态框
    const modal = bootstrap.Modal.getInstance(document.getElementById('addModal'))
    modal.hide()
    
    // 重新加载地址列表
    loadAddresses()
    
    // 重置编辑地址
    resetEditingAddress()
  } catch (error) {
    // 保存地址失败
  }
}

const deleteAddress = async (id) => {
  if (confirm('确定要删除这个地址吗？')) {
    try {
      await axios.delete(`/api/addresses/${id}`)
      loadAddresses()
    } catch (error) {
      // 删除地址失败
    }
  }
}


const resetEditingAddress = () => {
  editingAddress.value = {
    id: null,
    name: '',
    address: '4x',
    type: 'OUTPUT',
    dataType: 'NUMBER',
    deviceId: '',
    buttonSchemeId: '',
    description: '',
    isStoreInDb: false
  }
}


const toggleSelectAll = (checked) => {
  if (checked) {
    selectedAddressIds.value = addresses.value.map(address => address.id)
  } else {
    selectedAddressIds.value = []
  }
}

const showBatchUpdateModal = () => {
  if (selectedAddressIds.value.length === 0) {
    // 请先选择要修改的地址
    return
  }
  
  // 显示模态框
  const modal = new bootstrap.Modal(document.getElementById('batchUpdateModal'))
  modal.show()
}

const confirmBatchUpdate = async () => {
  if (selectedAddressIds.value.length === 0) {
    // 请先选择要修改的地址
    return
  }
  
  if (selectedBatchDeviceId.value === '') {
    // 请选择要修改的设备
    return
  }
  
  // 转换设备ID为null或数值
  const deviceId = selectedBatchDeviceId.value === 'null' ? null : parseInt(selectedBatchDeviceId.value)
  
  try {
    await axios.put('/api/addresses/batch/device', {
      addressIds: selectedAddressIds.value,
      deviceId: deviceId
    })
    
    // 批量修改成功
    loadAddresses()
    
    // 重置选择
    selectedAddressIds.value = []
    selectedBatchDeviceId.value = ''
    
    // 关闭模态框
    const modal = bootstrap.Modal.getInstance(document.getElementById('batchUpdateModal'))
    modal.hide()
  } catch (error) {
    // 批量修改失败
  }
}

// 生命周期钩子
onMounted(() => {
  loadDevices()
  loadAddresses()
  loadButtonSchemes()
})
</script>

<style scoped>
</style>
