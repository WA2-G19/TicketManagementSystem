create database "TicketManagementSystem";
\c "TicketManagementSystem"

create table public.priority_level
(
    id   serial      not null,
    name varchar(255) not null,
    primary key (id),
    constraint uk_priority_level_name
        unique (name)
);

create table public.product
(
    id    serial      not null,
    brand varchar(255) not null,
    ean   varchar(13)  not null,
    name  varchar(255) not null,
    primary key (id),
    constraint uk_product_ean
        unique (ean)
);

create table public.profile
(
    id      serial      not null,
    email   varchar(255) not null,
    name    varchar(255) not null,
    surname varchar(255) not null,
    address varchar(255) not null,
    primary key (id),
    constraint uk_profile_email
        unique (email)
);

create table public.staff
(
    dtype   varchar(31)  not null,
    id      serial      not null,
    email   varchar(255) not null,
    name    varchar(255) not null,
    surname varchar(255) not null,
    primary key (id),
    constraint uk_staff_email
        unique (email)
);

create table public.ticket
(
    id          serial       not null,
    description varchar(255) not null,
    customer_id integer      not null,
    product_id  integer      not null,
    primary key (id),
    constraint fk_ticket_customer_id
        foreign key (customer_id) references public.profile,
    constraint fk_ticket_product_id
        foreign key (product_id) references public.product
);

create table public.chat_message
(
    id        serial       not null,
    body      varchar(255) not null,
    timestamp timestamp(6) not null,
    author_id integer      not null,
    ticket_id integer      not null,
    primary key (id),
    constraint fk_chat_message_author_id
        foreign key (author_id) references public.profile,
    constraint fk_chat_message_ticket_id
        foreign key (ticket_id) references public.ticket
);

create table public.attachment
(
    id           serial       not null,
    content      bytea        not null,
    content_type varchar(255) not null,
    length       integer      not null,
    timestamp    timestamp(6) not null,
    message_id   integer      not null,
    primary key (id),
    constraint fk_attachment_message_id
        foreign key (message_id) references public.chat_message
);

create index ix_chat_message_timestamp
    on public.chat_message (timestamp desc);

create index ix_chat_message_ticket_id
    on public.chat_message (ticket_id desc);

create index ix_chat_message_author_id
    on public.chat_message (author_id desc);

create table public.ticket_status
(
    dtype       varchar(31) not null,
    id          serial      not null,
    timestamp   timestamp(6),
    ticket_id   integer,
    by_id       integer,
    expert_id   integer,
    priority_id integer,
    primary key (id),
    constraint fk_ticket_status_ticket_id
        foreign key (ticket_id) references public.ticket,
    constraint fk_ticket_status_by_id
        foreign key (by_id) references public.staff,
    constraint fk_ticket_status_expert_id
        foreign key (expert_id) references public.staff,
    constraint fk_ticket_status_priority_id
        foreign key (priority_id) references public.priority_level
);

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