-- Migrationscripts for ebean unittest
-- apply changes
create table migtest_e_ref (
  id                            integer generated by default as identity not null,
  name                          varchar(127) not null,
  constraint pk_migtest_e_ref primary key (id)
);
alter table migtest_e_ref add constraint uq_mgtst__rf_nm unique  (name);

alter table migtest_ckey_detail drop constraint fk_mgtst_ck_e1qkb5;
alter table migtest_fk_cascade drop constraint fk_mgtst_fk_65kf6l;
alter table migtest_fk_cascade add constraint fk_mgtst_fk_65kf6l foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade;
alter table migtest_fk_none drop constraint fk_mgtst_fk_nn_n_d;
alter table migtest_fk_none_via_join drop constraint fk_mgtst_fk_9tknzj;
alter table migtest_fk_set_null drop constraint fk_mgtst_fk_wicx8x;
alter table migtest_fk_set_null add constraint fk_mgtst_fk_wicx8x foreign key (one_id) references migtest_fk_one (id) on delete set null;
alter table migtest_e_basic drop constraint ck_mgtst__bsc_stts;
alter table migtest_e_basic alter column status drop default;
alter table migtest_e_basic alter column status set null;
alter table migtest_e_basic add constraint ck_mgtst__bsc_stts check ( status in ('N','A','I'));
alter table migtest_e_basic drop constraint uq_mgtst__b_vs45xo;
alter table migtest_e_basic alter column some_date drop default;
alter table migtest_e_basic alter column some_date set null;

update migtest_e_basic set user_id = 23 where user_id is null;
alter table migtest_e_basic drop constraint fk_mgtst__bsc_sr_d;
alter table migtest_e_basic alter column user_id set default 23;
alter table migtest_e_basic alter column user_id set not null;
alter table migtest_e_basic add column old_boolean boolean default false not null;
alter table migtest_e_basic add column old_boolean2 boolean;
alter table migtest_e_basic add column eref_id integer;

alter table migtest_e_basic drop constraint uq_mgtst__b_ucfcne;
alter table migtest_e_basic drop constraint uq_mgtst__bsc_nm;
alter table migtest_e_basic drop constraint uq_mgtst__b_4ayc00;
alter table migtest_e_basic drop constraint uq_mgtst__b_4ayc01;
-- NOT SUPPORTED alter table migtest_e_basic add constraint uq_mgtst__b_4aybzy unique  (indextest2);
-- NOT SUPPORTED alter table migtest_e_basic add constraint uq_mgtst__b_4ayc02 unique  (indextest6);
comment on column migtest_e_history.test_string is '';
comment on table migtest_e_history is '';
alter table migtest_e_history2 alter column test_string drop default;
alter table migtest_e_history2 alter column test_string set null;
create index ix_mgtst__b_eu8csq on migtest_e_basic (indextest1);
create index ix_mgtst__b_eu8csu on migtest_e_basic (indextest5);
drop index ix_mgtst__b_eu8css;
drop index ix_mgtst__b_eu8csv;
alter table migtest_e_basic add constraint fk_mgtst__bsc_rf_d foreign key (eref_id) references migtest_e_ref (id) on delete restrict;
create index ix_mgtst__bsc_rf_d on migtest_e_basic (eref_id);

