import { test, expect } from '@playwright/test'
import { loginAsReviewer } from './live-helpers'

test.beforeEach(async ({ page }) => {
  await loginAsReviewer(page)
})

test('describe age workflow', async ({ page }) => {
  await page.getByLabel('Age in years').fill('7')

  await page.getByRole('button', { name: 'Describe age' }).click()

  await expect(page.getByText('You are schoolchild', { exact: true })).toBeVisible()
})
