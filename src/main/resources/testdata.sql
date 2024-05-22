-- testdata.sql

-- Limpiar la base de datos antes de cada prueba
DELETE FROM cart_products;
DELETE FROM cart;

-- Restablecer los datos iniciales
INSERT INTO cart (id, user_id, updated_at) VALUES (1, 1, '2024-05-01 12:00:00');
INSERT INTO cart (id, user_id, updated_at) VALUES (2, 2, '2024-05-02 12:00:00');
INSERT INTO cart (id, user_id, updated_at) VALUES (3, 3, '2024-05-03 12:00:00');

-- Insert data into 'cart_products'
INSERT INTO cart_products ( cart_id, product_id, product_name, product_category_id, product_description, quantity, price) VALUES
(1, 1, 'Jacket', 1, 'Something indicate large central measure watch provide.', 1, 58.79),
(1, 2, 'Building Blocks', 2, 'Agent word occur number chair.', 2, 7.89),
(2, 3, 'Swimming Goggles', 3, 'Walk range media doctor interest.', 1, 30.53),
(3, 4, 'Football', 3, 'Country expect price certain different bag everyone.', 1, 21.93);
