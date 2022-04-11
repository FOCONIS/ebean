-- Migrationscripts for ebean unittest
-- apply changes
create table migtest_ckey_assoc (
  id                            integer auto_increment not null,
  assoc_one                     varchar(255),
  constraint pk_migtest_ckey_assoc primary key (id)
);

create table migtest_ckey_detail (
  id                            integer auto_increment not null,
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
  id                            bigint auto_increment not null,
  one_id                        bigint,
  constraint pk_migtest_fk_cascade primary key (id)
);

create table migtest_fk_cascade_one (
  id                            bigint auto_increment not null,
  constraint pk_migtest_fk_cascade_one primary key (id)
);

create table migtest_fk_none (
  id                            bigint auto_increment not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none primary key (id)
);

create table migtest_fk_none_via_join (
  id                            bigint auto_increment not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none_via_join primary key (id)
);

create table migtest_fk_one (
  id                            bigint auto_increment not null,
  constraint pk_migtest_fk_one primary key (id)
);

create table migtest_fk_set_null (
  id                            bigint auto_increment not null,
  one_id                        bigint,
  constraint pk_migtest_fk_set_null primary key (id)
);

create table migtest_e_basic (
  id                            integer auto_increment not null,
  status                        varchar(1) default 'A' not null,
  status2                       varchar(127),
  name                          varchar(127),
  description                   varchar(127),
  some_date                     datetime,
  new_string_field              varchar(255) default 'foo''bar' not null,
  new_boolean_field             tinyint(1) default 1 not null,
  new_boolean_field2            tinyint(1) default 1 not null,
  indextest1                    varchar(127),
  indextest2                    varchar(127),
  indextest3                    varchar(127),
  indextest4                    varchar(127),
  indextest5                    varchar(127),
  indextest6                    varchar(127),
  progress                      integer default 0 not null,
  new_integer                   integer default 42 not null,
  user_id                       integer,
  constraint uq_migtest_e_basic_description unique (description),
  constraint uq_migtest_e_basic_status_indextest1 unique (status,indextest1),
  constraint uq_migtest_e_basic_name unique (name),
  constraint uq_migtest_e_basic_indextest4 unique (indextest4),
  constraint uq_migtest_e_basic_indextest5 unique (indextest5),
  constraint pk_migtest_e_basic primary key (id)
);

create table migtest_e_enum (
  id                            integer auto_increment not null,
  test_status                   varchar(1),
  constraint pk_migtest_e_enum primary key (id)
);

create table migtest_e_history (
  id                            integer auto_increment not null,
  test_string                   bigint comment 'Column altered to long now',
  constraint pk_migtest_e_history primary key (id)
) comment='We have history now';

create table migtest_e_history2 (
  id                            integer auto_increment not null,
  test_string                   varchar(255) default 'unknown' not null,
  test_string2                  varchar(255),
  test_string3                  varchar(255) default 'unknown' not null,
  new_column                    varchar(20),
  constraint pk_migtest_e_history2 primary key (id)
);

create table migtest_e_history3 (
  id                            integer auto_increment not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_history3 primary key (id)
);

create table migtest_e_history4 (
  id                            integer auto_increment not null,
  test_number                   bigint,
  constraint pk_migtest_e_history4 primary key (id)
);

create table migtest_e_history5 (
  id                            integer auto_increment not null,
  test_number                   integer,
  test_boolean                  tinyint(1) default 0 not null,
  constraint pk_migtest_e_history5 primary key (id)
);

create table migtest_e_history6 (
  id                            integer auto_increment not null,
  test_number1                  integer default 42 not null,
  test_number2                  integer,
  constraint pk_migtest_e_history6 primary key (id)
);

create table migtest_e_softdelete (
  id                            integer auto_increment not null,
  test_string                   varchar(255),
  deleted                       tinyint(1) default 0 not null,
  constraint pk_migtest_e_softdelete primary key (id)
);

create table migtest_e_user (
  id                            integer auto_increment not null,
  constraint pk_migtest_e_user primary key (id)
);

create table migtest_mtm_c (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_migtest_mtm_c primary key (id)
);

create table migtest_mtm_c_migtest_mtm_m (
  migtest_mtm_c_id              integer not null,
  migtest_mtm_m_id              bigint not null,
  constraint pk_migtest_mtm_c_migtest_mtm_m primary key (migtest_mtm_c_id,migtest_mtm_m_id)
);

create table migtest_mtm_m (
  id                            bigint auto_increment not null,
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
  id                            integer auto_increment not null,
  name                          varchar(255),
  master_id                     bigint,
  constraint uq_migtest_oto_child_master_id unique (master_id),
  constraint pk_migtest_oto_child primary key (id)
);

create table migtest_oto_master (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  constraint pk_migtest_oto_master primary key (id)
);

-- apply alter tables
alter table migtest_e_history add column sys_period_start datetime(6) default now(6);
alter table migtest_e_history add column sys_period_end datetime(6);
alter table migtest_e_history2 add column sys_period_start datetime(6) default now(6);
alter table migtest_e_history2 add column sys_period_end datetime(6);
alter table migtest_e_history3 add column sys_period_start datetime(6) default now(6);
alter table migtest_e_history3 add column sys_period_end datetime(6);
alter table migtest_e_history4 add column sys_period_start datetime(6) default now(6);
alter table migtest_e_history4 add column sys_period_end datetime(6);
alter table migtest_e_history5 add column sys_period_start datetime(6) default now(6);
alter table migtest_e_history5 add column sys_period_end datetime(6);
alter table migtest_e_history6 add column sys_period_start datetime(6) default now(6);
alter table migtest_e_history6 add column sys_period_end datetime(6);
-- apply post alter
create table migtest_e_history_history(
  id                            integer,
  test_string                   bigint,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history_with_history as select * from migtest_e_history union all select * from migtest_e_history_history;
lock tables migtest_e_history write;
delimiter $$
create trigger migtest_e_history_history_upd before update on migtest_e_history for each row begin
    insert into migtest_e_history_history (sys_period_start,sys_period_end,id, test_string) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history_history_del before delete on migtest_e_history for each row begin
    insert into migtest_e_history_history (sys_period_start,sys_period_end,id, test_string) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string);
end$$
unlock tables;

create table migtest_e_history2_history(
  id                            integer,
  test_string                   varchar(255),
  test_string2                  varchar(255),
  test_string3                  varchar(255),
  new_column                    varchar(20),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;
lock tables migtest_e_history2 write;
delimiter $$
create trigger migtest_e_history2_history_upd before update on migtest_e_history2 for each row begin
    insert into migtest_e_history2_history (sys_period_start,sys_period_end,id, test_string, test_string3, new_column) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string, OLD.test_string3, OLD.new_column);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history2_history_del before delete on migtest_e_history2 for each row begin
    insert into migtest_e_history2_history (sys_period_start,sys_period_end,id, test_string, test_string3, new_column) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string, OLD.test_string3, OLD.new_column);
end$$
unlock tables;

create table migtest_e_history3_history(
  id                            integer,
  test_string                   varchar(255),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history3_with_history as select * from migtest_e_history3 union all select * from migtest_e_history3_history;
lock tables migtest_e_history3 write;
delimiter $$
create trigger migtest_e_history3_history_upd before update on migtest_e_history3 for each row begin
    insert into migtest_e_history3_history (sys_period_start,sys_period_end,id) values (OLD.sys_period_start, now(6),OLD.id);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history3_history_del before delete on migtest_e_history3 for each row begin
    insert into migtest_e_history3_history (sys_period_start,sys_period_end,id) values (OLD.sys_period_start, now(6),OLD.id);
end$$
unlock tables;

create table migtest_e_history4_history(
  id                            integer,
  test_number                   bigint,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history4_with_history as select * from migtest_e_history4 union all select * from migtest_e_history4_history;
lock tables migtest_e_history4 write;
delimiter $$
create trigger migtest_e_history4_history_upd before update on migtest_e_history4 for each row begin
    insert into migtest_e_history4_history (sys_period_start,sys_period_end,id, test_number) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_number);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history4_history_del before delete on migtest_e_history4 for each row begin
    insert into migtest_e_history4_history (sys_period_start,sys_period_end,id, test_number) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_number);
end$$
unlock tables;

create table migtest_e_history5_history(
  id                            integer,
  test_number                   integer,
  test_boolean                  tinyint(1),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history5_with_history as select * from migtest_e_history5 union all select * from migtest_e_history5_history;
lock tables migtest_e_history5 write;
delimiter $$
create trigger migtest_e_history5_history_upd before update on migtest_e_history5 for each row begin
    insert into migtest_e_history5_history (sys_period_start,sys_period_end,id, test_number, test_boolean) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_number, OLD.test_boolean);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history5_history_del before delete on migtest_e_history5 for each row begin
    insert into migtest_e_history5_history (sys_period_start,sys_period_end,id, test_number, test_boolean) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_number, OLD.test_boolean);
end$$
unlock tables;

create table migtest_e_history6_history(
  id                            integer,
  test_number1                  integer,
  test_number2                  integer,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history6_with_history as select * from migtest_e_history6 union all select * from migtest_e_history6_history;
lock tables migtest_e_history6 write;
delimiter $$
create trigger migtest_e_history6_history_upd before update on migtest_e_history6 for each row begin
    insert into migtest_e_history6_history (sys_period_start,sys_period_end,id, test_number1, test_number2) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_number1, OLD.test_number2);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history6_history_del before delete on migtest_e_history6 for each row begin
    insert into migtest_e_history6_history (sys_period_start,sys_period_end,id, test_number1, test_number2) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_number1, OLD.test_number2);
end$$
unlock tables;

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