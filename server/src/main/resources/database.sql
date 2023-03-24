CREATE DATABASE TicketManagementSystem;

CREATE TABLE IF NOT EXISTS Product (
    ean varchar(15) PRIMARY KEY,
    name varchar(255),
    brand varchar(255)
);


CREATE TABLE IF NOT EXISTS Profile (

    email varchar(255) PRIMARY KEY,
    name varchar(255),
    surname varchar(255),
    password varchar(255),
    salt varchar(255)

);



