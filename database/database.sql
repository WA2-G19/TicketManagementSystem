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
    surname varchar(255),
    password varchar(255),
    salt varchar(255)
);