CREATE SCHEMA IF NOT EXISTS order_management;

CREATE TABLE order_management.orders
(
    id               UUID PRIMARY KEY,
    user_id          UUID NOT NULL,
    status           VARCHAR(50) NOT NULL,
    total_price      NUMERIC(10, 2) NOT NULL,
    shipping_address VARCHAR(255),
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_management.order_items
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   INT NOT NULL,
    price      NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_order
        FOREIGN KEY (order_id)
            REFERENCES order_management.orders (id)
);

CREATE INDEX idx_orders_user_id ON order_management.orders(user_id);