import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'

const backend = `http://127.0.0.1:${process.env.VITE_BACKEND_PORT ?? '8080'}`

export default defineConfig({
  plugins: [vue()],
  server: { proxy: { '/api': backend, '/login': backend, '/logout': backend } },
  test: { environment: 'jsdom', include: ['src/**/*.test.ts'], clearMocks: true },
})
