CREATE SCHEMA IF NOT EXISTS payment_management;

CREATE TABLE payment_management.payments
(
    id           UUID PRIMARY KEY,
    order_id     UUID NOT NULL UNIQUE,
    amount       NUMERIC(10, 2) NOT NULL,
    status       VARCHAR(50) NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);