<template>
  <BaseCard
    :data="data"
    :deviceStatusClass="deviceStatusClass"
    :getDeviceById="getDeviceById"
    :getDeviceNameById="getDeviceNameById"
    :deviceStatusText="deviceStatusText"
    :isAddressDeviceEnabledAndConnected="isAddressDeviceEnabledAndConnected"
    :buttonScheme="buttonScheme"
    cardType="input"
    dataType="boolean"
    @buttonClick="handleButtonClick"
  >
    <!-- BooleanInputCard没有额外的操作按钮 -->
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