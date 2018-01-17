create table supplier (
    id uuid primary key,
    name varchar not null,
    address varchar not null,
    contact varchar,
    unique(name)
);

create table transaction (
    id uuid primary key,
    supplier_id uuid not null,
    created timestamp not null default current_timestamp,
    content varchar not null,
    foreign key (supplier_id) references supplier(id)
);

create index transaction_created ON transaction(created desc);
create index transaction_supplier_created ON transaction(supplier_id, created desc);