DROP TABLE IF EXISTS payments CASCADE;

CREATE TABLE IF NOT EXISTS payments
(
    payment_id UUID PRIMARY KEY,
    order_id UUID,
    total_payment NUMERIC(10, 2),
    delivery_total NUMERIC(10, 2),
    state VARCHAR(12) CHECK (state IN ('PENDING', 'SUCCESS', 'FAILED'))
);

