import { test, expect } from '@playwright/test'
import { mockSignedIn } from './api-mocks'

test('an expired session shows an actionable message', async ({ page }) => {
  await mockSignedIn(page)
  await page.route('**/api/age**', route => route.fulfill({ status: 401 }))
  await page.goto('/')

  await page.getByRole('button', { name: 'Describe age' }).click()

  await expect(page.getByRole('alert')).toContainText('Sign in')
})
