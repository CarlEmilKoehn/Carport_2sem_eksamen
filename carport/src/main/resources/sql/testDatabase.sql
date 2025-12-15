BEGIN;

-- =========================
-- CREATE TEST SCHEMA
-- =========================
CREATE SCHEMA IF NOT EXISTS test;

-- =========================
-- ADMIN
-- =========================
CREATE TABLE IF NOT EXISTS test.admin (
                                          admin_email VARCHAR NOT NULL,
                                          admin_password VARCHAR NOT NULL,
                                          admin_firstname VARCHAR NOT NULL,
                                          admin_lastname VARCHAR NOT NULL,
                                          CONSTRAINT admin_pkey PRIMARY KEY (admin_email)
    );

-- =========================
-- CUSTOMER
-- =========================
CREATE TABLE IF NOT EXISTS test.customer (
                                             email VARCHAR NOT NULL,
                                             firstname VARCHAR NOT NULL,
                                             lastname VARCHAR NOT NULL,
                                             address VARCHAR NOT NULL,
                                             postal_code INTEGER NOT NULL,
                                             CONSTRAINT customer_pkey PRIMARY KEY (email)
    );

-- =========================
-- ROOF TYPE
-- =========================
CREATE TABLE IF NOT EXISTS test.roof_type (
                                              roof_type_id SERIAL PRIMARY KEY,
                                              roof_type_name VARCHAR NOT NULL,
                                              roof_type_deg INTEGER NOT NULL,
                                              roof_type_price NUMERIC(10,2) NOT NULL
    );

-- =========================
-- SHED
-- =========================
CREATE TABLE IF NOT EXISTS test.shed (
                                         shed_id SERIAL PRIMARY KEY,
                                         shed_width_mm INTEGER NOT NULL,
                                         shed_length_mm INTEGER NOT NULL
);

-- =========================
-- CUSTOMER ORDER
-- =========================
CREATE TABLE IF NOT EXISTS test.customer_order (
                                                   customer_order_id SERIAL PRIMARY KEY,
                                                   customer_email VARCHAR NOT NULL,
                                                   order_status VARCHAR NOT NULL,
                                                   roof_type_id INTEGER NOT NULL,
                                                   width_mm INTEGER NOT NULL,
                                                   length_mm INTEGER NOT NULL,
                                                   height_mm INTEGER NOT NULL,
                                                   shed_id INTEGER,
                                                   created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    order_price NUMERIC(10,2) NOT NULL
    );

-- =========================
-- CUSTOMER ORDER CHANGE
-- =========================
CREATE TABLE IF NOT EXISTS test.customer_order_change (
                                                          customer_order_change_id SERIAL PRIMARY KEY,
                                                          customer_order_id INTEGER NOT NULL,
                                                          admin_email VARCHAR NOT NULL,
                                                          admin_note VARCHAR,
                                                          created_at TIMESTAMPTZ NOT NULL_
