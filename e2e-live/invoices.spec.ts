import { test, expect } from '@playwright/test'
import { loginAsReviewer } from './live-helpers'

test.beforeEach(async ({ page }) => {
  await loginAsReviewer(page)
})

test('refresh invoices workflow', async ({ page }) => {
  await page.getByRole('button', { name: 'Refresh invoices' }).click()

  const invoices = page.getByRole('table', { name: 'Outstanding invoices, ordered by invoice ID' })
  
  await expect(invoices.locator('tbody tr')).toHaveCount(2)
  await expect(invoices.getByRole('rowheader', { name: '#1', exact: true })).toBeVisible()
  await expect(invoices.getByRole('rowheader', { name: '#2', exact: true })).toBeVisible()
})
