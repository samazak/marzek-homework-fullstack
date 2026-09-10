CREATE TABLE age_text (
    minimum_age NUMBER PRIMARY KEY,
    message VARCHAR2(100 CHAR) NOT NULL,
    CONSTRAINT age_minimum_nonnegative CHECK (minimum_age >= 0)
);

-- Thresholds remove the possibility of overlapping ranges; 0 covers all valid ages.
INSERT INTO age_text VALUES (0, 'You are infant');
INSERT INTO age_text VALUES (7, 'You are schoolchild');
INSERT INTO age_text VALUES (18, 'You are adult');
INSERT INTO age_text VALUES (40, 'You are in middle-age');
INSERT INTO age_text VALUES (55, 'You are aged');

-- GLOBAL TEMPORARY: each Oracle session sees only its own rows, so concurrent
-- calculate_pi calls from different users never contend for the same data and
-- need no cross-session lock. ON COMMIT DELETE ROWS purges them automatically
-- when the calling transaction commits, on top of the explicit DELETE the
-- function already issues before every run.
CREATE GLOBAL TEMPORARY TABLE pi_intermediate_result (
    iteration NUMBER(3) PRIMARY KEY,
    term_value NUMBER NOT NULL,
    partial_sum NUMBER NOT NULL,
    CONSTRAINT pi_iteration_nonnegative CHECK (iteration >= 0)
) ON COMMIT DELETE ROWS;

CREATE TABLE invoice (
    invoice_id NUMBER(18) PRIMARY KEY,
    invoice_date DATE NOT NULL,
    invoice_amount NUMBER(19,2) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    CONSTRAINT invoice_id_positive CHECK (invoice_id > 0),
    CONSTRAINT invoice_amount_nonnegative CHECK (invoice_amount >= 0),
    CONSTRAINT invoice_currency_format CHECK (REGEXP_LIKE(currency, '^[A-Z]{3}$', 'c')),
    CONSTRAINT invoice_id_currency_unique UNIQUE (invoice_id, currency)
);

CREATE TABLE payment (
    payment_id NUMBER(18) PRIMARY KEY,
    payment_date DATE NOT NULL,
    payment_amount NUMBER(19,2) NOT NULL,
    currency VARCHAR2(3 CHAR) NOT NULL,
    invoice_id NUMBER(18) NOT NULL,
    CONSTRAINT payment_id_positive CHECK (payment_id > 0),
    CONSTRAINT payment_amount_positive CHECK (payment_amount > 0),
    CONSTRAINT payment_invoice_currency_fk FOREIGN KEY (invoice_id, currency)
        REFERENCES invoice (invoice_id, currency)
);
CREATE INDEX payment_invoice_idx ON payment (invoice_id, currency);
COMMIT;
