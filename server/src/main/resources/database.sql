CREATE DATABASE TicketManagementSystem;

CREATE TABLE IF NOT EXISTS Product (
    ean varchar(15) PRIMARY KEY,
    name varchar(255),
    brand varchar(255)
);


CREATE EXTENSION citext;
CREATE DOMAIN email_type AS citext CHECK(
            VALUE ~ '^\w+@[a-zA-Z_]+?\[a-zA-Z]{2,3}$'
        );

CREATE TABLE IF NOT EXISTS Profile (

    email email_type PRIMARY KEY,
    name varchar(255),
    surname varchar(255),
    password varchar(255),
    salt varchar(255)

);

INSERT INTO Profile VALUES ('matteo@wa.polito.it', 'Matteo', 'Rosani')



