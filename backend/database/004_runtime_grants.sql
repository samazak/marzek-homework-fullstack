-- Optional production separation. As the owner, grant to a pre-created TRADE_FINANCE_RUNTIME user.
-- The DBA provisions credentials separately. Change the grantee when needed.
GRANT EXECUTE ON age_pkg TO trade_finance_runtime;
GRANT EXECUTE ON pi_pkg TO trade_finance_runtime;
GRANT SELECT ON pi_intermediate_result TO trade_finance_runtime;
GRANT SELECT ON unpaid_invoice TO trade_finance_runtime;
-- Configure Hikari connection-init-sql: ALTER SESSION SET CURRENT_SCHEMA = TRADE_FINANCE
-- when the runtime user differs from the TRADE_FINANCE owner; no CREATE ANY privileges needed.
