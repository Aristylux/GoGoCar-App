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
  - [Vehicles](#vehicles)

# Create

## Create database

```sql
CREATE DATABASE gogocar WITH ENCODING = 'UTF-8';
```

## Create tables

### User

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

## Vehicles

```sql
INSERT INTO vehicles (model, licence_plate, address, id_owner) VALUES 
    ('Peugeot 105', 'YZ-875-AZ', '14 rue des romarins 83000 TOULON', 1),
    ('Renault Mégane', 'PA-510-ND', '2 avenue Champ de Mars 83000 TOUON', 2);
```