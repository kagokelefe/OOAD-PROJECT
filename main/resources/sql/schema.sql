-- Schema for banking-app (H2)

CREATE TABLE IF NOT EXISTS users (
  id IDENTITY PRIMARY KEY,
  full_name VARCHAR(200),
  address VARCHAR(255),
  marital_status VARCHAR(50),
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100),
  password_hash VARCHAR(256),
  role VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS accounts (
  id IDENTITY PRIMARY KEY,
  user_id BIGINT NOT NULL,
  account_number VARCHAR(50) NOT NULL UNIQUE,
  balance DOUBLE,
  type VARCHAR(50),
  extra VARCHAR(255),
  employer_name VARCHAR(200),
  employer_address VARCHAR(255),
  status VARCHAR(20) DEFAULT 'PENDING',
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS transactions (
  id IDENTITY PRIMARY KEY,
  account_id BIGINT NOT NULL,
  amount DOUBLE,
  type VARCHAR(50),
  timestamp TIMESTAMP,
  description VARCHAR(255),
  FOREIGN KEY (account_id) REFERENCES accounts(id)
);
