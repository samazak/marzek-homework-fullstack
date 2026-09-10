import { expect, type Page } from '@playwright/test'

// Requires the actual packaged Java application and the disposable Oracle demo schema.
// No routes are intercepted: this covers browser → authenticated API → PL/SQL.
export async function loginAsReviewer(page: Page) {
  await page.goto('/')
  await expect(page).toHaveTitle('Trade Finance Demo')
  await expect(page.getByRole('link', { name: 'TF / Trade Finance Demo' })).toBeVisible()
  await page.getByRole('link', { name: 'Sign in' }).click()
  await page.getByLabel('Username').fill(process.env.LIVE_APP_USER ?? 'reviewer')
  await page.getByLabel('Password').fill(process.env.LIVE_APP_PASSWORD!)
  await page.getByRole('button', { name: 'Sign in' }).click()
  await expect(page.getByRole('heading', { name: 'From data to understanding.' })).toBeVisible()
}
