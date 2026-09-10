CREATE OR REPLACE PACKAGE pi_pkg AUTHID DEFINER IS
    c_err_invalid_precision CONSTANT PLS_INTEGER := -20003;
    c_err_not_converged CONSTANT PLS_INTEGER := -20004;
    -- Oracle NUMBER holds at most 38 significant digits; each BBP iteration resolves
    -- ~1.2 more decimal digits (log10(16)), so ~32 iterations already exhaust everything
    -- NUMBER can represent. 40 leaves a small margin without ever being reachable by a
    -- precision the datatype could actually satisfy - it only guards against a genuine
    -- bug in the convergence check looping forever, not against legitimate input.
    c_max_iterations CONSTANT PLS_INTEGER := 40;

    -- NUMBER holds ~38 significant digits total; pi's leading "3" consumes one, leaving
    -- an absolute precision floor well short of 38 decimal places once rounding error
    -- across c_max_iterations additions is accounted for. 1E-24 mirrors the API-side cap
    -- (PiService.MAX_PI_PRECISION_DECIMAL_SCALE) so callers hitting the package directly
    -- get the same guarantee: a tighter request would report convergence the datatype
    -- cannot actually back up.
    c_min_precision CONSTANT NUMBER := 1E-24;

    FUNCTION calculate_pi(p_precision IN NUMBER) RETURN NUMBER;
END pi_pkg;
/

CREATE OR REPLACE PACKAGE BODY pi_pkg IS

    -- Bailey-Borwein-Plouffe (BBP) series term: pi = sum(1/16^i * (4/(8i+1) - 2/(8i+4) - 1/(8i+5) - 1/(8i+6)))
    FUNCTION bbp_term(p_iteration IN PLS_INTEGER, p_factor IN NUMBER) RETURN NUMBER IS
    BEGIN
        RETURN p_factor * (4 / (8*p_iteration+1) - 2 / (8*p_iteration+4)
                            - 1 / (8*p_iteration+5) - 1 / (8*p_iteration+6));
    END bbp_term;

    -- Upper bound on the sum of every not-yet-added term (p_next_iteration onward):
    -- each term's absolute value is at most 4/(8j+1) (the largest of the four fractions),
    -- and successive terms shrink by 1/16, so their sum is bounded by a geometric series
    -- with ratio 1/16: sum(16^-k, k=0..inf) = 16/15. Combined: 4 * 16/15 = 64/15.
    FUNCTION remaining_term_bound(p_next_iteration IN PLS_INTEGER, p_next_factor IN NUMBER) RETURN NUMBER IS
    BEGIN
        RETURN (64 / 15) * p_next_factor / (8*p_next_iteration+1);
    END remaining_term_bound;

    FUNCTION calculate_pi(p_precision IN NUMBER) RETURN NUMBER IS
        v_sum NUMBER := 0;
        v_factor NUMBER := 1;
        v_term NUMBER;
        v_bound NUMBER;
    BEGIN
        IF p_precision IS NULL OR p_precision <= 0 THEN
            RAISE_APPLICATION_ERROR(c_err_invalid_precision, 'Precision must be a positive number.');
        END IF;
        IF p_precision < c_min_precision THEN
            RAISE_APPLICATION_ERROR(c_err_invalid_precision,
                'Precision below ' || TO_CHAR(c_min_precision) || ' is not supported.');
        END IF;
        SAVEPOINT pi_start;
        BEGIN
            DELETE FROM pi_intermediate_result;
            FOR i IN 0..c_max_iterations-1 LOOP
                v_term := bbp_term(i, v_factor);
                v_sum := v_sum + v_term;
                v_factor := v_factor / 16; -- next term's 16^-i factor, computed incrementally instead of via POWER()
                v_bound := remaining_term_bound(i+1, v_factor);
                INSERT INTO pi_intermediate_result VALUES (i, v_term, v_sum);
                IF v_bound <= p_precision THEN
                    RETURN v_sum;
                END IF;
            END LOOP;
            RAISE_APPLICATION_ERROR(c_err_not_converged, 'Calculation did not converge within the iteration limit.');
        EXCEPTION
            WHEN OTHERS THEN
                ROLLBACK TO pi_start;
                RAISE;
        END;
    END calculate_pi;

END pi_pkg;
/
