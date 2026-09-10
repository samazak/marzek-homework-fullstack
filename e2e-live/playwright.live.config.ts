import { defineConfig } from '@playwright/test'
if (!process.env.LIVE_APP_PASSWORD) throw new Error('LIVE_APP_PASSWORD is required for the live stack test')
export default defineConfig({
  testDir: '.',
  use: {
    baseURL: process.env.LIVE_APP_URL ?? 'http://127.0.0.1:18080',
    channel: process.env.PLAYWRIGHT_CHANNEL,
    headless: true,
  },
})
