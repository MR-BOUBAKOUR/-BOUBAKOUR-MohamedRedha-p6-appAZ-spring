DROP DATABASE IF EXISTS pay_my_buddy;
CREATE DATABASE pay_my_buddy;
USE pay_my_buddy;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
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

DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_account FOREIGN KEY (user_id) REFERENCES users (id)
);

DROP TABLE IF EXISTS transactions;
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_account_id INT NOT NULL,
    receiver_account_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender_account FOREIGN KEY (sender_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_receiver_account FOREIGN KEY (receiver_account_id) REFERENCES accounts(id)
);

INSERT INTO users (username, email, password)
VALUES
    ('Alice', 'alice@example.com', '$2a$12$IHcw/w11QtHyvSsa/PkTcOxfU6y7ylauBe07d1ZIDaFWKeghOpHF6'),
    ('Bob', 'bob@example.com', '$2a$12$x3INJ0gPAbgqn3Xz4gQtQOxbmwngp/uPE.yFg3bLHmJEYibCvHcre'),
    ('Charlie', 'charlie@example.com', '$2a$12$WfggkW8z2STUMs1fsm0SouOFAgdtKwR5nZiQNe246ybMh/tGgNlma'),
    ('David', 'david@example.com', '$2a$12$iZnlODlu9HDgV8GLfh.sQ.yTDP/1ObLUVOU8lVo4LrsUEu5gY6A0y');

INSERT INTO accounts (user_id, balance, type)
VALUES
    (1, 100.00, 'personal'),
    (1, 500.00, 'business'),
    (2, 50.00, 'personal'),
    (3, 25.00, 'personal'),
    (4, 10.00, 'personal');

INSERT INTO user_relationships (user_id, relationship_id)
VALUES
    (1, 2),  -- Alice et Bob
    (2, 1),  -- Bob et Alice (relation bidirectionnelle potentielle ?)
    (1, 3);  -- Alice et Charlie

INSERT INTO transactions (sender_account_id, receiver_account_id, amount, description, type)
VALUES
    (1, 2, 50.00, 'Transfert de compte personnel à compte business', 'transfer'),
    (1, 1, 100.00, 'Dépôt initial sur compte personnel', 'deposit'),
    (2, 2, 200.00, 'Retrait sur compte business', 'withdrawal');
