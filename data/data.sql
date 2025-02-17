DROP DATABASE IF EXISTS pay_my_buddy;
CREATE DATABASE pay_my_buddy;
USE pay_my_buddy;


DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL,
    CONSTRAINT unique_email UNIQUE (email)
);

DROP TABLE IF EXISTS user_relationships;
CREATE TABLE user_relationships (
    user_id INT NOT NULL,
    relationship_id INT NOT NULL,
    PRIMARY KEY (user_id, relationship_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_relationship FOREIGN KEY (relationship_id) REFERENCES users (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS transactions;
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users (id)
);


-- mot de passe "password123"

INSERT INTO users (username, email, password, balance)
VALUES
    ('Alice', 'alice@example.com', '$2a$12$Ql2L51UKSQlyPBLv5wy29.Ba23juBOZPxpt8qeY3jKkCWRJa56N2G', 100.00),
    ('Bob', 'bob@example.com', '$2a$10$9Bdq1nPgoM4jBcIbWwF7u.CZq8gXa.T2A3lS0u8MtLFs/ds64.T.y', 50.00),
    ('Charlie', 'charlie@example.com', '$2a$10$9Bdq1nPgoM4jBcIbWwF7u.CZq8gXa.T2A3lS0u8MtLFs/ds64.T.y', 25.00),
    ('David', 'david@example.com', '$2a$10$9Bdq1nPgoM4jBcIbWwF7u.CZq8gXa.T2A3lS0u8MtLFs/ds64.T.y', 10.00);

INSERT INTO user_relationships (user_id, relationship_id)
VALUES
    (1, 2);

INSERT INTO transactions (sender_id, receiver_id, description, amount)
VALUES
    (1, 2, 'Transaction entre amis', 15.00);
