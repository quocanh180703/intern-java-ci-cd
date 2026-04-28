CREATE DATABASE library_db;
USE library_db;

CREATE TABLE items (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255),
                       isbn VARCHAR(50) UNIQUE,
                       type VARCHAR(50),
                       author VARCHAR(255),
                       total_copies INT NOT NULL DEFAULT 1,
                       available_copies INT NOT NULL DEFAULT 1
);

CREATE TABLE loans (
                       id VARCHAR(36) PRIMARY KEY,
                       user_id VARCHAR(36) NOT NULL,
                       item_id INT NOT NULL,
                       status VARCHAR(30) NOT NULL,
                       borrowed_at DATETIME NOT NULL,
                       due_at DATETIME NOT NULL,
                       returned_at DATETIME,
                       CONSTRAINT fk_loans_users FOREIGN KEY (user_id) REFERENCES users(id),
                       CONSTRAINT fk_loans_items FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE reservations (
                       id VARCHAR(36) PRIMARY KEY,
                       user_id VARCHAR(36) NOT NULL,
                       item_id INT NOT NULL,
                       status VARCHAR(30) NOT NULL,
                       created_at DATETIME NOT NULL,
                       fulfilled_at DATETIME,
                       priority INT NOT NULL,
                       CONSTRAINT fk_reservations_users FOREIGN KEY (user_id) REFERENCES users(id),
                       CONSTRAINT fk_reservations_items FOREIGN KEY (item_id) REFERENCES items(id),
                       INDEX idx_user_status(user_id, status),
                       INDEX idx_item_status(item_id, status)
);

CREATE TABLE fines (
                       id VARCHAR(36) PRIMARY KEY,
                       user_id VARCHAR(36) NOT NULL,
                       loan_id VARCHAR(36) NOT NULL,
                       amount DECIMAL(10,2) NOT NULL,
                       is_paid BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at DATE NOT NULL,
                       paid_at DATE,
                       CONSTRAINT fk_fines_users FOREIGN KEY (user_id) REFERENCES users(id),
                       CONSTRAINT fk_fines_loans FOREIGN KEY (loan_id) REFERENCES loans(id),
                       INDEX idx_user_paid(user_id, is_paid),
                       INDEX idx_loan(loan_id)
);

CREATE TABLE notifications (
                       id VARCHAR(36) PRIMARY KEY,
                       user_id VARCHAR(36) NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       message TEXT NOT NULL,
                       is_read BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at DATETIME NOT NULL,
                       read_at DATETIME,
                       CONSTRAINT fk_notifications_users FOREIGN KEY (user_id) REFERENCES users(id),
                       INDEX idx_user_read(user_id, is_read)
);