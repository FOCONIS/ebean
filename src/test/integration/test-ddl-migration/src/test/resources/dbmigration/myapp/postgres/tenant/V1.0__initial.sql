-- apply changes
create table local_cachable (
  id                            serial not null,
  name                          varchar(255),
  constraint pk_local_cachable primary key (id)
);

create table local_model (
  id                            serial not null,
  global_test_model_id          integer,
  name                          varchar(255),
  constraint pk_local_model primary key (id)
);

create table model_under_test_a (
  id                            serial not null,
  status                        varchar(1),
  name                          varchar(255),
  description                   varchar(255),
  some_date                     timestamptz,
  test_model_id                 integer,
  constraint ck_model_under_test_a_status check ( status in ('N','A','I')),
  constraint pk_model_under_test_a primary key (id)
);

create index ix_model_under_test_a_name on model_under_test_a (name);
alter table local_model add constraint fk_local_model_global_test_model_id foreign key (global_test_model_id) references public.global_test_model (id) on delete restrict on update restrict;
create index ix_local_model_global_test_model_id on local_model (global_test_model_id);

alter table model_under_test_a add constraint fk_model_under_test_a_test_model_id foreign key (test_model_id) references public.global_test_model (id) on delete restrict on update restrict;
create index ix_model_under_test_a_test_model_id on model_under_test_a (test_model_id);

