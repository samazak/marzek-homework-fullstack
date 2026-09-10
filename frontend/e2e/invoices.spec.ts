import { test, expect } from '@playwright/test'
import { mockSignedIn } from './api-mocks'

test('a reviewer can refresh outstanding invoices', async ({ page }) => {
  await mockSignedIn(page)
  await page.route('**/api/invoices/**', route => route.fulfill({
    json: {
      items: [{ invoiceId: '2', invoiceDate: '2026-01-11', currency: 'EUR', amount: '200', paidAmount: '75', outstandingAmount: '125' }],
      nextOffset: null,
    },
  }))
  await page.goto('/')

  await page.getByRole('button', { name: 'Refresh invoices' }).click()

  await expect(page.getByRole('cell', { name: '125', exact: true })).toBeVisible()
})
