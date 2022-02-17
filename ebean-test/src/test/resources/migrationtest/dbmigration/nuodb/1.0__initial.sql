-- Migrationscripts for ebean unittest
-- apply changes
create table migtest_ckey_assoc (
  id                            integer generated by default as identity not null,
  assoc_one                     varchar(255),
  constraint pk_migtest_ckey_assoc primary key (id)
);

create table migtest_ckey_detail (
  id                            integer generated by default as identity not null,
  something                     varchar(255),
  constraint pk_migtest_ckey_detail primary key (id)
);

create table migtest_ckey_parent (
  one_key                       integer not null,
  two_key                       varchar(127) not null,
  name                          varchar(255),
  version                       integer not null,
  constraint pk_migtest_ckey_parent primary key (one_key,two_key)
);

create table migtest_fk_cascade (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_cascade primary key (id)
);

create table migtest_fk_cascade_one (
  id                            bigint generated by default as identity not null,
  constraint pk_migtest_fk_cascade_one primary key (id)
);

create table migtest_fk_none (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none primary key (id)
);

create table migtest_fk_none_via_join (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none_via_join primary key (id)
);

create table migtest_fk_one (
  id                            bigint generated by default as identity not null,
  constraint pk_migtest_fk_one primary key (id)
);

create table migtest_fk_set_null (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_set_null primary key (id)
);

create table migtest_e_basic (
  id                            integer generated by default as identity not null,
  status                        varchar(1),
  status2                       varchar(1) default 'N' not null,
  name                          varchar(127),
  description                   varchar(127),
  some_date                     timestamp,
  old_boolean                   boolean default false not null,
  old_boolean2                  boolean,
  eref_id                       integer,
  indextest1                    varchar(127),
  indextest2                    varchar(127),
  indextest3                    varchar(127),
  indextest4                    varchar(127),
  indextest5                    varchar(127),
  indextest6                    varchar(127),
  user_id                       integer not null,
  constraint ck_migtest_e_basic_status check ( status in ('N','A','I')),
  constraint ck_migtest_e_basic_status2 check ( status2 in ('N','A','I')),
  constraint uq_migtest_e_basic_indextest2 unique (indextest2),
  constraint uq_migtest_e_basic_indextest6 unique (indextest6),
  constraint pk_migtest_e_basic primary key (id)
);

create table migtest_e_enum (
  id                            integer generated by default as identity not null,
  test_status                   varchar(1),
  constraint ck_migtest_e_enum_test_status check ( test_status in ('N','A','I')),
  constraint pk_migtest_e_enum primary key (id)
);

create table migtest_e_history (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_history primary key (id)
);

create table migtest_e_history2 (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  obsolete_string1              varchar(255),
  obsolete_string2              varchar(255),
  constraint pk_migtest_e_history2 primary key (id)
);

create table migtest_e_history3 (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_history3 primary key (id)
);

create table migtest_e_history4 (
  id                            integer generated by default as identity not null,
  test_number                   integer,
  constraint pk_migtest_e_history4 primary key (id)
);

create table migtest_e_history5 (
  id                            integer generated by default as identity not null,
  test_number                   integer,
  constraint pk_migtest_e_history5 primary key (id)
);

create table migtest_e_history6 (
  id                            integer generated by default as identity not null,
  test_number1                  integer,
  test_number2                  integer not null,
  constraint pk_migtest_e_history6 primary key (id)
);

create table migtest_e_ref (
  id                            integer generated by default as identity not null,
  name                          varchar(127) not null,
  constraint uq_migtest_e_ref_name unique (name),
  constraint pk_migtest_e_ref primary key (id)
);

create table migtest_e_softdelete (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_softdelete primary key (id)
);

create table migtest_mtm_c (
  id                            integer generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_mtm_c primary key (id)
);

create table migtest_mtm_m (
  id                            bigint generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_mtm_m primary key (id)
);

create table migtest_oto_child (
  id                            integer generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_oto_child primary key (id)
);

create table migtest_oto_master (
  id                            bigint generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_oto_master primary key (id)
);

-- apply foreign keys
create index ix_migtest_fk_cascade_one_id on migtest_fk_cascade (one_id);
alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade;

create index ix_migtest_fk_set_null_one_id on migtest_fk_set_null (one_id);
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id);

create index ix_migtest_e_basic_eref_id on migtest_e_basic (eref_id);
alter table migtest_e_basic add constraint fk_migtest_e_basic_eref_id foreign key (eref_id) references migtest_e_ref (id);

create index ix_migtest_e_basic_indextest1 on migtest_e_basic (indextest1);
create index ix_migtest_e_basic_indextest5 on migtest_e_basic (indextest5);
-- apply history view
alter table migtest_e_history2 add column sys_period_start datetime(6) default now();
alter table migtest_e_history2 add column sys_period_end datetime(6);
create table migtest_e_history2_history(
  id                            integer,
  test_string                   varchar(255),
  obsolete_string1              varchar(255),
  obsolete_string2              varchar(255),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;

alter table migtest_e_history3 add column sys_period_start datetime(6) default now();
alter table migtest_e_history3 add column sys_period_end datetime(6);
create table migtest_e_history3_history(
  id                            integer,
  test_string                   varchar(255),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history3_with_history as select * from migtest_e_history3 union all select * from migtest_e_history3_history;

alter table migtest_e_history4 add column sys_period_start datetime(6) default now();
alter table migtest_e_history4 add column sys_period_end datetime(6);
create table migtest_e_history4_history(
  id                            integer,
  test_number                   integer,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history4_with_history as select * from migtest_e_history4 union all select * from migtest_e_history4_history;

alter table migtest_e_history5 add column sys_period_start datetime(6) default now();
alter table migtest_e_history5 add column sys_period_end datetime(6);
create table migtest_e_history5_history(
  id                            integer,
  test_number                   integer,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history5_with_history as select * from migtest_e_history5 union all select * from migtest_e_history5_history;

alter table migtest_e_history6 add column sys_period_start datetime(6) default now();
alter table migtest_e_history6 add column sys_period_end datetime(6);
create table migtest_e_history6_history(
  id                            integer,
  test_number1                  integer,
  test_number2                  integer,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history6_with_history as select * from migtest_e_history6 union all select * from migtest_e_history6_history;

-- apply history trigger
delimiter $$
create or replace trigger migtest_e_history2_history_upd for migtest_e_history2 before update for each row as 
    NEW.sys_period_start = greatest(current_timestamp, date_add(OLD.sys_period_start, interval 1 microsecond));
    insert into migtest_e_history2_history (sys_period_start,sys_period_end,id, test_string, obsolete_string2) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_string, OLD.obsolete_string2);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history2_history_del for migtest_e_history2 before delete for each row as
    insert into migtest_e_history2_history (sys_period_start,sys_period_end,id, test_string, obsolete_string2) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_string, OLD.obsolete_string2);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history3_history_upd for migtest_e_history3 before update for each row as 
    NEW.sys_period_start = greatest(current_timestamp, date_add(OLD.sys_period_start, interval 1 microsecond));
    insert into migtest_e_history3_history (sys_period_start,sys_period_end,id, test_string) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_string);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history3_history_del for migtest_e_history3 before delete for each row as
    insert into migtest_e_history3_history (sys_period_start,sys_period_end,id, test_string) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_string);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history4_history_upd for migtest_e_history4 before update for each row as 
    NEW.sys_period_start = greatest(current_timestamp, date_add(OLD.sys_period_start, interval 1 microsecond));
    insert into migtest_e_history4_history (sys_period_start,sys_period_end,id, test_number) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_number);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history4_history_del for migtest_e_history4 before delete for each row as
    insert into migtest_e_history4_history (sys_period_start,sys_period_end,id, test_number) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_number);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history5_history_upd for migtest_e_history5 before update for each row as 
    NEW.sys_period_start = greatest(current_timestamp, date_add(OLD.sys_period_start, interval 1 microsecond));
    insert into migtest_e_history5_history (sys_period_start,sys_period_end,id, test_number) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_number);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history5_history_del for migtest_e_history5 before delete for each row as
    insert into migtest_e_history5_history (sys_period_start,sys_period_end,id, test_number) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_number);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history6_history_upd for migtest_e_history6 before update for each row as 
    NEW.sys_period_start = greatest(current_timestamp, date_add(OLD.sys_period_start, interval 1 microsecond));
    insert into migtest_e_history6_history (sys_period_start,sys_period_end,id, test_number1, test_number2) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_number1, OLD.test_number2);
end_trigger;
$$

delimiter $$
create or replace trigger migtest_e_history6_history_del for migtest_e_history6 before delete for each row as
    insert into migtest_e_history6_history (sys_period_start,sys_period_end,id, test_number1, test_number2) values (OLD.sys_period_start, NEW.sys_period_start,OLD.id, OLD.test_number1, OLD.test_number2);
end_trigger;
$$

