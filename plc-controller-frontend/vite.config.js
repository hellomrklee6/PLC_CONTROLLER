import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/plcweb/',
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:19999',
        changeOrigin: true
      },
      '/ws-plc': {
        target: 'http://localhost:19999',
        changeOrigin: true,
        ws: true
      }
    }
  },
  define: {
    'process.env': {},
    'global': 'window'
  }
})
