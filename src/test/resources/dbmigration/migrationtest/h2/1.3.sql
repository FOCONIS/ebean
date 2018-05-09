-- Migrationscripts for ebean unittest
-- drop dependencies
drop view if exists migtest_e_history2_with_history;

-- apply changes
create table migtest_e_ref (
  id                            integer auto_increment not null,
  name                          varchar(127) not null,
  constraint uq_migtest_e_ref_name unique (name),
  constraint pk_migtest_e_ref primary key (id)
);

alter table migtest_ckey_detail drop constraint if exists fk_migtest_ckey_detail_parent;
alter table migtest_fk_cascade drop constraint if exists fk_migtest_fk_cascade_one_id;
alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade on update cascade;
alter table migtest_fk_none drop constraint if exists fk_migtest_fk_none_one_id;
alter table migtest_fk_none_via_join drop constraint if exists fk_migtest_fk_none_via_join_one_id;
alter table migtest_fk_set_null drop constraint if exists fk_migtest_fk_set_null_one_id;
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete set null on update set null;
alter table migtest_e_basic drop constraint ck_migtest_e_basic_status;
alter table migtest_e_basic alter column status drop default;
alter table migtest_e_basic alter column status set null;
alter table migtest_e_basic add constraint ck_migtest_e_basic_status check ( status in ('N','A','I'));
alter table migtest_e_basic drop constraint uq_migtest_e_basic_description;

update migtest_e_basic set user_id = 23 where user_id is null;
alter table migtest_e_basic drop constraint if exists fk_migtest_e_basic_user_id;
alter table migtest_e_basic alter column user_id set default 23;
alter table migtest_e_basic alter column user_id set not null;
alter table migtest_e_basic add column old_boolean boolean default false not null;
alter table migtest_e_basic add column old_boolean2 boolean;
alter table migtest_e_basic add column eref_id integer;

alter table migtest_e_basic drop constraint uq_migtest_e_basic_status_indextest1;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_name;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest4;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest5;
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest2 unique  (indextest2);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest6 unique  (indextest6);
comment on column migtest_e_history.test_string is '';
comment on table migtest_e_history is '';
alter table migtest_e_history2 alter column id integer;
alter table migtest_e_history2_history alter column id integer;
alter table migtest_e_history2 alter column test_string drop default;
alter table migtest_e_history2 alter column test_string set null;
alter table migtest_e_history2 add column obsolete_string1 varchar(255);
alter table migtest_e_history2 add column obsolete_string2 varchar(255);
alter table migtest_e_history2_history add column obsolete_string1 varchar(255);
alter table migtest_e_history2_history add column obsolete_string2 varchar(255);

create index ix_migtest_e_basic_indextest1 on migtest_e_basic (indextest1);
create index ix_migtest_e_basic_indextest5 on migtest_e_basic (indextest5);
drop index if exists ix_migtest_e_basic_indextest3;
drop index if exists ix_migtest_e_basic_indextest6;
create index ix_migtest_e_basic_eref_id on migtest_e_basic (eref_id);
alter table migtest_e_basic add constraint fk_migtest_e_basic_eref_id foreign key (eref_id) references migtest_e_ref (id) on delete restrict on update restrict;

-- changes: [alter id, add obsolete_string1, add obsolete_string2]
create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;

drop trigger migtest_e_history2_history_upd;
-- put migtest_e_history2_history migration here;
create trigger migtest_e_history2_history_upd before update,delete on migtest_e_history2 for each row call "io.ebean.config.dbplatform.h2.H2HistoryTrigger";
