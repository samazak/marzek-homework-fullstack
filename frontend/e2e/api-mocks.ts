import type { Page } from '@playwright/test'

/** Browser contract tests use explicit HTTP fixtures; Oracle integration is tested separately (see ../e2e-live). */
export async function mockSignedIn(page: Page) {
  await page.route('**/api/csrf', route => route.fulfill({ json: { headerName: 'X-CSRF-TOKEN', token: 'fixture' } }))
  await page.route('**/api/session', route => route.fulfill({ status: 200 }))
}
