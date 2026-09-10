import { test, expect } from '@playwright/test'
import { loginAsReviewer } from './live-helpers'

test.beforeEach(async ({ page }) => {
  await loginAsReviewer(page)
})

test('calculate pi workflow', async ({ page }) => {
  const piResponse = page.waitForResponse(response => response.url().endsWith('/api/pi'))

  await page.getByRole('button', { name: 'Calculate π' }).click()
  const pi = await (await piResponse).json()

  expect(Math.abs(Number(pi.value) - Math.PI)).toBeLessThanOrEqual(0.000001)
  await expect(page.getByRole('region', { name: 'Watch π converge' }).locator('output')).toHaveText(pi.value)

  await page.getByText('Intermediate results', { exact: true }).click()

  await expect(page.locator('details tbody tr')).toHaveCount(pi.steps.length)
})
