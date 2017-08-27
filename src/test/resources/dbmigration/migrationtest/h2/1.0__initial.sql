-- apply changes
create table e_basic (
  id                            integer auto_increment not null,
  status                        varchar(1),
  name                          varchar(255),
  description                   varchar(255),
  some_date                     timestamp,
  old_boolean                   boolean default false not null,
  old_boolean2                  boolean,
  constraint ck_e_basic_status check ( status in ('N','A','I')),
  constraint pk_e_basic primary key (id)
);

create index ix_e_basic_name on e_basic (name);
