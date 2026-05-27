CREATE DATABASE IF NOT EXISTS drink_store
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE drink_store;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE') NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(120),
    address VARCHAR(255),
    active TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_employees_users
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_employees_email UNIQUE (email),
    CONSTRAINT uq_employees_phone UNIQUE (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(120) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_products_name UNIQUE (name),
    CONSTRAINT ck_products_price CHECK (price > 0),
    CONSTRAINT ck_products_stock CHECK (stock_quantity >= 0),
    CONSTRAINT fk_products_categories
        FOREIGN KEY (category_id) REFERENCES categories(category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12,2) NOT NULL,
    CONSTRAINT ck_orders_total CHECK (total_amount >= 0),
    CONSTRAINT fk_orders_employees
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    CONSTRAINT ck_order_details_quantity CHECK (quantity > 0),
    CONSTRAINT ck_order_details_price CHECK (unit_price > 0),
    CONSTRAINT ck_order_details_line_total CHECK (line_total >= 0),
    CONSTRAINT fk_order_details_orders
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_order_details_products
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (username, password_hash, role, active) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 1),
('nhanvien', '70ae1c421727d20eb63385ae5763f2d798dc4c0bd66663bdf0f13e7512f5767f', 'EMPLOYEE', 1);

INSERT INTO employees (user_id, full_name, phone, email, address, active) VALUES
(1, 'Quản trị viên', '0900000001', 'admin@drinkstore.local', 'Hà Nội', 1),
(2, 'Nhân viên bán hàng', '0900000002', 'nhanvien@drinkstore.local', 'Hồ Chí Minh', 1);

INSERT INTO categories (name, description) VALUES
('Cà phê', 'Các loại cà phê nóng và đá'),
('Trà sữa', 'Trà sữa và topping'),
('Nước ép', 'Nước ép trái cây tươi'),
('Soda', 'Soda pha chế'),
('Đồ ăn nhẹ', 'Bánh và đồ ăn nhẹ');

INSERT INTO products (category_id, name, price, stock_quantity, status) VALUES
(1, 'Cà phê sữa đá', 25000, 79, 'ACTIVE'),
(1, 'Bạc xỉu', 30000, 60, 'ACTIVE'),
(2, 'Trà sữa trân châu', 35000, 67, 'ACTIVE'),
(2, 'Trà sữa matcha', 38000, 45, 'ACTIVE'),
(3, 'Nước ép cam', 32000, 50, 'ACTIVE'),
(3, 'Nước ép dưa hấu', 28000, 55, 'ACTIVE'),
(4, 'Soda chanh', 30000, 40, 'ACTIVE'),
(5, 'Bánh flan', 18000, 35, 'ACTIVE');

INSERT INTO orders (employee_id, order_date, total_amount) VALUES
(2, '2026-05-27 09:30:00', 60000),
(2, '2026-05-27 10:15:00', 70000);

INSERT INTO order_details (order_id, product_id, quantity, unit_price, line_total) VALUES
(1, 1, 1, 25000, 25000),
(1, 3, 1, 35000, 35000),
(2, 3, 2, 35000, 70000);
