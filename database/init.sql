create database "TicketManagementSystem";
\c "TicketManagementSystem"

drop schema if exists public cascade;
create schema public;

create sequence public.attachment_seq
    increment by 50;

create sequence public.chat_message_seq
    increment by 50;

create sequence public.ticket_seq
    increment by 50;

create sequence public.ticket_status_seq
    increment by 50;

create table public.priority_level
(
    name varchar(255) not null,
    primary key (name)
);

create table public.product
(
    ean   varchar(13)  not null,
    brand varchar(255) not null,
    name  varchar(255) not null,
    primary key (ean)
);

create table public.profile
(
    email   varchar(255) not null,
    name    varchar(255) not null,
    surname varchar(255) not null,
    address varchar(255) not null,
    primary key (email)
);

create table public.skill
(
    name varchar(255) not null,
    primary key (name)
);

create table public.staff
(
    dtype   varchar(31)  not null,
    email   varchar(255) not null,
    name    varchar(255) not null,
    surname varchar(255) not null,
    primary key (email)
);

create table public.staff_skill
(
    staff_id varchar(255) not null,
    skill_id varchar(255) not null,
    primary key (staff_id, skill_id),
    constraint fk_staff_skill_skill
        foreign key (skill_id) references public.skill,
    constraint fk_staff_skill_staff
        foreign key (staff_id) references public.staff
);

create table public.ticket
(
    id                  integer      not null default nextval('public.ticket_seq'),
    description         varchar(255) not null,
    status              smallint,
    customer_email      varchar(255) not null,
    expert_email        varchar(255),
    priority_level_name varchar(255),
    product_ean         varchar(255) not null,
    primary key (id),
    constraint fk_ticket_customer
        foreign key (customer_email) references public.profile,
    constraint fk_ticket_expert
        foreign key (expert_email) references public.staff,
    constraint fk_ticket_priority_level
        foreign key (priority_level_name) references public.priority_level,
    constraint fk_ticket_product
        foreign key (product_ean) references public.product
);

create table public.chat_message
(
    dtype              varchar(31)  not null,
    id                 integer      not null default nextval('public.chat_message_seq'),
    body               varchar(255) not null,
    timestamp          timestamp(6) not null,
    ticket_id          integer      not null,
    customer_author_id varchar(255),
    staff_author_id    varchar(255),
    primary key (id),
    constraint fk_chat_message_ticket
        foreign key (ticket_id) references public.ticket,
    constraint fk_chat_message_customer_author
        foreign key (customer_author_id) references public.profile,
    constraint fk_chat_message_staff_author
        foreign key (staff_author_id) references public.staff
);

create table public.attachment
(
    id           integer      not null default nextval('public.attachment_seq'),
    content      bytea        not null,
    content_type varchar(255) not null,
    length       integer      not null,
    name         varchar(255) not null,
    timestamp    timestamp(6) not null,
    message_id   integer      not null,
    primary key (id),
    constraint fk_attachment_chat_message
        foreign key (message_id) references public.chat_message
);

create index ix_chat_message_timestamp
    on public.chat_message (timestamp desc);

create index ix_chat_message_ticket_id
    on public.chat_message (ticket_id desc);

create index ix_chat_message_customer_author_id
    on public.chat_message (customer_author_id desc);

create index ix_chat_message_staff_author_id
    on public.chat_message (staff_author_id desc);

create table public.ticket_status
(
    dtype         varchar(31)  not null,
    id            integer      not null default nextval('public.ticket_status_seq'),
    timestamp     timestamp(6) not null,
    ticket_id     integer,
    by_email      varchar(255),
    expert_email  varchar(255),
    priority_name varchar(255),
    primary key (id),
    constraint fk_ticket_status_ticket
        foreign key (ticket_id) references public.ticket,
    constraint fk_ticket_status_by
        foreign key (by_email) references public.staff,
    constraint fk_ticket_status_expert
        foreign key (expert_email) references public.staff,
    constraint fk_ticket_status_priority_level
        foreign key (priority_name) references public.priority_level
);

create index ix_ticket_status_timestamp
    on public.ticket_status (timestamp desc);

create index ix_ticket_status_ticket_id
    on public.ticket_status (ticket_id desc);

COPY public.product(ean, name, brand)
FROM '/docker-entrypoint-initdb.d/product.csv'
WITH DELIMITER ',' CSV HEADER;

COPY public.profile(email, name, surname, address)
FROM '/docker-entrypoint-initdb.d/profile.csv'
WITH DELIMITER ',' CSV HEADER;

COPY public.priority_level(name)
    FROM '/docker-entrypoint-initdb.d/priority_level.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.ticket(description,customer_email,product_ean,expert_email,priority_level_name,status)
    FROM '/docker-entrypoint-initdb.d/ticket.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.staff(email, name, surname, dtype)
    FROM '/docker-entrypoint-initdb.d/staff.csv'
    WITH DELIMITER ',' CSV HEADER;


COPY public.skill(name)
    FROM '/docker-entrypoint-initdb.d/skill.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.staff_skill(staff_id, skill_id)
    FROM '/docker-entrypoint-initdb.d/staff_skill.csv'
    WITH DELIMITER ',' CSV HEADER;