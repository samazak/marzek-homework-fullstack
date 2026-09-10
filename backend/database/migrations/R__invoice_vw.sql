CREATE OR REPLACE VIEW unpaid_invoice AS
SELECT i.invoice_id, i.invoice_date, i.invoice_amount, i.currency,
       NVL(p.paid_amount, 0) AS paid_amount,
       i.invoice_amount - NVL(p.paid_amount, 0) AS outstanding_amount
  FROM invoice i
  LEFT JOIN (
      SELECT invoice_id, currency, SUM(payment_amount) AS paid_amount
        FROM payment
       GROUP BY invoice_id, currency
  ) p ON p.invoice_id = i.invoice_id AND p.currency = i.currency
 WHERE NVL(p.paid_amount, 0) < i.invoice_amount;
/
