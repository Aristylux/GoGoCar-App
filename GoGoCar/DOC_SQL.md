# SQL Queries

This file save all big query for re-create GoGoCar database.

# Summary

- [SQL Queries](#sql-queries)
- [Summary](#summary)
- [Create](#create)
  - [Create database](#create-database)
  - [Create tables](#create-tables)
    - [User](#user)
    - [Vehicle](#vehicle)
- [Insert](#insert)
  - [User](#user-1)
  - [Vehicles](#vehicles)

# Create

## Create database

```sql
CREATE DATABASE gogocar WITH ENCODING = 'UTF-8';
```

## Create tables

### User

```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, 
    name VARCHAR(40) NOT NULL,
    email VARCHAR(40) UNIQUE NOT NULL,
    phone VARCHAR(14) UNIQUE NOT NULL,
    password VARCHAR(65) UNIQUE NOT NULL,
    id_identity INTEGER UNIQUE
    );
```

### Vehicle

```sql
CREATE TABLE vehicles (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, 
    model VARCHAR(40) NOT NULL, 
    licence_plate VARCHAR(10) UNIQUE NOT NULL,
    address VARCHAR(100) NOT NULL,
    id_owner INTEGER NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    is_booked BOOLEAN NOT NULL DEFAULT FALSE,
    id_user_book INTEGER
    );
```


# Insert

## User

password: 'admin'
hash: `hash = hashPassword("admin");`

```sql
INSERT INTO users(name, email, phone, password) VALUES
  ('Admin Admin', 'admin@admin.com', '06 05 04 03 02', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918');
```

## Vehicles

```sql
INSERT INTO vehicles (model, licence_plate, address, id_owner) VALUES 
    ('Peugeot 105', 'YZ-875-AZ', '14 rue des romarins', 1),
    ('Renault Mégane', 'PA-510-ND', '2 avenue Champ de Mars', 2);
```

```sql
INSERT INTO vehicles (model, licence_plate, address, id_owner) VALUES 
    ('Nissan GT', 'BD-325-FE', '26 rue General de Gaulle', 3),
    ('Peugeot 206', 'AM-871-DD', '18 Boulevard Jules Ferry', 3);
```