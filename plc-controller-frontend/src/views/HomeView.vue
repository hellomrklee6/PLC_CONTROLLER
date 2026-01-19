<template>
  <div>
    <!-- 首页横幅 -->
    <div class="jumbotron text-center">
      <div class="container-fluid">
        <h1>PLC控制系统</h1>
        <p>欢迎使用PLC控制系统，选择以下功能模块开始操作</p>
      </div>
    </div>
    
    <!-- 功能导航卡片 -->
    <div class="container-fluid">
      <div class="row g-4">
        <!-- 监控管理 -->
        <div class="col-md-6 col-lg-3">
          <div class="card feature-card">
            <div class="card-body text-center">
              <i class="bi bi-speedometer2 feature-icon"></i>
              <h5 class="card-title">监控管理</h5>
              <p class="card-text">实时监控PLC设备状态，查看输入输出数据，进行远程控制操作。</p>
              <router-link to="/monitor" class="btn btn-primary btn-lg">进入</router-link>
            </div>
          </div>
        </div>
        
        <!-- 设备管理 -->
        <div class="col-md-6 col-lg-3">
          <div class="card feature-card">
            <div class="card-body text-center">
              <i class="bi bi-router feature-icon"></i>
              <h5 class="card-title">设备管理</h5>
              <p class="card-text">管理PLC设备列表，配置设备参数，监控设备连接状态。</p>
              <router-link to="/devices" class="btn btn-primary btn-lg">进入</router-link>
            </div>
          </div>
        </div>
        
        <!-- 地址管理 -->
        <div class="col-md-6 col-lg-3">
          <div class="card feature-card">
            <div class="card-body text-center">
              <i class="bi bi-geo-alt feature-icon"></i>
              <h5 class="card-title">地址管理</h5>
              <p class="card-text">配置PLC地址信息，设置数据类型，批量管理地址参数。</p>
              <router-link to="/addresses" class="btn btn-primary btn-lg">进入</router-link>
            </div>
          </div>
        </div>
        
        <!-- 按钮方案 -->
        <div class="col-md-6 col-lg-3">
          <div class="card feature-card">
            <div class="card-body text-center">
              <i class="bi bi-grid-3x3 feature-icon"></i>
              <h5 class="card-title">按钮方案</h5>
              <p class="card-text">创建和管理按钮方案，为PLC地址配置快捷操作按钮。</p>
              <router-link to="/button-schemes" class="btn btn-primary btn-lg">进入</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.feature-card {
  transition: transform 0.3s, box-shadow 0.3s;
  border: none;
  border-radius: 15px;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  color: #0d6efd;
}

.card-body {
  padding: 2rem;
}

.jumbotron {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 0;
  margin-bottom: 2rem;
}

.jumbotron h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.jumbotron p {
  font-size: 1.2rem;
  opacity: 0.9;
}
</style>
