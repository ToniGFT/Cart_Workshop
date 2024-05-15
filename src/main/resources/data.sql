-- Create the 'cart' table
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    updated_at TIMESTAMP
);

-- Create the 'cart_products' table
CREATE TABLE IF NOT EXISTS cart_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT,
    product_name VARCHAR(255),
    product_category VARCHAR(255),
    product_description VARCHAR(255),
    quantity INT,
    price DECIMAL(10, 2),
    FOREIGN KEY (cart_id) REFERENCES cart(id)
);

-- Insert data into 'cart'
INSERT INTO cart ( user_id, updated_at) VALUES ( 101, '2024-05-01 12:00:00');
INSERT INTO cart ( user_id, updated_at) VALUES ( 102, '2024-05-02 12:00:00');
INSERT INTO cart ( user_id, updated_at) VALUES ( 103, '2024-05-03 12:00:00');

-- Insert data into 'cart_products'
INSERT INTO cart_products ( cart_id, product_name, product_category, product_description, quantity, price) VALUES
(1, 'Apple MacBook Pro', 'Electronics', 'Latest model of Apple MacBook Pro 16 inch.', 1, 2399.99),
( 1, 'Logitech Mouse', 'Electronics', 'Wireless Logitech Mouse M235', 2, 29.99),
( 2, 'Adidas Running Shoes', 'Footwear', 'Adidas Ultraboost for men size 10', 1, 180.00),
( 3, 'Sony Headphones', 'Electronics', 'Sony WH-1000XM4 Noise Cancelling', 1, 348.50);