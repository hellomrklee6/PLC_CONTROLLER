<template>
  <div class="container-fluid">
    <!-- 操作按钮 -->
    <div class="mb-4">
      <button class="btn btn-primary" id="addSchemeBtn" @click="addScheme">添加方案</button>
    </div>
    
    <!-- 方案列表 -->
    <div class="card">
      <div class="card-body">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>描述</th>
              <th>按钮数量</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody id="schemeList">
            <tr v-if="schemes.length === 0">
              <td colspan="5" class="text-center">没有按钮方案</td>
            </tr>
            <tr v-for="scheme in schemes" :key="scheme.id">
              <td>{{ scheme.id }}</td>
              <td>{{ scheme.name }}</td>
              <td>{{ scheme.description || '-' }}</td>
              <td>{{ getButtonCount(scheme.id) }}</td>
              <td>
                <button class="btn btn-sm btn-primary me-1" @click="editScheme(scheme)">编辑</button>
                <button class="btn btn-sm btn-danger" @click="deleteScheme(scheme.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- 添加/编辑方案模态框 -->
    <div class="modal fade" id="addModal" tabindex="-1" aria-labelledby="addModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="addModalLabel">{{ editingScheme.id ? '编辑方案' : '添加方案' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form>
              <!-- 方案名称 -->
              <div class="mb-3">
                <label for="schemeName" class="form-label">方案名称</label>
                <input type="text" class="form-control" id="schemeName" v-model="editingScheme.name" required>
              </div>
              
              <!-- 方案描述 -->
              <div class="mb-3">
                <label for="schemeDescription" class="form-label">方案描述</label>
                <textarea class="form-control" id="schemeDescription" v-model="editingScheme.description" rows="2"></textarea>
              </div>
              
              <!-- 按钮列表 -->
              <div class="mb-3">
                <label class="form-label">按钮配置</label>
                <div v-for="(button, index) in editingButtons" :key="index" class="card mb-2 p-3 border">
                  <div class="d-flex justify-content-between align-items-center mb-2">
                    <h6>按钮 {{ index + 1 }}</h6>
                    <button type="button" class="btn btn-sm btn-danger" @click="removeButton(index)">删除</button>
                  </div>
                  <div class="row g-2">
                    <div class="col-md-4">
                      <label :for="`buttonName-${index}`" class="form-label">按钮名称</label>
                      <input type="text" class="form-control" :id="`buttonName-${index}`" v-model="button.name" required>
                    </div>
                    <div class="col-md-4">
                      <label :for="`buttonValue-${index}`" class="form-label">按钮值</label>
                      <input type="text" class="form-control" :id="`buttonValue-${index}`" v-model="button.buttonValue" required>
                    </div>
                    <div class="col-md-4">
                      <label :for="`buttonDescription-${index}`" class="form-label">按钮描述</label>
                      <input type="text" class="form-control" :id="`buttonDescription-${index}`" v-model="button.description">
                    </div>
                  </div>
                </div>
                
                <!-- 添加按钮 -->
                <button type="button" class="btn btn-sm btn-success w-100" @click="addButton">添加按钮</button>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveScheme">保存</button>
          </div>
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
const schemes = ref([])
const editingScheme = ref({
  id: null,
  name: '',
  description: ''
})
const editingButtons = ref([])
const buttonCounts = ref({})

// 加载所有方案
const loadSchemes = async () => {
  try {
    const response = await axios.get('/api/button-schemes')
    schemes.value = response.data
    // 加载每个方案的按钮数量
    for (const scheme of schemes.value) {
      await loadButtonCount(scheme.id)
    }
  } catch (error) {
    console.error('加载按钮方案失败:', error)
  }
}

// 加载方案的按钮数量
const loadButtonCount = async (schemeId) => {
  try {
    const response = await axios.get(`/api/buttons/scheme/${schemeId}`)
    buttonCounts.value[schemeId] = response.data.length
  } catch (error) {
    console.error('加载按钮数量失败:', error)
    buttonCounts.value[schemeId] = 0
  }
}

// 获取按钮数量
const getButtonCount = (schemeId) => {
  return buttonCounts.value[schemeId] || 0
}

// 添加方案
const addScheme = () => {
  resetEditingScheme()
  // 显示模态框
  const modal = new bootstrap.Modal(document.getElementById('addModal'))
  modal.show()
}

// 编辑方案
const editScheme = async (scheme) => {
  // 深拷贝方案数据
  editingScheme.value = JSON.parse(JSON.stringify(scheme))
  // 加载方案的按钮
  try {
    const response = await axios.get(`/api/buttons/scheme/${scheme.id}`)
    editingButtons.value = response.data
  } catch (error) {
    console.error('加载方案按钮失败:', error)
    editingButtons.value = []
  }
  // 显示模态框
  const modal = new bootstrap.Modal(document.getElementById('addModal'))
  modal.show()
}

// 删除方案
const deleteScheme = async (schemeId) => {
  if (confirm('确定要删除这个方案吗？')) {
    try {
      // 先删除方案的所有按钮
      await axios.delete(`/api/buttons/scheme/${schemeId}`)
      // 再删除方案
      const response = await axios.delete(`/api/button-schemes/${schemeId}`)
      if (response.status === 204) {
        // 重新加载方案列表
        await loadSchemes()
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        alert(error.response.data)
      } else {
        console.error('删除按钮方案失败:', error)
      }
    }
  }
}

// 保存方案
const saveScheme = async () => {
  try {
    let response
    if (editingScheme.value.id) {
      // 更新方案
      response = await axios.put(`/api/button-schemes/${editingScheme.value.id}`, editingScheme.value)
      // 删除旧按钮
      await axios.delete(`/api/buttons/scheme/${editingScheme.value.id}`)
    } else {
      // 添加方案
      response = await axios.post('/api/button-schemes', editingScheme.value)
    }
    
    // 保存新按钮
    for (const button of editingButtons.value) {
      button.schemeId = response.data.id
      await axios.post('/api/buttons', button)
    }
    
    // 重新加载方案列表
    await loadSchemes()
    // 关闭模态框
    const modal = bootstrap.Modal.getInstance(document.getElementById('addModal'))
    modal.hide()
  } catch (error) {
    console.error('保存按钮方案失败:', error)
  }
}

// 添加按钮
const addButton = () => {
  editingButtons.value.push({
    name: '',
    buttonValue: '',
    description: ''
  })
}

// 删除按钮
const removeButton = (index) => {
  editingButtons.value.splice(index, 1)
}

// 重置编辑方案
const resetEditingScheme = () => {
  editingScheme.value = {
    id: null,
    name: '',
    description: ''
  }
  editingButtons.value = []
}

// 组件挂载时加载方案列表
onMounted(() => {
  loadSchemes()
})
</script>

<style scoped>
/* 自定义样式 */
</style>
