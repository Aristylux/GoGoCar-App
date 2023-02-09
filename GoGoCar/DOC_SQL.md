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
    - [Modules gogocar](#modules-gogocar)
- [Insert](#insert)
  - [User](#user-1)
  - [Vehicles](#vehicles)
  - [Modules gogocar](#modules-gogocar-1)

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
    password VARCHAR(129) UNIQUE NOT NULL,
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
    id_user_book INTEGER,
    id_module INTEGER UNIQUE NOT NULL
    );
```

### Modules gogocar

```sql
CREATE TABLE modules (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(11) UNIQUE NOT NULL,
    mac_address VARCHAR(129) UNIQUE NOT NULL
    );
```
### Marques et Modeles voitures
```sql
CREATE TABLE `modeles` (
  `id` int(11) NOT NULL,
  `marque` varchar(255) NOT NULL,
  `modele` varchar(255) NOT NULL
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
INSERT INTO vehicles (model, licence_plate, address, id_owner, id_module) VALUES 
    ('Peugeot 105', 'YZ-875-AZ', '14 rue des romarins', 1, 3),
    ('Renault Mégane', 'PA-510-ND', '2 avenue Champ de Mars', 2, 4);
```

```sql
INSERT INTO vehicles (model, licence_plate, address, id_owner, id_module) VALUES 
    ('Nissan GT', 'BD-325-FE', '26 rue General de Gaulle', 3, 5),
    ('Peugeot 206', 'AM-871-DD', '18 Boulevard Jules Ferry', 3, 6);
```

```sql
INSERT INTO vehicles (model, licence_plate, address, id_owner, id_module) VALUES 
    ('Mini Cooper', 'ST-425-ZE', '10 Avenue Francois Cuzin', 3, 7),
    ('Fiat 500', 'PN-671-DT', '18 Boulevard Jules Ferry', 4, 8);
```

## Modules gogocar

```sql
INSERT INTO modules (name, mac_address) VALUES
    ('#01-01-0001', 'e0c6a87b46d582b0d5b5ca19cc5b0ba3d9e3ed79d113ebff9248b2f8ce5affdc52a044bd4dc8c1d70ffdf08256d7b68beff3a4ae6ae2582ad201cf8f4c6d47a9'),
    ('#01-01-0002', '29c063acbefc433fa96073ae50cec2d8f31748775a69ef0881c4af55bc86481e42f624407111d9a81acef775844f1532f7f30fcf88e4e6c2511598852dabcca4'),
    ('#01-01-0003', '1'),
    ('#01-01-0004', '2'),
    ('#01-01-0005', '3'),
    ('#01-01-0006', '4');
```
