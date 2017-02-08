-- apply changes
create table public.global_cachable (
  id                            serial not null,
  name                          varchar(255),
  constraint pk_global_cachable primary key (id)
);

create table public.global_test_model (
  id                            serial not null,
  name                          varchar(255),
  constraint pk_global_test_model primary key (id)
);

