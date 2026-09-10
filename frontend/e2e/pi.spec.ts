import { test, expect } from '@playwright/test'
import { mockSignedIn } from './api-mocks'

test('a reviewer can calculate pi and inspect intermediate results', async ({ page }) => {
  await mockSignedIn(page)
  await page.route('**/api/pi', route => route.fulfill({
    json: {
      value: '3.14159265', precision: '0.000001',
      steps: [{ iteration: 0, term: '3.1333', partialSum: '3.1333' }],
    },
  }))
  await page.goto('/')

  await page.getByRole('button', { name: 'Calculate π' }).click()

  await expect(page.getByText('3.14159265', { exact: true })).toBeVisible()

  await page.getByText('Intermediate results', { exact: true }).click()

  await expect(page.getByRole('cell', { name: '3.1333', exact: true })).toBeVisible()
})
