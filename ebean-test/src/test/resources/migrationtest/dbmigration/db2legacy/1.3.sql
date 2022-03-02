-- Migrationscripts for ebean unittest
-- drop dependencies
alter table migtest_ckey_detail NOT USED fk_mgtst_ck_e1qkb5;
alter table migtest_fk_cascade NOT USED fk_mgtst_fk_65kf6l;
alter table migtest_fk_none NOT USED fk_mgtst_fk_nn_n_d;
alter table migtest_fk_none_via_join NOT USED fk_mgtst_fk_9tknzj;
alter table migtest_fk_set_null NOT USED fk_mgtst_fk_wicx8x;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'CK_MGTST__BSC_STTS' and tabname = 'MIGTEST_E_BASIC') then
  prepare stmt from 'alter table migtest_e_basic drop constraint ck_mgtst__bsc_stts';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'CK_MGTST__B_Z543FG' and tabname = 'MIGTEST_E_BASIC') then
  prepare stmt from 'alter table migtest_e_basic drop constraint ck_mgtst__b_z543fg';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'UQ_MGTST__B_VS45XO' and tabname = 'MIGTEST_E_BASIC') then
  prepare stmt from 'alter table migtest_e_basic drop constraint uq_mgtst__b_vs45xo';
  execute stmt;
end if;
end$$
delimiter $$
begin
if exists (select indname from syscat.indexes where indschema = current_schema and indname = 'UQ_MGTST__B_VS45XO') then
  prepare stmt from 'drop index uq_mgtst__b_vs45xo';
  execute stmt;
end if;
end$$;
alter table migtest_e_basic NOT USED fk_mgtst__bsc_sr_d;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'UQ_MGTST__B_UCFCNE' and tabname = 'MIGTEST_E_BASIC') then
  prepare stmt from 'alter table migtest_e_basic drop constraint uq_mgtst__b_ucfcne';
  execute stmt;
end if;
end$$
delimiter $$
begin
if exists (select indname from syscat.indexes where indschema = current_schema and indname = 'UQ_MGTST__B_UCFCNE') then
  prepare stmt from 'drop index uq_mgtst__b_ucfcne';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'UQ_MGTST__BSC_NM' and tabname = 'MIGTEST_E_BASIC') then
  prepare stmt from 'alter table migtest_e_basic drop constraint uq_mgtst__bsc_nm';
  execute stmt;
end if;
end$$
delimiter $$
begin
if exists (select indname from syscat.indexes where indschema = current_schema and indname = 'UQ_MGTST__BSC_NM') then
  prepare stmt from 'drop index uq_mgtst__bsc_nm';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'UQ_MGTST__B_4AYC00' and tabname = 'MIGTEST_E_BASIC') then
  prepare stmt from 'alter table migtest_e_basic drop constraint uq_mgtst__b_4ayc00';
  execute stmt;
end if;
end$$
delimiter $$
begin
if exists (select indname from syscat.indexes where indschema = current_schema and indname = 'UQ_MGTST__B_4AYC00') then
  prepare stmt from 'drop index uq_mgtst__b_4ayc00';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'UQ_MGTST__B_4AYC01' and tabname = 'MIGTEST_E_BASIC') then
  prepare stmt from 'alter table migtest_e_basic drop constraint uq_mgtst__b_4ayc01';
  execute stmt;
end if;
end$$
delimiter $$
begin
if exists (select indname from syscat.indexes where indschema = current_schema and indname = 'UQ_MGTST__B_4AYC01') then
  prepare stmt from 'drop index uq_mgtst__b_4ayc01';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = 'CK_MGTST__N_773SOK' and tabname = 'MIGTEST_E_ENUM') then
  prepare stmt from 'alter table migtest_e_enum drop constraint ck_mgtst__n_773sok';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select indname from syscat.indexes where indschema = current_schema and indname = 'IX_MGTST__B_EU8CSS') then
  prepare stmt from 'drop index ix_mgtst__b_eu8css';
  execute stmt;
end if;
end$$;
delimiter $$
begin
if exists (select indname from syscat.indexes where indschema = current_schema and indname = 'IX_MGTST__B_EU8CSV') then
  prepare stmt from 'drop index ix_mgtst__b_eu8csv';
  execute stmt;
end if;
end$$;
-- apply changes
create table migtest_e_ref (
  id                            integer generated by default as identity not null,
  name                          varchar(127) not null,
  constraint pk_migtest_e_ref primary key (id)
);


update migtest_e_basic set status2 = 'N' where status2 is null;

update migtest_e_basic set user_id = 23 where user_id is null;



-- NOTE: table has @History - special migration may be necessary
update migtest_e_history6 set test_number2 = 7 where test_number2 is null;
-- apply alter tables
alter table migtest_e_basic alter column status drop default;
alter table migtest_e_basic alter column status drop not null;
alter table migtest_e_basic alter column status2 set data type varchar(1);
alter table migtest_e_basic alter column status2 set default 'N';
alter table migtest_e_basic alter column status2 set not null;
alter table migtest_e_basic alter column user_id set default 23;
alter table migtest_e_basic alter column user_id set not null;
alter table migtest_e_basic add column description_file blob(64M);
alter table migtest_e_basic add column old_boolean boolean default false not null;
alter table migtest_e_basic add column old_boolean2 boolean;
alter table migtest_e_basic add column eref_id integer;
alter table migtest_e_history2 alter column test_string drop default;
alter table migtest_e_history2 alter column test_string drop not null;
alter table migtest_e_history2 add column obsolete_string1 varchar(255);
alter table migtest_e_history2 add column obsolete_string2 varchar(255);
alter table migtest_e_history4 alter column test_number set data type integer;
alter table migtest_e_history6 alter column test_number1 drop default;
alter table migtest_e_history6 alter column test_number1 drop not null;
alter table migtest_e_history6 alter column test_number2 set default 7;
alter table migtest_e_history6 alter column test_number2 set not null;
-- apply post alter
alter table migtest_e_ref add constraint uq_mgtst__rf_nm unique  (name);
alter table migtest_e_basic add constraint ck_mgtst__bsc_stts check ( status in ('N','A','I'));
alter table migtest_e_basic add constraint ck_mgtst__b_z543fg check ( status2 in ('N','A','I'));
create unique index uq_mgtst__b_4aybzy on migtest_e_basic(indextest2) exclude null keys;
create unique index uq_mgtst__b_4ayc02 on migtest_e_basic(indextest6) exclude null keys;
alter table migtest_e_enum add constraint ck_mgtst__n_773sok check ( test_status in ('N','A','I'));
comment on column migtest_e_history.test_string is '';
comment on table migtest_e_history is '';
create index ix_mgtst__b_eu8csq on migtest_e_basic (indextest1);
create index ix_mgtst__b_eu8csu on migtest_e_basic (indextest5);
-- foreign keys and indices
alter table migtest_fk_cascade add constraint fk_mgtst_fk_65kf6l foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade on update restrict;
alter table migtest_fk_set_null add constraint fk_mgtst_fk_wicx8x foreign key (one_id) references migtest_fk_one (id) on delete set null on update restrict;
create index ix_mgtst__bsc_rf_d on migtest_e_basic (eref_id);
alter table migtest_e_basic add constraint fk_mgtst__bsc_rf_d foreign key (eref_id) references migtest_e_ref (id) on delete restrict on update restrict;

