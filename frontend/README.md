# Trade Finance Demo - Frontend

Vue 3 + TypeScript UI: sign-in, age lookup, π calculation and the outstanding-invoices table.

See the [top-level README](../README.md) for the feature map, full-stack JAR/container build, and the AI-assisted development disclosure. See [`backend/README.md`](../backend/README.md) for the API this UI calls.

## Prerequisites

- Node.js **24.12.0** (or another supported Node release ≥22.12) and npm. The lockfile pins transitive dependencies.
- **The backend must already be running** - see [`backend/README.md`](../backend/README.md#run-locally). Without it, `npm run dev` starts fine but sign-in and every API call fail.
- Optional Chromium/Chrome for browser tests (`npx playwright install chromium`, or set `PLAYWRIGHT_CHANNEL=chrome` to use an installed Chrome).

Vite's Node requirements are in its [getting-started guide](https://vite.dev/guide/).

### Switching Node versions

This folder has an [`.nvmrc`](./.nvmrc) pinning the Node version. If you juggle multiple projects with different Node requirements, use a version manager instead of a single global install:

```powershell
# nvm-windows (https://github.com/coreybutler/nvm-windows)
nvm install 24.12.0
nvm use 24.12.0

# nvm (macOS/Linux, https://github.com/nvm-sh/nvm) or fnm (https://github.com/Schniz/fnm) - both read .nvmrc
nvm use
```

## Run locally

Start the backend first (see prerequisite above), then:

```powershell
cd frontend
npm ci
npm run dev
```

Open `http://127.0.0.1:5173`, select **Sign in**, then use the configured application credentials. Vite proxies API and login requests to the backend on port 8080. A backend session authenticates API requests; the π POST obtains a CSRF token before submitting. Credentials are not embedded in the UI or stored in browser local storage. The page itself is public, but all data endpoints require authentication.

## Build

```powershell
npm run build
```

Type-checks with `vue-tsc` and produces a Vite production bundle in `dist/`. See the [top-level README](../README.md#build-one-deployable-jar) for packaging this bundle into the backend's JAR.

## Application boundaries

The Vue UI has one feature component per task (`src/features/age`, `pi`, `invoice`, `login`), a shared `src/components/ui` kit of reusable primitives (buttons, tables, form fields), a `composables/useAsyncAction` helper for shared busy/error handling, and an injectable transport class (`src/api.ts`) for HTTP/session behavior so components do not know CSRF or HTTP details. This applies single responsibility, interface segregation and dependency inversion without unnecessary abstraction. See [`backend/README.md`](../backend/README.md#application-boundaries) for the server-side package split.

## Tests

```powershell
npm test              # Vitest: component + api.ts transport tests
npm run build          # type-check + production build
npx playwright install chromium
npm run test:ui        # Playwright, mocked /api/** routes - see e2e/README.md
```

Vitest covers CSRF transport, session/network errors and component results. Playwright browser tests in [`e2e/`](./e2e/README.md) use explicitly mocked API responses: they verify real browser interaction and rendering, **not** Oracle/backend integration.

For the real-backend, real-Oracle browser tests, see [`../e2e-live/README.md`](../e2e-live/README.md) (`npm run test:live`).
