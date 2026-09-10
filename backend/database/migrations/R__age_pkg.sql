CREATE OR REPLACE PACKAGE age_pkg AUTHID DEFINER IS
    c_err_invalid_age CONSTANT PLS_INTEGER := -20001;
    c_err_no_thresholds CONSTANT PLS_INTEGER := -20002;

    FUNCTION age_description(p_age IN NUMBER) RETURN VARCHAR2;
END age_pkg;
/

CREATE OR REPLACE PACKAGE BODY age_pkg IS

    FUNCTION age_description(p_age IN NUMBER) RETURN VARCHAR2 IS
        v_message age_text.message%TYPE;
    BEGIN
        IF p_age IS NULL OR p_age < 0 THEN
            RAISE_APPLICATION_ERROR(c_err_invalid_age, 'Age must be a nonnegative number.');
        END IF;
        SELECT message INTO v_message
          FROM age_text
         WHERE minimum_age <= p_age
         ORDER BY minimum_age DESC
         FETCH FIRST 1 ROW ONLY;
        RETURN v_message;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(c_err_no_thresholds, 'Age thresholds are not configured.');
    END age_description;

END age_pkg;
/
