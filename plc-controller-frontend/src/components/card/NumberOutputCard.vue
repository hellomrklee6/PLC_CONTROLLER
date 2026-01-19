<template>
  <BaseCard
    :data="data"
    :deviceStatusClass="deviceStatusClass"
    :getDeviceById="getDeviceById"
    :getDeviceNameById="getDeviceNameById"
    :deviceStatusText="deviceStatusText"
    :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
    cardType="output"
    dataType="number"
    :buttonScheme="buttonScheme"
    @buttonClick="handleButtonClick"
  >
    <!-- 数值设置输入框和按钮 -->
    <div class="input-group mb-2">
      <input 
        type="number" 
        class="form-control form-control-sm" 
        :id="`input_${data.id}`" 
        v-model="localValue" 
        :disabled="!isAddressDeviceEnabledAndConnected(data)"
        @focus="isEditing = true"
        @blur="isEditing = false"
        @keydown="isEditing = true"
      >
      <button 
        class="btn btn-primary btn-sm" 
        @click="setNumber(data.id, localValue)" 
        :disabled="!isAddressDeviceEnabledAndConnected(data)"
      >
        设置
      </button>
    </div>
  </BaseCard>
</template>

<script setup>
// 导入BaseCard组件
import BaseCard from './BaseCard.vue'
import { ref, watch } from 'vue'

// 定义props
const props = defineProps({
  data: Object,
  deviceStatusClass: Function,
  getDeviceById: Function,
  getDeviceNameById: Function,
  deviceStatusText: Function,
  isAddressDeviceEnabledAndConnected: Function,
  buttonScheme: Object
})

// 定义事件
const emit = defineEmits(['setNumber', 'buttonClick'])

// 本地值存储，避免WebSocket数据更新覆盖用户输入
const localValue = ref(props.data.value || 0)
// 编辑状态标记
const isEditing = ref(false)

// 监听data.value变化，更新本地值（仅在用户未编辑时）
watch(() => props.data.value, (newValue) => {
  // 只有当用户未编辑且本地值与新值不同时才更新
  if (!isEditing.value && localValue.value !== (newValue || 0)) {
    localValue.value = newValue || 0
  }
})

// 传递事件
const setNumber = (id, value) => {
  emit('setNumber', id, value)
}

// 传递按钮点击事件
const handleButtonClick = (event) => {
  emit('buttonClick', event)
}
</script>