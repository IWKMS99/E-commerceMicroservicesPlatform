CREATE SCHEMA IF NOT EXISTS catalog_management;

CREATE TABLE catalog_management.categories
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);