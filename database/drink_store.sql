CREATE DATABASE IF NOT EXISTS brewpoint_pos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE brewpoint_pos;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS order_item_toppings;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product_sizes;
DROP TABLE IF EXISTS toppings;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'CASHIER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(20) NULL UNIQUE,
    email VARCHAR(120) NULL UNIQUE,
    active TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_employees_users
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    display_order INT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    product_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL UNIQUE,
    image_path VARCHAR(255) NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ck_products_stock CHECK (stock_quantity >= 0),
    CONSTRAINT fk_products_categories
        FOREIGN KEY (category_id) REFERENCES categories(category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    INDEX idx_products_category_active (category_id, active),
    INDEX idx_products_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_sizes (
    product_size_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    size_code VARCHAR(10) NOT NULL,
    size_name VARCHAR(50) NOT NULL,
    sale_price DECIMAL(12,0) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT uq_product_sizes UNIQUE (product_id, size_code),
    CONSTRAINT ck_product_sizes_price CHECK (sale_price > 0),
    CONSTRAINT fk_product_sizes_products
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE toppings (
    topping_id INT AUTO_INCREMENT PRIMARY KEY,
    topping_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    extra_price DECIMAL(12,0) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT ck_toppings_price CHECK (extra_price > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(30) NOT NULL UNIQUE,
    employee_id INT NOT NULL,
    order_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    subtotal DECIMAL(12,0) NOT NULL,
    discount_amount DECIMAL(12,0) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,0) NOT NULL,
    received_amount DECIMAL(12,0) NULL,
    change_amount DECIMAL(12,0) NULL,
    CONSTRAINT ck_orders_status CHECK (status IN ('COMPLETED', 'CANCELLED')),
    CONSTRAINT ck_orders_payment CHECK (payment_method IN ('CASH', 'MANUAL_BANK_TRANSFER')),
    CONSTRAINT ck_orders_amount CHECK (subtotal >= 0 AND discount_amount >= 0 AND total_amount >= 0),
    CONSTRAINT fk_orders_employees
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    INDEX idx_orders_time (order_time),
    INDEX idx_orders_employee_time (employee_id, order_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id INT NULL,
    product_code_snapshot VARCHAR(30) NOT NULL,
    product_name_snapshot VARCHAR(120) NOT NULL,
    size_code_snapshot VARCHAR(10) NOT NULL,
    size_name_snapshot VARCHAR(50) NOT NULL,
    base_price_snapshot DECIMAL(12,0) NOT NULL,
    unit_price DECIMAL(12,0) NOT NULL,
    quantity INT NOT NULL,
    line_total DECIMAL(12,0) NOT NULL,
    note VARCHAR(200) NULL,
    CONSTRAINT ck_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT ck_order_items_money CHECK (base_price_snapshot > 0 AND unit_price > 0 AND line_total >= 0),
    CONSTRAINT fk_order_items_orders
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_order_items_products
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    INDEX idx_order_items_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_item_toppings (
    order_item_topping_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_item_id BIGINT NOT NULL,
    topping_id INT NULL,
    topping_code_snapshot VARCHAR(30) NOT NULL,
    topping_name_snapshot VARCHAR(100) NOT NULL,
    extra_price_snapshot DECIMAL(12,0) NOT NULL,
    CONSTRAINT ck_order_item_toppings_price CHECK (extra_price_snapshot > 0),
    CONSTRAINT fk_order_item_toppings_items
        FOREIGN KEY (order_item_id) REFERENCES order_items(order_item_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_order_item_toppings_toppings
        FOREIGN KEY (topping_id) REFERENCES toppings(topping_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    INDEX idx_order_item_toppings_item (order_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (username, password_hash, role, active) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 1),
('cashier', 'b4c94003c562bb0d89535eca77f07284fe560fd48a7cc1ed99f0a56263d616ba', 'CASHIER', 1);

INSERT INTO employees (user_id, full_name, phone, email, active) VALUES
(1, 'Quản trị viên BrewPoint', '0900000001', 'admin@brewpoint.local', 1),
(2, 'Thu ngân demo', '0900000002', 'cashier@brewpoint.local', 1);

INSERT INTO categories (name, display_order, active) VALUES
('Cà phê Việt', 1, 1),
('Trà trái cây', 2, 1),
('Trà sữa', 3, 1),
('Matcha & cacao', 4, 1),
('Đá xay', 5, 1);

INSERT INTO products (category_id, product_code, name, image_path, stock_quantity, active) VALUES
(1, 'CF-DEN-DA', 'Cà phê phin đen đá', NULL, 80, 1),
(1, 'CF-SUA-DA', 'Cà phê sữa đá', NULL, 80, 1),
(1, 'BAC-XIU', 'Bạc xỉu', NULL, 70, 1),
(2, 'TRA-DAO-CAM-SA', 'Trà đào cam sả', NULL, 60, 1),
(2, 'TRA-SEN-VANG', 'Trà sen vàng', NULL, 55, 1),
(2, 'TRA-VAI', 'Trà vải', NULL, 60, 1),
(3, 'TS-TC-DD', 'Trà sữa trân châu đường đen', NULL, 65, 1),
(3, 'OLONG-SUA', 'Ô long sữa', NULL, 65, 1),
(3, 'HONG-TRA-SUA', 'Hồng trà sữa', NULL, 65, 1),
(4, 'MATCHA-LATTE', 'Matcha latte', NULL, 45, 1),
(4, 'CACAO-SUA-DA', 'Cacao sữa đá', NULL, 50, 1),
(5, 'MATCHA-DA-XAY', 'Matcha đá xay', NULL, 35, 1);

INSERT INTO product_sizes (product_id, size_code, size_name, sale_price, active) VALUES
(1, 'STD', 'Tiêu chuẩn', 25000, 1),
(2, 'STD', 'Tiêu chuẩn', 29000, 1),
(3, 'STD', 'Tiêu chuẩn', 32000, 1),
(4, 'M', 'Size M', 39000, 1),
(4, 'L', 'Size L', 49000, 1),
(5, 'M', 'Size M', 42000, 1),
(5, 'L', 'Size L', 52000, 1),
(6, 'M', 'Size M', 39000, 1),
(6, 'L', 'Size L', 49000, 1),
(7, 'M', 'Size M', 45000, 1),
(7, 'L', 'Size L', 55000, 1),
(8, 'M', 'Size M', 39000, 1),
(8, 'L', 'Size L', 49000, 1),
(9, 'M', 'Size M', 39000, 1),
(9, 'L', 'Size L', 49000, 1),
(10, 'M', 'Size M', 45000, 1),
(10, 'L', 'Size L', 55000, 1),
(11, 'M', 'Size M', 39000, 1),
(11, 'L', 'Size L', 49000, 1),
(12, 'M', 'Size M', 55000, 1),
(12, 'L', 'Size L', 65000, 1);

INSERT INTO toppings (topping_code, name, extra_price, active) VALUES
('TC-DEN', 'Trân châu đen', 10000, 1),
('TC-TRANG', 'Trân châu trắng', 10000, 1),
('THACH-CA-PHE', 'Thạch cà phê', 8000, 1),
('THACH-DUA', 'Thạch dừa', 8000, 1),
('PUDDING-TRUNG', 'Pudding trứng', 8000, 1),
('KEM-CHEESE', 'Kem cheese', 12000, 1),
('HAT-SEN', 'Hạt sen', 10000, 1);
