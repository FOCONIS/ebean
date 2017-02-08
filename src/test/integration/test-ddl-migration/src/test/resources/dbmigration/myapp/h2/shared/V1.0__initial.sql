-- apply changes
create table public.global_cachable (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_global_cachable primary key (id)
);

create table public.global_test_model (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_global_test_model primary key (id)
);

