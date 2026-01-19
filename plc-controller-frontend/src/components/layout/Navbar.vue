<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light mb-0">
    <div class="container-fluid">
      <router-link class="navbar-brand" to="/">PLC控制系统</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'monitor' }" to="/monitor">监控管理</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'devices' }" to="/devices">设备管理</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'buttonSchemes' }" to="/button-schemes">按钮方案</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'addresses' }" to="/addresses">地址管理</router-link>
          </li>
        </ul>
        <div class="ms-auto">
          <span class="navbar-text">{{ currentTime }}</span>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const currentTime = ref('')

const updateCurrentTime = () => {
  const now = new Date()
  const timeString = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
  currentTime.value = timeString
}

let timeInterval = null

onMounted(() => {
  updateCurrentTime()
  timeInterval = setInterval(updateCurrentTime, 1000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.nav-link.active {
  font-weight: bold;
  color: #0d6efd !important;
}
</style>
