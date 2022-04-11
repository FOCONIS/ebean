-- Migrationscripts for ebean unittest
-- apply changes
create table migtest_ckey_assoc (
  id                            integer generated by default as identity (start with 1)  not null,
  assoc_one                     varchar(255),
  constraint pk_migtest_ckey_assoc primary key (id)
);

create table migtest_ckey_detail (
  id                            integer generated by default as identity (start with 1)  not null,
  something                     varchar(255),
  one_key                       integer,
  two_key                       varchar(127),
  constraint pk_migtest_ckey_detail primary key (id)
);

create table migtest_ckey_parent (
  one_key                       integer not null,
  two_key                       varchar(127) not null,
  name                          varchar(255),
  assoc_id                      integer,
  version                       integer not null,
  constraint pk_migtest_ckey_parent primary key (one_key,two_key)
);

create table migtest_fk_cascade (
  id                            bigint generated by default as identity (start with 1)  not null,
  one_id                        bigint,
  constraint pk_migtest_fk_cascade primary key (id)
);

create table migtest_fk_cascade_one (
  id                            bigint generated by default as identity (start with 1)  not null,
  constraint pk_migtest_fk_cascade_one primary key (id)
);

create table migtest_fk_none (
  id                            bigint generated by default as identity (start with 1)  not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none primary key (id)
);

create table migtest_fk_none_via_join (
  id                            bigint generated by default as identity (start with 1)  not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none_via_join primary key (id)
);

create table migtest_fk_one (
  id                            bigint generated by default as identity (start with 1)  not null,
  constraint pk_migtest_fk_one primary key (id)
);

create table migtest_fk_set_null (
  id                            bigint generated by default as identity (start with 1)  not null,
  one_id                        bigint,
  constraint pk_migtest_fk_set_null primary key (id)
);

create table migtest_e_basic (
  id                            integer generated by default as identity (start with 1)  not null,
  status                        varchar(1) default 'A' not null,
  status2                       varchar(127),
  name                          varchar(127),
  description                   varchar(127),
  some_date                     timestamp,
  new_string_field              varchar(255) default 'foo''bar' not null,
  new_boolean_field             boolean default true not null,
  new_boolean_field2            boolean default true not null,
  indextest1                    varchar(127),
  indextest2                    varchar(127),
  indextest3                    varchar(127),
  indextest4                    varchar(127),
  indextest5                    varchar(127),
  indextest6                    varchar(127),
  progress                      integer default 0 not null,
  new_integer                   integer default 42 not null,
  user_id                       integer,
  constraint ck_migtest_e_basic_status check ( status in ('N','A','I','?')),
  constraint ck_migtest_e_basic_progress check ( progress in (0,1,2)),
  constraint uq_migtest_e_basic_description unique (description),
  constraint uq_migtest_e_basic_status_indextest1 unique (status,indextest1),
  constraint uq_migtest_e_basic_name unique (name),
  constraint uq_migtest_e_basic_indextest4 unique (indextest4),
  constraint uq_migtest_e_basic_indextest5 unique (indextest5),
  constraint pk_migtest_e_basic primary key (id)
);

create table migtest_e_enum (
  id                            integer generated by default as identity (start with 1)  not null,
  test_status                   varchar(1),
  constraint pk_migtest_e_enum primary key (id)
);

create table migtest_e_history (
  id                            integer generated by default as identity (start with 1)  not null,
  test_string                   bigint,
  constraint pk_migtest_e_history primary key (id)
);
comment on table migtest_e_history is 'We have history now';
comment on column migtest_e_history.test_string is 'Column altered to long now';

create table migtest_e_history2 (
  id                            integer generated by default as identity (start with 1)  not null,
  test_string                   varchar(255) default 'unknown' not null,
  test_string2                  varchar(255),
  test_string3                  varchar(255) default 'unknown' not null,
  new_column                    varchar(20),
  constraint pk_migtest_e_history2 primary key (id)
);

create table migtest_e_history3 (
  id                            integer generated by default as identity (start with 1)  not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_history3 primary key (id)
);

create table migtest_e_history4 (
  id                            integer generated by default as identity (start with 1)  not null,
  test_number                   bigint,
  constraint pk_migtest_e_history4 primary key (id)
);

create table migtest_e_history5 (
  id                            integer generated by default as identity (start with 1)  not null,
  test_number                   integer,
  test_boolean                  boolean default false not null,
  constraint pk_migtest_e_history5 primary key (id)
);

create table migtest_e_history6 (
  id                            integer generated by default as identity (start with 1)  not null,
  test_number1                  integer default 42 not null,
  test_number2                  integer,
  constraint pk_migtest_e_history6 primary key (id)
);

create table migtest_e_softdelete (
  id                            integer generated by default as identity (start with 1)  not null,
  test_string                   varchar(255),
  deleted                       boolean default false not null,
  constraint pk_migtest_e_softdelete primary key (id)
);

create table migtest_e_user (
  id                            integer generated by default as identity (start with 1)  not null,
  constraint pk_migtest_e_user primary key (id)
);

create table migtest_mtm_c (
  id                            integer generated by default as identity (start with 1)  not null,
  name                          varchar(255),
  constraint pk_migtest_mtm_c primary key (id)
);

create table migtest_mtm_c_migtest_mtm_m (
  migtest_mtm_c_id              integer not null,
  migtest_mtm_m_id              bigint not null,
  constraint pk_migtest_mtm_c_migtest_mtm_m primary key (migtest_mtm_c_id,migtest_mtm_m_id)
);

create table migtest_mtm_m (
  id                            bigint generated by default as identity (start with 1)  not null,
  name                          varchar(255),
  constraint pk_migtest_mtm_m primary key (id)
);

create table migtest_mtm_m_migtest_mtm_c (
  migtest_mtm_m_id              bigint not null,
  migtest_mtm_c_id              integer not null,
  constraint pk_migtest_mtm_m_migtest_mtm_c primary key (migtest_mtm_m_id,migtest_mtm_c_id)
);

create table migtest_mtm_m_phone_numbers (
  migtest_mtm_m_id              bigint not null,
  value                         varchar(255) not null
);

create table migtest_oto_child (
  id                            integer generated by default as identity (start with 1)  not null,
  name                          varchar(255),
  master_id                     bigint,
  constraint uq_migtest_oto_child_master_id unique (master_id),
  constraint pk_migtest_oto_child primary key (id)
);

create table migtest_oto_master (
  id                            bigint generated by default as identity (start with 1)  not null,
  name                          varchar(255),
  constraint pk_migtest_oto_master primary key (id)
);

-- foreign keys and indices
create index ix_migtest_ckey_detail_parent on migtest_ckey_detail (one_key,two_key);
alter table migtest_ckey_detail add constraint fk_migtest_ckey_detail_parent foreign key (one_key,two_key) references migtest_ckey_parent (one_key,two_key) on delete restrict on update restrict;

create index ix_migtest_ckey_parent_assoc_id on migtest_ckey_parent (assoc_id);
alter table migtest_ckey_parent add constraint fk_migtest_ckey_parent_assoc_id foreign key (assoc_id) references migtest_ckey_assoc (id) on delete restrict on update restrict;

create index ix_migtest_fk_cascade_one_id on migtest_fk_cascade (one_id);
alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete restrict on update restrict;

create index ix_migtest_fk_none_one_id on migtest_fk_none (one_id);
alter table migtest_fk_none add constraint fk_migtest_fk_none_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;

create index ix_migtest_fk_none_via_join_one_id on migtest_fk_none_via_join (one_id);
alter table migtest_fk_none_via_join add constraint fk_migtest_fk_none_via_join_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;

create index ix_migtest_fk_set_null_one_id on migtest_fk_set_null (one_id);
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;

create index ix_migtest_e_basic_user_id on migtest_e_basic (user_id);
alter table migtest_e_basic add constraint fk_migtest_e_basic_user_id foreign key (user_id) references migtest_e_user (id) on delete restrict on update restrict;

create index ix_migtest_mtm_c_migtest_mtm_m_migtest_mtm_c on migtest_mtm_c_migtest_mtm_m (migtest_mtm_c_id);
alter table migtest_mtm_c_migtest_mtm_m add constraint fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_c foreign key (migtest_mtm_c_id) references migtest_mtm_c (id) on delete restrict on update restrict;

create index ix_migtest_mtm_c_migtest_mtm_m_migtest_mtm_m on migtest_mtm_c_migtest_mtm_m (migtest_mtm_m_id);
alter table migtest_mtm_c_migtest_mtm_m add constraint fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_m foreign key (migtest_mtm_m_id) references migtest_mtm_m (id) on delete restrict on update restrict;

create index ix_migtest_mtm_m_migtest_mtm_c_migtest_mtm_m on migtest_mtm_m_migtest_mtm_c (migtest_mtm_m_id);
alter table migtest_mtm_m_migtest_mtm_c add constraint fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_m foreign key (migtest_mtm_m_id) references migtest_mtm_m (id) on delete restrict on update restrict;

create index ix_migtest_mtm_m_migtest_mtm_c_migtest_mtm_c on migtest_mtm_m_migtest_mtm_c (migtest_mtm_c_id);
alter table migtest_mtm_m_migtest_mtm_c add constraint fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_c foreign key (migtest_mtm_c_id) references migtest_mtm_c (id) on delete restrict on update restrict;

create index ix_migtest_mtm_m_phone_numbers_migtest_mtm_m_id on migtest_mtm_m_phone_numbers (migtest_mtm_m_id);
alter table migtest_mtm_m_phone_numbers add constraint fk_migtest_mtm_m_phone_numbers_migtest_mtm_m_id foreign key (migtest_mtm_m_id) references migtest_mtm_m (id) on delete restrict on update restrict;

alter table migtest_oto_child add constraint fk_migtest_oto_child_master_id foreign key (master_id) references migtest_oto_master (id) on delete restrict on update restrict;

create index ix_migtest_e_basic_indextest3 on migtest_e_basic (indextest3);
create index ix_migtest_e_basic_indextest6 on migtest_e_basic (indextest6);