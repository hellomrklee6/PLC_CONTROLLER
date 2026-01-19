<template>
  <div class="p-3 border rounded shadow-sm" :class="{ 
    'text-muted opacity-75 bg-light': !isAddressDeviceEnabledAndConnected(data),
    'value-change-flash': isFlashing 
  }" style="flex: 0 0 280px; max-width: 380px; position: relative;">
    <!-- 当前值显示在右上角 -->
    <span class="current-value-badge badge font-weight-bold p-2 position-absolute" 
          :class="valueClass"
          style="top: 8px; right: 8px; z-index: 100; margin: 0;">
      {{ formattedValue }}
    </span>
    
    <!-- 标题行：名称 -->
    <h5 class="mb-2 font-weight-bold">{{ data.name }}</h5>
    
    <!-- 设备状态和只读读写类型行 -->
    <div class="d-flex justify-content-between align-items-center mb-2" style="font-size: 0.8rem;">
      <!-- 所属设备 -->
      <span v-if="data.deviceId" :id="`device-status-${data.deviceId}`" :class="deviceStatusClass(getDeviceById(data.deviceId))">
        {{ getDeviceNameById(data.deviceId) }} ({{ deviceStatusText(getDeviceById(data.deviceId)) }})
      </span>
      <span v-else class="badge bg-secondary" style="font-size: 0.7rem;">无设备</span>
      
      <!-- 只读读写 -->
      <span class="badge" :class="typeBadgeClass" style="font-size: 0.7rem;">{{ typeText }}</span>
    </div>
    
    <!-- 地址信息 -->
    <p class="mb-1 small">地址：{{ data.address }}</p>
    
    <!-- 数据类型信息 -->
    <p class="mb-1 small">数据类型：{{ dataTypeText }}</p>
    
    <!-- 备注信息 -->
    <p v-if="data.description" class="mb-2 small">备注：{{ data.description }}</p>
    
    <!-- 插槽：用于子组件添加特定的操作按钮或控制元素 -->
    <slot></slot>
    
    <!-- 按钮方案按钮 -->
    <div v-if="buttonScheme && buttonScheme.buttons && buttonScheme.buttons.length > 0" class="mt-3">
      <div class="d-flex flex-wrap gap-1">
        <button 
          v-for="button in buttonScheme.buttons" 
          :key="button.id || button.name"
          class="btn btn-sm btn-outline-primary"
          :disabled="!isAddressDeviceEnabledAndConnected || !isAddressDeviceEnabledAndConnected(data)"
          @click="handleButtonClick(button)"
        >
          {{ button.name }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

// 定义props
const props = defineProps({
  data: Object,
  deviceStatusClass: Function,
  getDeviceById: Function,
  getDeviceNameById: Function,
  deviceStatusText: Function,
  isAddressDeviceEnabledAndConnected: Function,
  // 卡片类型：input/output
  cardType: String,
  // 数据类型：boolean/number
  dataType: String,
  // 按钮方案
  buttonScheme: Object
})

// 定义事件
const emit = defineEmits(['buttonClick'])

// 闪烁效果状态
const isFlashing = ref(false)

// 记录上一个值
const previousValue = ref(props.data.value)

// 记录闪烁的定时器ID
let flashTimer = null

// 监听值变化，触发闪烁效果
watch(() => props.data.value, (newValue, oldValue) => {
  // 只有当值真正变化时才触发闪烁
  if (newValue !== oldValue) {
    // 清除现有的定时器
    if (flashTimer) {
      clearTimeout(flashTimer)
      flashTimer = null
    }
    
    // 立即触发闪烁
    isFlashing.value = true
    
    // 动画持续时间为0.25秒
    flashTimer = setTimeout(() => {
      isFlashing.value = false
      previousValue.value = newValue
      flashTimer = null
    }, 250)
  }
})

// 计算属性：类型标签类名
const typeBadgeClass = computed(() => {
  return props.cardType === 'input' ? 'bg-primary' : 'bg-warning'
})

// 计算属性：类型文本
const typeText = computed(() => {
  return props.cardType === 'input' ? '只读' : '读写'
})

// 计算属性：数据类型文本
const dataTypeText = computed(() => {
  return props.dataType === 'boolean' ? '布尔' : '数值'
})

// 计算属性：格式化后的值
const formattedValue = computed(() => {
  // 检查设备是否已连接且已启用，或者值是否有效
  if (!props.isAddressDeviceEnabledAndConnected || !props.isAddressDeviceEnabledAndConnected(props.data) || props.data.value === undefined || props.data.value === null) {
    return 'N/A'
  }
  
  if (props.dataType === 'boolean') {
    return props.data.value ? 'T' : 'F'
  } else {
    return props.data.value || 0
  }
})

// 计算属性：值的样式类
const valueClass = computed(() => {
  // 检查设备是否已连接且已启用
  if (!props.isAddressDeviceEnabledAndConnected || !props.isAddressDeviceEnabledAndConnected(props.data)) {
    return 'bg-secondary'
  }
  
  if (props.dataType === 'boolean') {
    return props.data.value ? 'bg-success' : 'bg-danger'
  } else {
    return 'bg-info'
  }
})

// 处理按钮点击
const handleButtonClick = (button) => {
  emit('buttonClick', {
    addressId: props.data.id,
    buttonValue: button.buttonValue,
    buttonName: button.name
  })
}
</script>

<style scoped>
/* 自定义当前值徽章样式，使其更明显 */
.current-value-badge {
  font-size: 1.1rem;
  font-weight: bold;
  padding: 0.5rem 0.8rem;
}

/* 值变化闪烁动画 */
@keyframes valueChangeFlash {
  0% { background-color: rgba(255, 255, 255, 1); }
  50% { background-color: rgba(76, 175, 80, 0.4); }
  100% { background-color: rgba(255, 255, 255, 1); }
}

/* 闪烁效果类 */
.value-change-flash {
  animation: valueChangeFlash 0.25s ease-out;
}

</style>