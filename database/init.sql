create database "TicketManagementSystem";
\c "TicketManagementSystem"

drop schema if exists public cascade;
create schema public;

create sequence public.priority_level_seq start with 1 increment by 50;
create sequence public.attachment_seq start with 1 increment by 50;
create sequence public.product_seq start with 1 increment by 50;
create sequence public.profile_seq start with 1 increment by 50;
create sequence public.staff_seq start with 1 increment by 50;
create sequence public.ticket_seq start with 1 increment by 50;
create sequence public.ticket_status_seq start with 1 increment by 50;
create sequence public.chat_message_seq start with 1 increment by 50;
create sequence public.skill_seq start with 1 increment by 50;

create table public.priority_level
(
    id   integer      not null default nextval('public.priority_level_seq'),
    name varchar(255) not null,
    primary key (id),
    constraint uk_priority_level_name
        unique (name)
);

create table public.product
(
    id    integer      not null default nextval('public.product_seq'),
    brand varchar(255) not null,
    ean   varchar(13)  not null,
    name  varchar(255) not null,
    primary key (id),
    constraint uk_product_ean
        unique (ean)
);

create table public.profile
(
    id      integer      not null default nextval('public.profile_seq'),
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
    dtype   varchar(31)  not null ,
    id      integer      not null default nextval('public.staff_seq'),
    email   varchar(255) not null,
    name    varchar(255) not null,
    surname varchar(255) not null,
    primary key (id),
    constraint uk_staff_email
        unique (email)
);
create table public.skill
(
    id integer not null default nextval('public.skill_seq'),
    name varchar(255) not null,
    primary key (id)
);

create table public.staff_skill
(
    staff_id integer not null,
    skill_id integer not null,
    PRIMARY KEY(staff_id, skill_id),
    constraint fk_staff_skill_staff_id
        foreign key (staff_id) references public.staff,
    constraint fk_staff_skill_skill_id
        foreign key (skill_id) references public.skill
);

create table public.ticket
(
    id          integer       not null default nextval('public.ticket_seq'),
    description varchar(255) not null,
    customer_id integer      not null,
    product_id  integer      not null,
    expert_id   integer,
    priority_level_id integer,
    status smallint not null,
    primary key (id),
    constraint fk_ticket_customer_id
        foreign key (customer_id) references public.profile,
    constraint fk_ticket_product_id
        foreign key (product_id) references public.product,
    constraint fk_ticket_priority_id
        foreign key (priority_level_id) references public.priority_level
);

create table public.chat_message
(
    dtype              varchar(31)  not null,
    id                 integer      not null default  nextval('public.chat_message_seq'),
    body               varchar(255) not null,
    timestamp          timestamp(6) not null,
    ticket_id          integer      not null,
    customer_author_id integer      ,
    staff_author_id    integer      ,
    primary key (id),
    constraint fk_chat_message_customer_author_id
        foreign key (customer_author_id) references public.profile,
    constraint fk_chat_message_staff_author_id
        foreign key (staff_author_id) references public.profile,
    constraint fk_chat_message_ticket_id
        foreign key (ticket_id) references public.ticket
);

create table public.attachment
(
    id           integer       not null default nextval('public.attachment_seq'),
    name         varchar(255) not null,
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

create index ix_chat_message_customer_author_id
    on public.chat_message (customer_author_id desc);

create index ix_chat_message_staff_author_id
    on public.chat_message (staff_author_id desc);

create table public.ticket_status
(
    dtype       varchar(31) not null,
    id          integer      not null default nextval('public.ticket_status_seq'),
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

COPY public.profile(email, name, surname, address)
FROM '/docker-entrypoint-initdb.d/profile.csv'
WITH DELIMITER ',' CSV HEADER;

COPY public.priority_level(id,name)
    FROM '/docker-entrypoint-initdb.d/priority_level.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.ticket(description,customer_id,product_id,expert_id,priority_level_id,status)
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

