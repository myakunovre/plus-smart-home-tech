DROP TABLE IF EXISTS deliveries CASCADE;

CREATE TABLE IF NOT EXISTS deliveries
(
    delivery_id UUID PRIMARY KEY,
    total_volume DOUBLE PRECISION,
    total_weight DOUBLE PRECISION,
    is_fragile BOOLEAN,

    from_country VARCHAR(255),
    from_city VARCHAR(255),
    from_street VARCHAR(255),
    from_house VARCHAR(255),
    from_flat VARCHAR(255),

    to_country VARCHAR(255),
    to_city VARCHAR(255),
    to_street VARCHAR(255),
    to_house VARCHAR(255),
    to_flat VARCHAR(255),

    state VARCHAR(12) CHECK (state IN ('CREATED', 'IN_PROGRESS', 'DELIVERED', 'FAILED', 'CANCELLED')),

    order_id UUID
);

