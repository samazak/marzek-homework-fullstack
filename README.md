# Trade Finance Demo

Oracle PL/SQL wired into a Java + Vue app for the Trade Finance Baltics domain.

Stack: Oracle SQL/PLSQL, Java 21, Spring Boot, Gradle, TypeScript, Vue 3. Tasks 1-3's business logic lives in Oracle; the app just calls it over JDBC and shows the results.

## Repository map

| Path | What's there | Docs |
| --- | --- | --- |
| [`backend/`](backend/README.md) | Oracle PL/SQL schema + Java/Spring Boot API | [`backend/README.md`](backend/README.md) - database setup, run locally, API reference, design decisions, JUnit/Oracle tests |
| [`frontend/`](frontend/README.md) | Vue 3 + TypeScript UI | [`frontend/README.md`](frontend/README.md) - run locally, build, Vitest tests |
| [`frontend/e2e/`](frontend/e2e/README.md) | Browser tests against mocked `/api/**` | [`frontend/e2e/README.md`](frontend/e2e/README.md) |
| [`e2e-live/`](e2e-live/README.md) | Browser tests against the real packaged app + disposable Oracle | [`e2e-live/README.md`](e2e-live/README.md) |

Read [`backend/README.md`](backend/README.md) first (database + API), then [`frontend/README.md`](frontend/README.md) (UI). Both link back here for the full-stack build below.

Docker (for the app container) and Chromium/Chrome (for browser tests) are the only things both sides need - check each README for the rest (Oracle/JDK/Gradle on the backend, Node on the frontend).

## Docker, three ways

No Docker yet? Pick one:

| Option | Platform | Install | Notes |
| --- | --- | --- | --- |
| **Docker Desktop** (recommended) | Windows, macOS | [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/) | GUI + CLI. On Windows it sets up its own WSL2 backend during install - a separate WSL install is not required. |
| **WSL2 + Docker Engine** | Windows, no Desktop | `wsl --install` (admin PowerShell, then restart) - [details](https://learn.microsoft.com/windows/wsl/install), then install [Docker Engine](https://docs.docker.com/engine/install/) inside the WSL Linux distro | CLI only, lighter, no Desktop license/UI. Run all `docker`/`docker compose` commands from inside the WSL shell. |
| **Docker Engine** | Linux | [docs.docker.com/engine/install](https://docs.docker.com/engine/install/) | Native, no VM layer needed. |

Check either worked with `docker --version` and `docker compose version`.

All three paths below only need Docker - no JDK, Gradle, Node or Oracle client on the host.

| Compose file | What it starts | Use it for | Details |
| --- | --- | --- | --- |
| `compose.dev.yml` | A persistent Oracle Free container, schema pre-installed | Day-to-day local dev: run backend/frontend against it with `gradle bootRun` / `npm run dev` | [`backend/README.md` § Database setup, Option A](backend/README.md#database-setup) |
| `Dockerfile` (plain `docker build`/`run`) | The full app as one image, no Oracle included | Trying the packaged JAR against your own Oracle | "Container build" below |
| `compose.verify.yml` | A disposable Oracle + the built image, wired together | One-command proof the app works end to end, then thrown away | "Verify everything using Docker" below |

New here? Start with `compose.dev.yml` - it's the fastest way to get a working database and see the app run.

### Build one deployable JAR

```powershell
cd frontend
npm ci
npm run build
cd ../backend
gradle test bootJar
java -jar build/libs/trade-finance-demo-1.0.0.jar
```

The Gradle resource task bundles the built UI into the JAR, served at `http://127.0.0.1:8080`. `bootJar` fails with a clear message if the UI isn't built yet - build frontend assets again after UI changes before rerunning Gradle.

### Container build

```text
docker build -t trade-finance-demo:local .
docker run --rm -p 127.0.0.1:8080:8080 --env-file .env trade-finance-demo:local
```

Create a local, gitignored `.env` with `DB_URL`, `DB_USER`, `DB_PASSWORD`, `APP_USER` and `APP_PASSWORD`. From Docker Desktop, reach a host Oracle instance via `host.docker.internal`, not `localhost`. The multi-stage build runs the frontend and JUnit tests and bakes the UI into the JAR; the final image runs as a non-root user. For a real production deployment you'd still need to terminate TLS, set `SPRING_PROFILES_ACTIVE=prod` for secure session cookies, manage secrets properly, and add resource limits and ingress throttling. The single-user login here is fine for a local demo - real customer data would need proper identity and object-level authorization, which is out of scope here.

## Verify everything using Docker

`compose.verify.yml` spins up a disposable Oracle Free database, loads the schema and synthetic data, and runs the real JDBC/PLSQL integration suite, following the image maintainer's [initialization and application-user instructions](https://github.com/gvenzl/oci-oracle-free/blob/main/README.md). No host port is published for the database, and this path doesn't need a host JDK, Gradle or Oracle client.

```powershell
# Create a gitignored .env containing VERIFY_DB_PASSWORD=<a generated test password>.
docker build -t trade-finance-demo:local .
docker compose -p trade-finance-demo-verify -f compose.verify.yml run --build --rm tests
docker compose -p trade-finance-demo-verify -f compose.verify.yml --profile app up -d app
```

The app then runs at `http://127.0.0.1:18080` with username `reviewer` and the same disposable test password. To try the packaged app in a browser with no mocked responses, see [`e2e-live/README.md`](e2e-live/README.md) (`npm run test:live` from `frontend`).

Once you're done, tear down just these disposable containers and their test database with `docker compose -p trade-finance-demo-verify -f compose.verify.yml down --volumes`. Don't reuse this verify project for anything persistent. Using a differently named env file? Pass `--env-file <path>` to every Compose command.

CI runs the frontend build, component tests, browser tests and JUnit tests, plus a security gate on every push: ESLint, `npm audit` and a license check on the frontend, OWASP Dependency-Check on the backend's Java dependencies, Trivy against dependencies and the Dockerfile, and Gitleaks for committed secrets. Dependabot opens weekly PRs for outdated/vulnerable dependencies. The Compose workflow above adds the real-database and live-browser gates CI doesn't cover. A passing CI run isn't proof a workflow has actually been executed against this code, and passing tests alone don't make this production-certified - check auth integration, load behavior and operational controls in your target environment first.

## AI-assisted development

I used **Claude Code** as my AI coding assistant throughout this project: designing the schema and transactions, generating the SQL/PLSQL, Java, TypeScript/Vue, tests, build/CI config, and this README. It also ran shell commands to install frontend dependencies and run the available checks, and looked things up in the official framework and Oracle docs when needed. Every part of this was AI-assisted - I'm not claiming any of it as independently handwritten.

That said, the requirements, stack and quality bar were mine to set, and I kept control over what the tool was allowed to run. Verification results are reported separately from the generated test code itself. Passing tests, AI-written or not, don't establish Oracle correctness or deployment readiness on their own - it's worth reading through the code and design tradeoffs before relying on this in production, especially the π transaction lock, the currency constraint, and how responsibilities are split. No other AI tools were used.