# Trade Finance Demo - Backend

Oracle PL/SQL + Java/Spring Boot implementation of age lookup, π calculation and unpaid invoices, and the HTTP API the frontend calls.

See the [top-level README](../README.md) for the feature map, full-stack JAR/container build, and the AI-assisted development disclosure. See [`frontend/README.md`](../frontend/README.md) for the UI that consumes this API.

## Prerequisites

- Oracle Database 19c or newer, with an empty schema and a SQL*Plus-compatible client. An existing Oracle Free installation is suitable.
- JDK 21 and Gradle 8.14+ on PATH. Install Gradle with [SDKMAN](https://sdkman.io/) (`sdk install gradle 8.14.3`) on Linux/macOS, or with [Scoop](https://scoop.sh/) (`scoop install gradle`) or [Chocolatey](https://chocolatey.org/) (`choco install gradle`) on Windows. IntelliJ IDEA also bundles a compatible Gradle distribution.
- Optional Docker for the application container.

## Database setup

### Option A: Docker (recommended for local dev)

`../compose.dev.yml` runs a persistent Oracle Free container and installs the schema and demo data on first start.

Create a local `.env` (gitignored, at the repo root) with:

```text
DB_SYS_PASSWORD=<a local secret, for the Oracle SYS account>
DB_USER=trade_finance
DB_PASSWORD=<a local secret, for the trade_finance schema>
```

Then, from the repo root:

```powershell
docker compose -f compose.dev.yml up -d
docker compose -f compose.dev.yml logs -f oracle   # watch until healthy
```

To wipe and start over:

```powershell
docker compose -f compose.dev.yml down --volumes
```

Set `DB_URL=jdbc:oracle:thin:@localhost:1521/FREEPDB1` and `DB_USER`/`DB_PASSWORD` matching `.env` for the backend (see **Run locally** below).

### Option B: existing Oracle install

Have a DBA create a dedicated development schema in the target PDB:

```sql
CREATE USER trade_finance IDENTIFIED BY "replace-with-a-local-secret"
  DEFAULT TABLESPACE users QUOTA 50M ON users;
GRANT CREATE SESSION, CREATE TABLE, CREATE PROCEDURE, CREATE VIEW TO trade_finance;
```

Then apply the schema with Flyway (see **Schema migrations** below):

```powershell
$env:DB_URL = 'jdbc:oracle:thin:@localhost:1521/FREEPDB1'
$env:DB_USER = 'trade_finance'
$env:DB_PASSWORD = '<your database password>'
cd backend
gradle flywayMigrate
```

Demo data is not part of the tracked schema (see below), load it separately:

```text
sqlplus trade_finance@localhost:1521/FREEPDB1
SQL> @backend/database/003_demo_data.sql
```

For production, pre-create a runtime account with `CREATE SESSION`, execute `backend/database/004_runtime_grants.sql` as the owner, and configure `SPRING_DATASOURCE_HIKARI_CONNECTION_INIT_SQL=ALTER SESSION SET CURRENT_SCHEMA = TRADE_FINANCE`.

## Schema migrations (Flyway)

`backend/database/migrations/` is a [Flyway](https://flywaydb.org/) migration directory, applied with `gradle flywayMigrate` (reads `DB_URL`/`DB_USER`/`DB_PASSWORD` from the environment or the repo-root `.env`, same as `bootRun`). Flyway tracks what has already been applied to a given schema in its own `flyway_schema_history` table, so re-running the same command on a redeploy only applies what's new - unlike a flat SQL script, it's safe to run again against a schema that isn't empty.

Two kinds of migration file, for two different problems:

- **Versioned** (`V1__init_schema.sql`, next would be `V2__...sql`) - table/schema changes. Each runs **exactly once**, in order, tracked by version number. Never edit an already-applied `V` file; ship a new `V2__` etc. for further changes, the same way you wouldn't rewrite a past commit.
- **Repeatable** (`R__age_pkg.sql`, `R__pi_pkg.sql`, `R__invoice_vw.sql`) - packages and views. These have no meaningful "version": `CREATE OR REPLACE` just recreates them from whatever the file currently says. Flyway compares a checksum of the file against what it last applied and reruns it only when the content changed, so package "versioning" across environments is just "whatever is currently in this file in git" - there's no drift to track separately. `R__zzz_verify_compile.sql` runs last (sorted after the other `R__` files) and fails the migration if any package/view has compile errors, since `CREATE OR REPLACE` itself succeeds even when the body doesn't compile - only `USER_ERRORS` catches that.

Rollback: Flyway (the free/community edition used here) has no automatic down-migration, for either kind. To undo a table change, ship a new forward `V` migration that reverses it. To undo a package/view change, `git revert` the `R__` file and run `flywayMigrate` again - the checksum changes back, so Flyway reapplies the old content. Either way, the fix is a normal forward deploy, not a special rollback command.

`compose.dev.yml`'s container still bootstraps itself via `backend/database/install.sql` over `sqlplus` on first start (disposable/throwaway container, `down --volumes` to reset - there's nothing to "redeploy" there). That script runs the same files under `migrations/` directly, so there's one copy of the SQL either way. If you then run `flywayMigrate` against that same container, Flyway finds an unmigrated schema that already matches `V1` and records it as a baseline (`baselineOnMigrate` in `build.gradle`) instead of failing - from that point on it's tracked normally.

## Run locally

```powershell
cd backend
$env:DB_URL = 'jdbc:oracle:thin:@localhost:1521/FREEPDB1'
$env:DB_USER = 'trade_finance'
$env:DB_PASSWORD = '<your database password>'
$env:APP_USER = 'reviewer'
$env:APP_PASSWORD = '<your application password>'
gradle bootRun
```

The API is now on `http://127.0.0.1:8080`. Start the frontend next - see [`frontend/README.md`](../frontend/README.md#run-locally).

## API

Swagger UI: `http://127.0.0.1:8080/swagger-ui.html`. OpenAPI document: `/v3/api-docs`. Disabled under the `prod` profile.

## Tests

```powershell
cd backend
gradle test
```

For real database tests, install schema and demo scripts into a disposable test schema, then run:

```powershell
cd backend
$env:ORACLE_TEST_URL = 'jdbc:oracle:thin:@localhost:1521/FREEPDB1'
$env:ORACLE_TEST_USER = 'trade_finance_test'
$env:ORACLE_TEST_PASSWORD = '<test schema password>'
gradle oracleTest
```

See the [top-level README](../README.md#verify-everything-using-docker) for the disposable Docker verification path.
