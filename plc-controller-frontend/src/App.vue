<template>
  <div class="app-container">
    <Navbar />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
    
    <!-- 后端未连接遮罩层 -->
    <div v-if="!backendConnected" class="backend-offline-overlay">
      <div class="backend-offline-message">
        <div class="offline-icon">⚠️</div>
        <h2>后端未连接</h2>
        <p>正在尝试重新连接...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import Navbar from './components/layout/Navbar.vue'
import Footer from './components/layout/Footer.vue'

// 后端连接状态
const backendConnected = ref(true)
// 连接检查定时器
let checkConnectionTimer = null

// 检查后端连接状态
const checkBackendConnection = () => {
  axios.get('/api/devices')
    .then(() => {
      backendConnected.value = true
    })
    .catch(() => {
      backendConnected.value = false
    })
}

// 定时检查后端连接状态（每3秒一次）
onMounted(() => {
  // 立即检查一次
  checkBackendConnection()
  // 然后每3秒检查一次
  checkConnectionTimer = setInterval(checkBackendConnection, 3000)
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (checkConnectionTimer) {
    clearInterval(checkConnectionTimer)
    checkConnectionTimer = null
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

/* 后端未连接遮罩层样式 */
.backend-offline-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(3px);
}

.backend-offline-message {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
}

.offline-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.backend-offline-message h2 {
  color: #dc3545;
  margin-bottom: 10px;
  font-size: 1.8rem;
}

.backend-offline-message p {
  color: #6c757d;
  font-size: 1.1rem;
}
</style>
