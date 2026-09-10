import { test, expect } from '@playwright/test'
import { mockSignedIn } from './api-mocks'

test('a reviewer can describe an age', async ({ page }) => {
  await mockSignedIn(page)
  await page.route('**/api/age**', route => route.fulfill({ json: { age: '18', text: 'You are adult' } }))
  await page.goto('/')

  await page.getByRole('button', { name: 'Describe age' }).click()

  await expect(page.getByText('You are adult')).toBeVisible()
})
