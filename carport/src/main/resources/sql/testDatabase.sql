BEGIN;

-- =========================
-- RESET TEST SCHEMA
-- =========================
DROP SCHEMA IF EXISTS test CASCADE;
CREATE SCHEMA test;

SET search_path TO test;

-- =========================
-- TABLES (same as public)
-- =========================

CREATE TABLE admin (
                       admin_email VARCHAR PRIMARY KEY,
                       admin_password VARCHAR NOT NULL,
                       admin_firstname VARCHAR NOT NULL,
                       admin_lastname VARCHAR NOT NULL
);

CREATE TABLE customer (
                          email VARCHAR PRIMARY KEY,
                          firstname VARCHAR NOT NULL,
                          lastname VARCHAR NOT NULL,
                          address VARCHAR NOT NULL,
                          postal_code INTEGER NOT NULL
);

CREATE TABLE roof_type (
                           roof_type_id SERIAL PRIMARY KEY,
                           roof_type_name VARCHAR NOT NULL,
                           roof_type_deg INTEGER NOT NULL,
                           roof_type_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE shed (
                      shed_id SERIAL PRIMARY KEY,
                      shed_width_mm INTEGER NOT NULL,
                      shed_length_mm INTEGER NOT NULL
);

CREATE TABLE material_category (
                                   material_category_id SERIAL PRIMARY KEY,
                                   material_category_name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE unit (
                      unit_id SERIAL PRIMARY KEY,
                      unit_name VARCHAR NOT NULL,
                      unit_short_name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE material_product (
                                  material_product_id SERIAL PRIMARY KEY,
                                  material_category_id INTEGER NOT NULL REFERENCES material_category(material_category_id),
                                  unit_id INTEGER NOT NULL REFERENCES unit(unit_id),
                                  length_mm INTEGER,
                                  material_product_name VARCHAR NOT NULL,
                                  material_product_description TEXT NOT NULL,
                                  material_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE customer_order (
                                customer_order_id SERIAL PRIMARY KEY,
                                customer_email VARCHAR NOT NULL REFERENCES customer(email),
                                order_status VARCHAR NOT NULL,
                                roof_type_id INTEGER NOT NULL REFERENCES roof_type(roof_type_id),
                                width_mm INTEGER NOT NULL,
                                length_mm INTEGER NOT NULL,
                                height_mm INTEGER NOT NULL,
                                shed_id INTEGER REFERENCES shed(shed_id),
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                order_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE order_material (
                                order_material_id SERIAL PRIMARY KEY,
                                customer_order_id INTEGER NOT NULL REFERENCES customer_order(customer_order_id),
                                material_product_id INTEGER NOT NULL REFERENCES material_product(material_product_id),
                                quantity INTEGER NOT NULL,
                                note TEXT,
                                total_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE customer_order_change (
                                       customer_order_change_id SERIAL PRIMARY KEY,
                                       customer_order_id INTEGER NOT NULL REFERENCES customer_order(customer_order_id),
                                       admin_email VARCHAR NOT NULL REFERENCES admin(admin_email),
                                       admin_note VARCHAR,
                                       created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- SEED LOOKUP DATA
-- =========================

-- Categories
INSERT INTO material_category (material_category_name) VALUES
                                                           ('Træ og Tagplader'),
                                                           ('Beslag og Skruer'),
                                                           ('Stolper'),
                                                           ('Rem/Spær'),
                                                           ('Understern'),
                                                           ('Overstern'),
                                                           ('Beklædning'),
                                                           ('Vandbræt'),
                                                           ('Tagplader'),
                                                           ('Løsholter'),
                                                           ('Lægter');

-- Units
INSERT INTO unit (unit_name, unit_short_name) VALUES
                                                  ('Styk',  'Stk'),
                                                  ('Pakke', 'Pakke'),
                                                  ('Rulle', 'Rulle'),
                                                  ('Sæt',   'Sæt');

-- Roof type (needed for tests)
INSERT INTO roof_type (roof_type_name, roof_type_deg, roof_type_price)
VALUES ('Plastmo fladt tag', 0, 0.00);

-- =========================
-- SEED MATERIAL PRODUCTS
-- (Only what tests need)
-- =========================

-- Stolpe (used in findPostForLength)
INSERT INTO material_product (
    material_category_id, unit_id, length_mm,
    material_product_name, material_product_description, material_price
) VALUES (
             (SELECT material_category_id FROM material_category WHERE material_category_name='Stolper'),
             (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
             180,
             '97x97 mm. trykimp. Stolpe',
             'Stolper nedgraves 90 cm. i jord',
             82.70
         );

-- Rem / Spær
INSERT INTO material_product VALUES
    (DEFAULT,
     (SELECT material_category_id FROM material_category WHERE material_category_name='Rem/Spær'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     360,
     '45x195 mm. spærtræ ubh.',
     'Rem / Spær',
     190.61
    );

-- Understern
INSERT INTO material_product VALUES
    (DEFAULT,
     (SELECT material_category_id FROM material_category WHERE material_category_name='Understern'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     360,
     '25x200 mm. trykimp. Brædt',
     'Understern',
     171.21
    );

-- Overstern
INSERT INTO material_product VALUES
    (DEFAULT,
     (SELECT material_category_id FROM material_category WHERE material_category_name='Overstern'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     360,
     '25x125 mm. trykimp. Brædt',
     'Overstern',
     125.81
    );

-- Rafter
INSERT INTO material_product VALUES
    (DEFAULT,
     (SELECT material_category_id FROM material_category WHERE material_category_name='Rem/Spær'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     540,
     '45x195 mm. spærtræ ubh.',
     'Rafter',
     254.15
    );

-- Roof sheet
INSERT INTO material_product VALUES
    (DEFAULT,
     (SELECT material_category_id FROM material_category WHERE material_category_name='Tagplader'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     300,
     'Plastmo Ecolite blåtonet',
     'Tagplade',
     179.00
    );

-- =========================
-- SEED TEST ORDER DATA
-- =========================

INSERT INTO customer VALUES
    ('a@a.dk', 'A', 'A', 'Testvej 1', 1234);

INSERT INTO customer_order (
    customer_email, order_status, roof_type_id,
    width_mm, length_mm, height_mm, order_price
) VALUES (
             'a@a.dk', 'PENDING', 1,
             600, 600, 250, 1000.00
         );

INSERT INTO order_material (
    customer_order_id, material_product_id, quantity, total_price
) VALUES (
             1, 1, 2, 165.40
         );

COMMIT;
