CREATE DATABASE "TicketManagementSystem";
\c "TicketManagementSystem"

CREATE TABLE public.product (
    ean varchar(15) PRIMARY KEY,
    name varchar(255),
    brand varchar(255)
);

CREATE TABLE public.profile (
    email varchar(255) PRIMARY KEY,
    name varchar(255),
    surname varchar(255)
);

COPY public.product(ean, name, brand)
FROM '/docker-entrypoint-initdb.d/product.csv'
WITH DELIMITER ',' CSV HEADER;

COPY public.profile(email, name, surname)
FROM '/docker-entrypoint-initdb.d/profile.csv'
WITH DELIMITER ',' CSV HEADER;