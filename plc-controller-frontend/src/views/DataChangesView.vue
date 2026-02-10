<template>
  <div class="container">
    <h1 class="mt-4 mb-4">数据变更记录</h1>
    
    <!-- 筛选条件 -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row">
          <div class="col-md-4">
            <div class="form-group">
              <label for="addressId">地址</label>
              <select class="form-control" id="addressId" v-model="filter.addressId">
                <option value="">全部地址</option>
                <option v-for="address in addresses" :key="address.id" :value="address.id">{{ address.name }} ({{ address.address }})</option>
              </select>
            </div>
          </div>
          <div class="col-md-4">
            <div class="form-group">
              <label for="startTime">开始时间</label>
              <input type="datetime-local" class="form-control" id="startTime" v-model="filter.startTime">
            </div>
          </div>
          <div class="col-md-4">
            <div class="form-group">
              <label for="endTime">结束时间</label>
              <input type="datetime-local" class="form-control" id="endTime" v-model="filter.endTime">
            </div>
          </div>
        </div>
        <div class="row mt-3">
          <div class="col-md-12">
            <button class="btn btn-primary mr-2" @click="search">查询</button>
            <button class="btn btn-secondary" @click="resetFilter">重置</button>
          </div>
        </div>
      </div>
    </div>
    

    <!-- 变更记录列表 -->
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-striped table-bordered">
            <thead>
              <tr>
                <th>地址名称</th>
                <th>地址</th>
                <th>旧值</th>
                <th>新值</th>
                <th>变更时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(log, index) in changeLogs" :key="log.id">
                <td>{{ log.addressName }}</td>
                <td>{{ log.address }}</td>
                <td>{{ log.oldValue }}</td>
                <td>{{ log.newValue }}</td>
                <td>{{ formatDateTime(log.changeTime) }}</td>
              </tr>
              <tr v-if="changeLogs.length === 0">
                <td colspan="5" class="text-center">暂无变更记录</td>
              </tr>
              <tr v-if="changeLogs.length > 0">
                <td colspan="5" class="text-center">共 {{ changeLogs.length }} 条记录</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- 分页控件 -->
        <div class="mt-4 d-flex justify-content-between align-items-center">
          <div class="text-muted">
            共 {{ total }} 条记录
          </div>
          <nav>
            <ul class="pagination">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="changePage(1)">首页</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="changePage(currentPage - 1)">上一页</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="changePage(currentPage + 1)">下一页</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="changePage(totalPages)">末页</button>
              </li>
            </ul>
          </nav>
          <div class="d-flex align-items-center">
            <span class="mr-2">每页显示：</span>
            <select class="form-control form-control-sm" v-model="pageSize" @change="changePageSize">
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
              <option value="100">100</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DataChangesView',
  data() {
    return {
      changeLogs: [],
      addresses: [],
      filter: {
        addressId: '',
        startTime: '',
        endTime: ''
      },
      // 分页相关
      currentPage: 1,
      pageSize: 10,
      total: 0,
      totalPages: 1
    }
  },
  mounted() {
    this.setDefaultTimeRange();
    this.loadAddresses();
    // 延迟搜索，确保地址加载完成
    setTimeout(() => {
      this.search();
    }, 500);
  },
  methods: {
    // 设置默认时间范围为今天
    setDefaultTimeRange() {
      const today = new Date();
      // 开始时间：今天的 00:00:00
      const startTime = new Date(today);
      startTime.setHours(0, 0, 0, 0);
      // 结束时间：今天的 23:59:59
      const endTime = new Date(today);
      endTime.setHours(23, 59, 59, 999);
      
      // 格式化为 datetime-local 格式
      this.filter.startTime = this.formatDateTimeLocal(startTime);
      this.filter.endTime = this.formatDateTimeLocal(endTime);
    },
    
    // 格式化日期为 datetime-local 格式
    formatDateTimeLocal(date) {
      return date.toISOString().slice(0, 16);
    },
    
    // 加载地址列表
    async loadAddresses() {
      try {
        const response = await fetch('/api/addresses');
        if (response.ok) {
          const data = await response.json();
          this.addresses = data;
        } else {
          console.error('Failed to load addresses:', response.statusText);
        }
      } catch (error) {
        console.error('Error loading addresses:', error);
      }
    },
    
    // 搜索变更记录
    async search() {
      try {
        // 暂时简化为直接调用基础API，不使用时间范围过滤
        let url = `/api/changelogs?page=${this.currentPage}&size=${this.pageSize}`;
        
        const response = await fetch(url);
        
        if (response.ok) {
          const text = await response.text();
          const result = JSON.parse(text);
          
          // 假设后端返回的是分页数据，包含 content 和 totalElements 字段
          if (result.content) {
            this.changeLogs = result.content;
            this.total = result.totalElements;
            this.totalPages = Math.ceil(this.total / this.pageSize);
          } else {
            // 兼容旧的返回格式
            this.changeLogs = result;
            this.total = result.length;
            this.totalPages = Math.ceil(this.total / this.pageSize);
          }
        } else {
          console.error('Failed to search change logs:', response.statusText);
        }
      } catch (error) {
        console.error('Error searching change logs:', error);
      }
    },
    
    // 重置筛选条件
    resetFilter() {
      this.filter = {
        addressId: '',
        startTime: '',
        endTime: ''
      };
      this.setDefaultTimeRange();
      this.currentPage = 1;
      this.search();
    },
    
    // 切换页码
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
        this.search();
      }
    },
    
    // 改变每页显示数量
    changePageSize() {
      this.currentPage = 1;
      this.search();
    },
    
    // 格式化日期时间
    formatDateTime(dateTimeString) {
      const date = new Date(dateTimeString);
      return date.toLocaleString();
    }
  }
}
</script>

<style scoped>
/* 自定义样式 */
</style>
