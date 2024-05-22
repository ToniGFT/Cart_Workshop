-- testdata.sql

-- Limpiar la base de datos antes de cada prueba
DELETE FROM cart_products;
DELETE FROM cart;

-- Restablecer los datos iniciales
INSERT INTO cart (id, user_id, updated_at) VALUES (1, 1, '2024-05-01 12:00:00');
INSERT INTO cart (id, user_id, updated_at) VALUES (2, 2, '2024-05-02 12:00:00');
INSERT INTO cart (id, user_id, updated_at) VALUES (3, 3, '2024-05-03 12:00:00');

INSERT INTO cart_products (id, cart_id, product_id, product_name, product_category, product_description, quantity, price) VALUES (1, 1, 1, 'Apple MacBook Pro', 'Electronics', 'Latest model of Apple MacBook Pro 16 inch.', 1, 2399.99);
INSERT INTO cart_products (id, cart_id, product_id, product_name, product_category, product_description, quantity, price) VALUES (2, 1, 2, 'Logitech Mouse', 'Electronics', 'Wireless Logitech Mouse M235', 2, 29.99);
INSERT INTO cart_products (id, cart_id, product_id, product_name, product_category, product_description, quantity, price) VALUES (3, 2, 3, 'Adidas Running Shoes', 'Footwear', 'Adidas Ultraboost for men size 10', 1, 180.00);
INSERT INTO cart_products (id, cart_id, product_id, product_name, product_category, product_description, quantity, price) VALUES (4, 3, 4, 'Sony Headphones', 'Electronics', 'Sony WH-1000XM4 Noise Cancelling', 1, 348.50);
