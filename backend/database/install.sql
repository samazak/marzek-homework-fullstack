WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
SET DEFINE OFF
-- Absolute paths, not @@relative: @@ resolves against the *currently executing
-- script's* directory, which this sqlplus build fails to track correctly when
-- install.sql itself is invoked via a heredoc-fed session (as the dev/verify
-- Docker init scripts do) rather than as a direct `sqlplus @install.sql` argument.
-- /database is the fixed mount point both compose.dev.yml and compose.verify.yml use.
@/database/migrations/V1__init_schema.sql
@/database/migrations/R__age_pkg.sql
@/database/migrations/R__pi_pkg.sql
@/database/migrations/R__invoice_vw.sql
@/database/migrations/R__zzz_verify_compile.sql
