import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// El proxy evita problemas de CORS en desarrollo: todo /api se envía a Spring Boot.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
