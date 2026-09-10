#!/bin/sh
set -eu
# The image provisions APP_USER before running initialization scripts.
# Do not enable shell tracing: the connection string contains a disposable secret.
sqlplus -s "${APP_USER}/${APP_USER_PASSWORD}@//localhost:1521/FREEPDB1" <<'SQL'
WHENEVER OSERROR EXIT FAILURE
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
@/database/install.sql
@/database/003_demo_data.sql
EXIT SUCCESS
SQL
