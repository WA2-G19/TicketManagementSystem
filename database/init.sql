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
    id      uuid         not null,
    email   varchar(255) not null unique,
    name    varchar(255) not null,
    surname varchar(255) not null,
    address varchar(255) not null,
    primary key (id)
);

create table public.skill
(
    name varchar(255) not null,
    primary key (name)
);

create table public.staff
(
    dtype   varchar(31)  not null,
    id      uuid         not null,
    email   varchar(255) not null unique,
    name    varchar(255) not null,
    surname varchar(255) not null,
    primary key (id)
);

create table public.staff_skill
(
    staff_id uuid         not null,
    skill_id varchar(255) not null,
    primary key (staff_id, skill_id),
    constraint fk_staff_skill_skill
        foreign key (skill_id) references public.skill,
    constraint fk_staff_skill_staff
        foreign key (staff_id) references public.staff
);

create table public.vendor
(
    id            uuid         not null,
    address       varchar(255) not null,
    business_name varchar(255) not null,
    email         varchar(255) not null,
    phone_number  varchar(255) not null,
    primary key (id),
    constraint uk_vendor_business_name
        unique (business_name),
    constraint uk_vendor_email
        unique (email)
);

create table public.warranty
(
    id                   uuid         not null default gen_random_uuid(),
    activation_timestamp timestamp(6),
    creation_timestamp   timestamp(6) not null,
    duration             varchar(255) not null,
    customer_id          uuid,
    product_ean          varchar(13) not null,
    vendor_id            uuid         not null,
    primary key (id),
    constraint fk_warranty_customer
        foreign key (customer_id) references public.profile,
    constraint fk_warranty_product
        foreign key (product_ean) references public.product,
    constraint fk_warranty_vendor
        foreign key (vendor_id) references public.vendor
);

create table public.ticket
(
    id                  integer      not null default nextval('public.ticket_seq'),
    description         varchar(255) not null,
    status              smallint,
    expert_id           uuid,
    priority_level_name varchar(255),
    warranty_id         uuid         not null,
    primary key (id),
    constraint fk_ticket_expert
        foreign key (expert_id) references public.staff,
    constraint fk_ticket_priority_level
        foreign key (priority_level_name) references public.priority_level,
    constraint fk_ticket_warranty
        foreign key (warranty_id) references public.warranty
);

create table public.chat_message
(
    dtype              varchar(31)  not null,
    id                 integer      not null default nextval('public.chat_message_seq'),
    body               varchar(255) not null,
    read               boolean          not null,
    timestamp          timestamp(6) not null,
    ticket_id          integer      not null,
    customer_author_id uuid,
    staff_author_id    uuid,
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
    by_id         uuid,
    expert_id     uuid,
    priority_name varchar(255),
    primary key (id),
    constraint fk_ticket_status_ticket
        foreign key (ticket_id) references public.ticket,
    constraint fk_ticket_status_by
        foreign key (by_id) references public.staff,
    constraint fk_ticket_status_expert
        foreign key (expert_id) references public.staff,
    constraint fk_ticket_status_priority_level
        foreign key (priority_name) references public.priority_level
);

create index ix_ticket_status_timestamp
    on public.ticket_status (timestamp desc);

create index ix_ticket_status_ticket_id
    on public.ticket_status (ticket_id desc);

COPY public.product("ean", "name", "brand")
    FROM '/docker-entrypoint-initdb.d/product.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.profile("id", "email", "name", "surname", "address")
    FROM '/docker-entrypoint-initdb.d/profile.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.staff("id", "email", "name", "surname", "dtype")
    FROM '/docker-entrypoint-initdb.d/staff.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.vendor("id", "email", "business_name", "address", "phone_number")
    FROM '/docker-entrypoint-initdb.d/vendor.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.skill("name")
    FROM '/docker-entrypoint-initdb.d/skill.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.staff_skill("staff_id", "skill_id")
    FROM '/docker-entrypoint-initdb.d/staff_skill.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.priority_level("name")
    FROM '/docker-entrypoint-initdb.d/priority_level.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.warranty("id", "product_ean", "vendor_id", "customer_id", "duration", "creation_timestamp", "activation_timestamp")
    FROM '/docker-entrypoint-initdb.d/warranty.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.ticket("id", "description", "warranty_id", "expert_id", "priority_level_name", "status")
    FROM '/docker-entrypoint-initdb.d/ticket.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.ticket_status("dtype", "id", "timestamp", "ticket_id", "by_id", "expert_id", "priority_name")
    FROM '/docker-entrypoint-initdb.d/ticket_status.csv'
    WITH DELIMITER ',' CSV HEADER;

COPY public.chat_message("dtype", "id", "body", "read", "timestamp","ticket_id", "customer_author_id", "staff_author_id")
    FROM '/docker-entrypoint-initdb.d/chat_message.csv'
    WITH DELIMITER ',' CSV HEADER;
