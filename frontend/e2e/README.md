# Browser contract tests

Part of [`frontend/`](../README.md); see the [top-level README](../../README.md) for the full repository map.

Playwright tests in this folder drive the real Vue app against **mocked `/api/**` routes** - no backend, no Oracle. They verify browser interaction and rendering, not integration; see [`../../e2e-live`](../../e2e-live) for the real-backend equivalent.

| File | What it checks |
|---|---|
| [`api-mocks.ts`](./api-mocks.ts) | Shared `mockSignedIn()` used by every spec |
| [`age.spec.ts`](./age.spec.ts) | Age-description workflow |
| [`pi.spec.ts`](./pi.spec.ts) | π calculation + intermediate steps table |
| [`invoices.spec.ts`](./invoices.spec.ts) | Outstanding invoices list |
| [`session-expiry.spec.ts`](./session-expiry.spec.ts) | A signed-in session that expires mid-action shows an actionable error |

Config lives in [`../playwright.config.ts`](../playwright.config.ts), next to `package.json` - it drives `npm run dev` as its `webServer`, so it stays where `npm` scripts resolve from rather than inside this folder.

## Run it

```powershell
cd frontend
npm ci
npx playwright install chromium
npm run test:ui
```

To use an installed Chrome instead of downloading Chromium, set `PLAYWRIGHT_CHANNEL=chrome`.

## Adding a test

Each workflow gets its own `*.spec.ts` file, same convention as `../../e2e-live` - keeps unrelated changes from touching the same file and colliding in review. Reuse `mockSignedIn` from `api-mocks.ts` rather than duplicating the `/api/csrf` and `/api/session` fixtures.
