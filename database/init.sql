CREATE DATABASE "TicketManagementSystem";
\c "TicketManagementSystem"

create table public.priority_level
(
    name varchar(255) not null
        primary key
)

create table public.product
(
    ean   varchar(13)  not null
        primary key,
    brand varchar(255) not null,
    name  varchar(255) not null
)

create table public.profile
(
    email   varchar(255) not null
        primary key,
    name    varchar(255) not null,
    surname varchar(255) not null,
    address varchar(255) not null
)

create table public.staff
(
    dtype   varchar(31)  not null,
    email   varchar(255) not null
        primary key,
    name    varchar(255) not null,
    surname varchar(255) not null
)

create table public.ticket
(
    id             integer      not null
        primary key,
    description    varchar(255) not null,
    customer_email varchar(255)
        constraint fkqbgiq76dwysc70toh2q4rmutd
            references public.profile
)

create table public.chat_message
(
    id        integer not null
        primary key,
    body      varchar(255),
    timestamp timestamp(6),
    author_id varchar(255)
        constraint fk8ulxd8qorp1su1r9jutt1cfyg
            references public.profile,
    ticket_id integer
        constraint fk4hcodrfek3rk0qu7j6ywwgysr
            references public.ticket
)

create table public.attachment
(
    id           integer not null
        primary key,
    content      bytea,
    content_type varchar(255),
    length       integer not null,
    timestamp    timestamp(6),
    message_id   integer
        constraint fk4j3fl63kp0oa3m424k1avyjv8
            references public.chat_message
)

create index ix_chat_message_timestamp
    on public.chat_message (timestamp desc);

create index ix_chat_message_ticket_id
    on public.chat_message (ticket_id desc);

create index ix_chat_message_author_id
    on public.chat_message (author_id desc);

create table public.ticket_status
(
    dtype         varchar(31) not null,
    id            integer     not null
        primary key,
    timestamp     timestamp(6),
    ticket_id     integer
        constraint fkc5crr2kjup6so4cfoslpc0a5l
            references public.ticket,
    by_email      varchar(255)
        constraint fk6dktnquron6sadmqobxgmgkd6
            references public.staff,
    expert_email  varchar(255)
        constraint fkkmhjfqan4l077y5ojuwedhnvq
            references public.staff,
    priority_name varchar(255)
        constraint fkfvllflb4ru7ahun3nam4nrcjo
            references public.priority_level
)

create index ix_ticket_status_timestamp
    on public.ticket_status (timestamp desc);

create index ix_ticket_status_ticket_id
    on public.ticket_status (ticket_id desc);

COPY public.product(ean, name, brand)
FROM '/docker-entrypoint-initdb.d/product.csv'
WITH DELIMITER ',' CSV HEADER;

COPY public.profile(email, name, surname)
FROM '/docker-entrypoint-initdb.d/profile.csv'
WITH DELIMITER ',' CSV HEADER;