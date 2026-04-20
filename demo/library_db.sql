CREATE DATABASE library_db;
USE library_db;

CREATE TABLE items (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255),
                       type VARCHAR(50),
                       author VARCHAR(255)
);