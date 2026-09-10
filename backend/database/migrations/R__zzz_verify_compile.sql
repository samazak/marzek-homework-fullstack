-- Sorted last among repeatables (R__zzz...) so it runs after every other R__ file in this migrate.
-- CREATE OR REPLACE succeeds even when a package/view body fails to compile; only USER_ERRORS catches it.
-- ${flyway:timestamp} makes this file's checksum differ on every migrate, so Flyway reruns it
-- even when only another R__ file (e.g. R__pi_pkg.sql) changed and this one didn't.
DECLARE
    v_errors NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_errors FROM user_errors
     WHERE name IN ('AGE_PKG', 'PI_PKG', 'UNPAID_INVOICE');
    IF v_errors > 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Compilation errors: inspect USER_ERRORS.');
    END IF;
END;
/
