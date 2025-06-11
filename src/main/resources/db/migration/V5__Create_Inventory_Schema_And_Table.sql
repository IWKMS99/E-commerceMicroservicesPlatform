CREATE SCHEMA IF NOT EXISTS inventory_management;

CREATE TABLE inventory_management.inventory
(
    product_id UUID PRIMARY KEY,
    quantity   INT NOT NULL CHECK (quantity >= 0)
);