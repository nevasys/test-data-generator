CREATE TABLE quality  (id bigint, description varchar(45));
CREATE TABLE customer (id bigint, name varchar(45), email varchar(45), country varchar(45), gender varchar(1), age int, quality int);
CREATE TABLE purchase_order (id bigint, product varchar(45), unit_price decimal(10,2), quantity  dec(8,2), custId bigint,  date bigint);
