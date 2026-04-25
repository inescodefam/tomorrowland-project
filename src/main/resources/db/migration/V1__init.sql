create table if not exists categories (
    id bigserial primary key,
    name varchar(255) not null unique,
    description varchar(255)
);

create table if not exists users (
    id bigserial primary key,
    username varchar(255) not null unique,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(20) not null
);

create table if not exists products (
    id bigserial primary key,
    name varchar(255) not null,
    description varchar(1024),
    price numeric(12,2) not null,
    stock integer not null,
    version bigint,
    category_id bigint references categories(id)
);

create table if not exists orders (
    id bigserial primary key,
    user_id bigint not null references users(id),
    total numeric(12,2),
    status varchar(20) not null,
    created_at timestamp not null
);

create table if not exists login_audit (
    id bigserial primary key,
    username varchar(255) not null,
    ip_address varchar(255) not null,
    created_at timestamp not null,
    success boolean not null
);
