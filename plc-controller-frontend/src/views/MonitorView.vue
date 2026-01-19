<template>
  <div class="container-fluid">
    <!-- 监控控制列表 -->
    <div class="mb-4">
      <!-- 第一级页签：PLC设备 -->
      <ul class="nav nav-tabs" id="deviceTabs" role="tablist">
        <li class="nav-item" role="presentation">
          <button class="nav-link active" id="all-devices-tab" data-bs-toggle="tab" data-bs-target="#all-devices-content" type="button" role="tab" aria-controls="all-devices-content" aria-selected="true">全部卡片</button>
        </li>
        <!-- 其他PLC设备页签将通过JavaScript动态生成 -->
        <li v-for="device in devices" :key="device.id" class="nav-item" role="presentation">
          <button class="nav-link" :id="`device-${device.id}-tab`" data-bs-toggle="tab" :data-bs-target="`#device-${device.id}-content`" type="button" role="tab" :aria-controls="`device-${device.id}-content`" aria-selected="false">
            {{ device.name }} <span :id="`status-${device.id}`" :class="deviceStatusClass(device)">{{ deviceStatusText(device) }}</span>
          </button>
        </li>
      </ul>
      
      <!-- 第一级页签内容 -->
      <div class="tab-content" id="deviceTabsContent">
        <div class="tab-pane fade show active" id="all-devices-content" role="tabpanel" aria-labelledby="all-devices-tab">
          <!-- 第二级页签：数据类型 -->
          <ul class="nav nav-tabs mt-3" id="dataTabs" role="tablist">
            <li class="nav-item" role="presentation">
              <button class="nav-link active" id="all-tab" data-bs-toggle="tab" data-bs-target="#all-content" type="button" role="tab" aria-controls="all-content" aria-selected="true">全部</button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="input-tab" data-bs-toggle="tab" data-bs-target="#input-content" type="button" role="tab" aria-controls="input-content" aria-selected="false">只读地址</button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="output-tab" data-bs-toggle="tab" data-bs-target="#output-content" type="button" role="tab" aria-controls="output-content" aria-selected="false">读写地址</button>
            </li>
          </ul>
          
          <!-- Tab内容 -->
          <div class="tab-content mt-3" id="dataTabsContent">
            <!-- 全部 -->
            <div class="tab-pane fade show active" id="all-content" role="tabpanel" aria-labelledby="all-tab">
              <div class="card">
                <div class="card-body">
                  <div id="allList">
                    <div class="d-flex flex-wrap gap-3">

                      <!-- 布尔类型输入卡片 -->
                      <BooleanInputCard v-for="data in allData.filter(d => d.dataType === 'BOOLEAN' && d.type === 'INPUT').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />

                      
                      <!-- 布尔类型输出卡片 -->
                      <BooleanOutputCard v-for="data in allData.filter(d => d.dataType === 'BOOLEAN' && d.type === 'OUTPUT').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输入卡片 -->
                      <NumberInputCard v-for="data in allData.filter(d => d.dataType === 'NUMBER' && d.type === 'INPUT').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输出卡片 -->
                      <NumberOutputCard v-for="data in allData.filter(d => d.dataType === 'NUMBER' && d.type === 'OUTPUT').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                        @setNumber="setNumber"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 只读地址 -->
            <div class="tab-pane fade" id="input-content" role="tabpanel" aria-labelledby="input-tab">
              <div class="card">
                <div class="card-body">
                  <div id="inputList">
                    <div class="d-flex flex-wrap gap-3">
                      <!-- 布尔类型输入卡片 -->
                      <BooleanInputCard v-for="data in inputData.filter(d => d.dataType === 'BOOLEAN').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输入卡片 -->
                      <NumberInputCard v-for="data in inputData.filter(d => d.dataType === 'NUMBER').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 读写地址 -->
            <div class="tab-pane fade" id="output-content" role="tabpanel" aria-labelledby="output-tab">
              <div class="card">
                <div class="card-body">
                  <div id="outputList">
                    <div class="d-flex flex-wrap gap-3">
                      <!-- 布尔类型输出卡片 -->
                      <BooleanOutputCard v-for="data in outputData.filter(d => d.dataType === 'BOOLEAN').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输出卡片 -->
                      <NumberOutputCard v-for="data in outputData.filter(d => d.dataType === 'NUMBER').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                        @setNumber="setNumber"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 其他PLC设备的页签内容将通过JavaScript动态生成 -->
        <div v-for="device in devices" :key="device.id" class="tab-pane fade" :id="`device-${device.id}-content`" role="tabpanel" :aria-labelledby="`device-${device.id}-tab`">
          <!-- 第二级页签：数据类型 -->
          <ul class="nav nav-tabs mt-3" :id="`dataTabs-${device.id}`" role="tablist">
            <li class="nav-item" role="presentation">
              <button class="nav-link active" :id="`all-${device.id}-tab`" data-bs-toggle="tab" :data-bs-target="`#all-${device.id}-content`" type="button" role="tab" :aria-controls="`all-${device.id}-content`" aria-selected="true">全部</button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" :id="`input-${device.id}-tab`" data-bs-toggle="tab" :data-bs-target="`#input-${device.id}-content`" type="button" role="tab" :aria-controls="`input-${device.id}-content`" aria-selected="false">只读地址</button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" :id="`output-${device.id}-tab`" data-bs-toggle="tab" :data-bs-target="`#output-${device.id}-content`" type="button" role="tab" :aria-controls="`output-${device.id}-content`" aria-selected="false">读写地址</button>
            </li>
          </ul>
          
          <!-- 第二级页签内容 -->
          <div class="tab-content mt-3" :id="`dataTabsContent-${device.id}`">
            <!-- 全部 -->
            <div class="tab-pane fade show active" :id="`all-${device.id}-content`" role="tabpanel" :aria-labelledby="`all-${device.id}-tab`">
              <div class="card">
                <div class="card-body">
                  <div :id="`allList-${device.id}`">
                    <div class="d-flex flex-wrap gap-3">
                      <!-- 布尔类型输入卡片 -->
                      <BooleanInputCard v-for="data in getDeviceData(device.id).filter(d => d.dataType === 'BOOLEAN' && d.type === 'INPUT').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 布尔类型输出卡片 -->
                      <BooleanOutputCard v-for="data in getDeviceData(device.id).filter(d => d.dataType === 'BOOLEAN' && d.type === 'OUTPUT').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输入卡片 -->
                      <NumberInputCard v-for="data in getDeviceData(device.id).filter(d => d.dataType === 'NUMBER' && d.type === 'INPUT').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输出卡片 -->
                      <NumberOutputCard v-for="data in getDeviceData(device.id).filter(d => d.dataType === 'NUMBER' && d.type === 'OUTPUT').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                        @setNumber="setNumber"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 只读地址 -->
            <div class="tab-pane fade" :id="`input-${device.id}-content`" role="tabpanel" :aria-labelledby="`input-${device.id}-tab`">
              <div class="card">
                <div class="card-body">
                  <div :id="`inputList-${device.id}`">
                    <div class="d-flex flex-wrap gap-3">
                      <!-- 布尔类型输入卡片 -->
                      <BooleanInputCard v-for="data in getDeviceInputData(device.id).filter(d => d.dataType === 'BOOLEAN').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输入卡片 -->
                      <NumberInputCard v-for="data in getDeviceInputData(device.id).filter(d => d.dataType === 'NUMBER').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 读写地址 -->
            <div class="tab-pane fade" :id="`output-${device.id}-content`" role="tabpanel" :aria-labelledby="`output-${device.id}-tab`">
              <div class="card">
                <div class="card-body">
                  <div :id="`outputList-${device.id}`">
                    <div class="d-flex flex-wrap gap-3">
                      <!-- 布尔类型输出卡片 -->
                      <BooleanOutputCard v-for="data in getDeviceOutputData(device.id).filter(d => d.dataType === 'BOOLEAN').sort((a, b) => a.id - b.id)" :key="data.id" 
                        :data="data" 
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                      />
                      
                      <!-- 数值类型输出卡片 -->
                      <NumberOutputCard v-for="data in getDeviceOutputData(device.id).filter(d => d.dataType === 'NUMBER').sort((a, b) => a.id - b.id)" :key="data.id"
                        :data="data"
                        :deviceStatusClass="deviceStatusClass"
                        :getDeviceById="getDeviceById"
                        :getDeviceNameById="getDeviceNameById"
                        :deviceStatusText="deviceStatusText"
                        :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
                        :buttonScheme="getButtonSchemeById(data.buttonSchemeId)"
                        @buttonClick="handleButtonClick"
                        @setNumber="setNumber"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- PLC连接日志 -->
    <div class="mb-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h6 class="mb-0">PLC连接日志</h6>
          <button id="scrollToggleBtn" class="btn btn-sm" :class="{ 'btn-outline-secondary': isAutoScrollEnabled, 'btn-primary': !isAutoScrollEnabled }" @click="toggleAutoScroll()">{{ isAutoScrollEnabled ? '锁定滚动' : '自动滚动' }}</button>
        </div>
        <div class="card-body" style="max-height: 200px; overflow-y: auto;" ref="logContainer">
          <div id="connectionLog" class="font-monospace small">
            <div v-for="(log, index) in connectionLogs" :key="index" class="mb-1">{{ log }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import axios from 'axios'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
// 导入卡片组件
import BooleanInputCard from '../components/card/BooleanInputCard.vue'
import BooleanOutputCard from '../components/card/BooleanOutputCard.vue'
import NumberInputCard from '../components/card/NumberInputCard.vue'
import NumberOutputCard from '../components/card/NumberOutputCard.vue'

// 响应式数据
const devices = ref([])
const plcData = ref([])
const buttonSchemes = ref([])
const connectionLogs = ref([])
const isAutoScrollEnabled = ref(true)
const logContainer = ref(null)
const stompClient = ref(null)
const wsConnected = ref(false)

// 计算属性
const allData = computed(() => plcData.value)
const inputData = computed(() => plcData.value.filter(data => data.type === 'INPUT'))
const outputData = computed(() => plcData.value.filter(data => data.type === 'OUTPUT'))

// 方法
const deviceStatusClass = (device) => {
  if (!device.enabled) {
    return 'badge bg-secondary'
  }
  return device.connected ? 'badge bg-success' : 'badge bg-danger'
}

const isAddressDeviceEnabledAndConnected = (address) => {
  if (!address.deviceId) return false // 没有关联设备的地址也视为禁用
  const device = devices.value.find(d => d.id === address.deviceId)
  return device ? (device.enabled && device.connected) : false
}

const getDeviceById = (deviceId) => {
  return devices.value.find(d => d.id === deviceId) || { enabled: false, connected: false }
}

const getDeviceNameById = (deviceId) => {
  const device = getDeviceById(deviceId)
  return device ? device.name : '未知设备'
}

const deviceStatusText = (device) => {
  if (!device.enabled) {
    return '禁用'
  }
  return device.connected ? '在线' : '掉线'
}

const getDeviceData = (deviceId) => {
  return plcData.value.filter(data => data.deviceId === deviceId)
}

const getDeviceInputData = (deviceId) => {
  return plcData.value.filter(data => data.deviceId === deviceId && data.type === 'INPUT')
}

const getDeviceOutputData = (deviceId) => {
  return plcData.value.filter(data => data.deviceId === deviceId && data.type === 'OUTPUT')
}

const loadPlcDevices = async () => {
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
    // 加载PLC设备列表失败
  }
}

const loadPlcData = async () => {
  try {
    const addressesResponse = await axios.get('/api/addresses')
    const addresses = addressesResponse.data
    
    const dataPromises = addresses.map(async (address) => {
      try {
        const valueResponse = await axios.get(`/api/control/${address.id}`)
        return { ...address, value: valueResponse.data }
      } catch (error) {
        return { ...address, value: 'N/A' }
      }
    })
    
    const results = await Promise.all(dataPromises)
    plcData.value = results.sort((a, b) => a.id - b.id)
  } catch (error) {
    // 加载PLC数据失败
  }
}

const loadButtonSchemes = async () => {
  try {
    const response = await axios.get('/api/button-schemes')
    const schemes = response.data
    
    // 为每个方案加载按钮
    for (const scheme of schemes) {
      try {
        const buttonsResponse = await axios.get(`/api/buttons/scheme/${scheme.id}`)
        scheme.buttons = buttonsResponse.data
      } catch (error) {
        console.error(`加载方案 ${scheme.name} 的按钮失败:`, error)
        scheme.buttons = []
      }
    }
    
    buttonSchemes.value = schemes
  } catch (error) {
    // 加载按钮方案失败
  }
}

const getButtonSchemeById = (schemeId) => {
  if (!schemeId) return null
  return buttonSchemes.value.find(scheme => scheme.id === schemeId)
}


const connectWebSocket = () => {
  try {
    const socket = new SockJS('/ws-plc')
    stompClient.value = Stomp.over(socket)
    
    // 禁用STOMP客户端调试日志
    stompClient.value.debug = null
    
    // 确保socket对象正确初始化
    if (socket) {
      stompClient.value.connect({}, (frame) => {
        // WebSocket连接成功
        wsConnected.value = true
        
        // 订阅PLC数据主题
        stompClient.value.subscribe('/topic/plc/data', (data) => {
          try {
            const plcDataMap = JSON.parse(data.body)
            updatePlcDataDisplay(plcDataMap)
          } catch (error) {
            // 解析数据失败
          }
        })
        
        // 订阅PLC连接日志主题
        stompClient.value.subscribe('/topic/plc/connection-log', (log) => {
          try {
            const logContent = log.body
            addConnectionLog(logContent)
          } catch (error) {
            // 处理日志失败
          }
        })
        
        // 订阅PLC设备状态主题
        stompClient.value.subscribe('/topic/plc/device-status', (status) => {
          try {
            const deviceStatus = JSON.parse(status.body)
            updateDeviceStatus(deviceStatus)
          } catch (error) {
            // 解析设备状态失败
          }
        })
      }, (error) => {
        wsConnected.value = false
        // WebSocket连接失败
        
        // 连接失败后，尝试5秒后重连
        setTimeout(connectWebSocket, 5000)
      })
    }
  } catch (error) {
    // WebSocket初始化失败
    wsConnected.value = false
    setTimeout(connectWebSocket, 5000)
  }
}

const disconnectWebSocket = () => {
  if (stompClient.value !== null) {
    stompClient.value.disconnect()
    wsConnected.value = false
  }
}

const updatePlcDataDisplay = (plcDataMap) => {
  // 这里需要根据实际的plcDataMap结构来更新plcData
  // 假设plcDataMap是一个对象，键是地址ID，值是数据
  for (const [id, data] of Object.entries(plcDataMap)) {
    const index = plcData.value.findIndex(item => item.id === parseInt(id))
    if (index !== -1) {
      plcData.value[index].value = data.value
    }
  }
}

const addConnectionLog = (log) => {
  // 过滤掉设备连接状态检查日志
  if (log.includes('[设备管理] 执行设备连接状态检查')) {
    return
  }
  
  connectionLogs.value.push(log)

  // 限制日志数量，最多显示200条
  if (connectionLogs.value.length > 200) {
    connectionLogs.value.shift()
  }

  // 根据设置决定是否自动滚动到底部
  if (isAutoScrollEnabled.value && logContainer.value) {
    setTimeout(() => {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }, 0)
  }
}

const toggleAutoScroll = () => {
  isAutoScrollEnabled.value = !isAutoScrollEnabled.value
  
  // 切换为自动滚动时，立即滚动到底部
  if (isAutoScrollEnabled.value && logContainer.value) {
    logContainer.value.scrollTop = logContainer.value.scrollHeight
  }
}
const toggleBoolean = async (addressId, value) => {
  try {
    // 将布尔值转换为数字：true -> 1, false -> 0
    const numericValue = value ? 1 : 0
    const response = await axios.post(`/api/control/${addressId}`, { value: numericValue })
    if (response.data) {
      loadPlcData()
    } else {
      // 设置失败
    }
  } catch (error) {
    // 设置失败
  }
}
// 更新设备状态
const updateDeviceStatus = (deviceStatus) => {
  const { deviceId, connected } = deviceStatus
  
  // 找到对应的设备并更新状态
  const deviceIndex = devices.value.findIndex(device => device.id === deviceId)
  if (deviceIndex !== -1) {
    devices.value[deviceIndex].connected = connected
    
    // 可以添加状态变化的日志记录
    addConnectionLog(`[设备状态变化] 设备ID=${deviceId}，连接状态=${connected ? '在线' : '掉线'}`)
  }
}


const setNumber = async (addressId, value) => {
  const numValue = parseInt(value)
  if (isNaN(numValue)) {
    // 请输入有效的数值
    return
  }
  
  try {
    const response = await axios.post(`/api/control/${addressId}`, { value: numValue })
    if (response.data) {
      loadPlcData()
    } else {
      // 设置失败
    }
  } catch (error) {
    // 设置失败
  }
}


const handleButtonClick = async (event) => {
  const { addressId, buttonValue } = event
  
  try {
    // 尝试将按钮值转换为适当的类型
    let value
    if (buttonValue === 'true' || buttonValue === 'false') {
      // 将布尔值转换为数字：true -> 1, false -> 0
      value = buttonValue === 'true' ? 1 : 0
    } else {
      value = parseInt(buttonValue)
      if (isNaN(value)) {
        // 无效的按钮值
        return
      }
    }
    
    const response = await axios.post(`/api/control/${addressId}`, { value })
    if (response.data) {
      loadPlcData()
    }
  } catch (error) {
    // 按钮点击失败
  }
}



// 生命周期钩子
onMounted(() => {
  // 加载按钮方案（先加载，确保数据显示时方案已就绪）
  loadButtonSchemes()
  
  // 加载PLC设备列表
  loadPlcDevices()
  
  // 加载PLC数据
  loadPlcData()
  
  // 连接WebSocket
  connectWebSocket()
  
  // 组件卸载时清理
  onUnmounted(() => {
    disconnectWebSocket()
  })
})
</script>

<style scoped>
</style>
