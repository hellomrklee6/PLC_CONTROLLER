<template>
  <BaseCard
    :data="data"
    :deviceStatusClass="deviceStatusClass"
    :getDeviceById="getDeviceById"
    :getDeviceNameById="getDeviceNameById"
    :deviceStatusText="deviceStatusText"
    :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
    :buttonScheme="buttonScheme"
    cardType="output"
    dataType="boolean"
    @buttonClick="handleButtonClick"
  >
    <!-- 移除重复的布尔值显示，只保留右上角的显示 -->
  </BaseCard>
</template>

<script setup>
// 导入BaseCard组件
import BaseCard from './BaseCard.vue'

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
const emit = defineEmits(['buttonClick'])

// 传递按钮点击事件
const handleButtonClick = (event) => {
  emit('buttonClick', event)
}
</script>