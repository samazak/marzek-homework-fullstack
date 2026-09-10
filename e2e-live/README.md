# Live e2e tests

See the [top-level README](../README.md) for the full repository map, and [`frontend/e2e/README.md`](../frontend/e2e/README.md) for the mocked-backend equivalent.

Playwright tests in this folder run against the **real packaged application** and a **disposable Oracle schema** - no mocked routes. They cover browser → authenticated API → PL/SQL end to end.

Two ways to run these specs:

- **No Docker/Oracle** (faster, mocked backend): [`frontend/e2e`](../frontend/e2e/README.md) - same specs, run this first.
- **This folder** (real integration, needs Docker + disposable Oracle): steps below.

| File | What it checks |
|---|---|
| [`live-helpers.ts`](./live-helpers.ts) | Shared `loginAsReviewer()` used by every spec |
| [`age.spec.ts`](./age.spec.ts) | Age-description workflow |
| [`pi.spec.ts`](./pi.spec.ts) | π calculation + intermediate steps table |
| [`invoices.spec.ts`](./invoices.spec.ts) | Outstanding invoices list |

Config lives in [`playwright.live.config.ts`](./playwright.live.config.ts), right beside the specs. `LIVE_APP_PASSWORD` is required; `LIVE_APP_USER` defaults to `reviewer`; `LIVE_APP_URL` defaults to `http://127.0.0.1:18080`.

## Run it

1. Start the disposable Oracle DB + packaged app (from repo root):

```powershell
# Create an ignored .env containing VERIFY_DB_PASSWORD=<a generated test password>.
docker build -t trade-finance-demo:local .
docker compose -p trade-finance-demo-verify -f compose.verify.yml run --build --rm tests
docker compose -p trade-finance-demo-verify -f compose.verify.yml --profile app up -d app
```

App is now on `http://127.0.0.1:18080`, user `reviewer`, password = your `VERIFY_DB_PASSWORD`.

2. Run the tests:

```powershell
cd frontend
$env:LIVE_APP_PASSWORD = '<the same disposable test password>'
# Optional, to use installed Chrome instead of downloading Chromium: $env:PLAYWRIGHT_CHANNEL = 'chrome'
npm run test:live
cd ..
```

3. Clean up the disposable containers + DB when done:

```powershell
docker compose -p trade-finance-demo-verify -f compose.verify.yml down --volumes
```

Do not reuse the `trade-finance-demo-verify` Compose project for persistent data, and don't point `LIVE_APP_URL` at anything but a disposable schema - the invoice/π assertions expect known demo data.

## Adding a test

Each workflow gets its own `*.spec.ts` file - keeps unrelated changes from touching the same file and colliding in review. Reuse `loginAsReviewer` from `live-helpers.ts` in a `test.beforeEach` rather than duplicating the sign-in flow.

## Why Playwright, not Selenium

Functionally both can drive the same browser flows here - sign in, fill the age form, wait for the π API response, read the invoices table. The difference is boilerplate and stability, not capability:

- **Waiting.** Playwright auto-waits for elements to be actionable before every interaction. Selenium needs explicit `WebDriverWait` + `ExpectedConditions` around most steps, or you get flaky `ElementNotInteractableException`s.
- **Locators.** Playwright's role-based locators (`getByRole`, `getByLabel`) read close to how a user finds things. Selenium's `By.xpath`/`By.cssSelector` are more verbose and brittle to markup changes.
- **Network.** Waiting for the `/api/pi` response (see [`pi.spec.ts`](./pi.spec.ts)) is one line via `page.waitForResponse`. Selenium has no network layer - you'd poll the DOM or bolt on a proxy (e.g. BrowserMob) to get the same signal.
- **Stack fit.** This is a TypeScript/Vue frontend; Playwright shares the same language and `npm` toolchain as the component tests. A Selenium suite here would mean introducing JUnit + a Java test module (or a separate Node `selenium-webdriver` setup) alongside the existing Gradle/JUnit backend suite, plus WebDriver binary management.

Rough size of a Selenium/JUnit port of these four files: same three workflows, but each interaction grows from 1 line to roughly 3-5 (explicit wait + locator + action), plus a `@BeforeEach` login helper, a driver factory/teardown class, and Maven/Gradle wiring for WebDriverManager and a JUnit test task in CI. Call it 3-4x the line count and a half-day to a day of infra work to get an equivalent CI gate, versus the existing `npm run test:live` one-liner. Feasible, not free - kept as Playwright here since the quality bar (auto-wait, network assertions) is higher for less code, but Selenium/JUnit experience is straightforward to demonstrate separately if wanted.
