CREATE TABLE quality  (id bigint, description varchar);
CREATE TABLE customer (id bigint, name varchar, email varchar, country varchar, gender varchar, age int, quality int);
CREATE TABLE purchase_order (id bigint, product varchar, unit_price decimal(10,2), quantity  dec(8,2), customerId bigint, date timestamp);
