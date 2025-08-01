-- Migrationscripts for ebean unittest
-- drop dependencies
alter table migtest_ckey_detail drop constraint fk_migtest_ckey_detail_parent;
alter table migtest_fk_cascade drop constraint fk_migtest_fk_cascade_one_id;
alter table migtest_fk_none drop constraint fk_migtest_fk_none_one_id;
alter table migtest_fk_none_via_join drop constraint fk_mgtst_fk_nn_v_jn_n_d;
alter table migtest_fk_set_null drop constraint fk_migtest_fk_set_null_one_id;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_basic drop constraint ck_migtest_e_basic_status';
exception
  when expected_error then null;
end;
$$;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_basic drop constraint ck_migtest_e_basic_status2';
exception
  when expected_error then null;
end;
$$;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_description';
exception
  when expected_error then null;
end;
$$;
alter table migtest_e_basic drop constraint fk_migtest_e_basic_user_id;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_basic drop constraint uq_mgtst__bsc_stts_ndxtst1';
exception
  when expected_error then null;
end;
$$;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_name';
exception
  when expected_error then null;
end;
$$;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest4';
exception
  when expected_error then null;
end;
$$;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest5';
exception
  when expected_error then null;
end;
$$;
delimiter $$
declare
  expected_error exception;
  pragma exception_init(expected_error, -2443);
begin
  execute immediate 'alter table migtest_e_enum drop constraint ck_migtest_e_enum_test_status';
exception
  when expected_error then null;
end;
$$;
alter table drop_main_drop_ref_many drop constraint fk_drp_mn_drp_rf_mny_drp_mn;
alter table drop_main_drop_ref_many drop constraint fk_drp_mn_drp_rf_mny_dr_joeslj;
alter table drop_ref_one drop constraint fk_drop_ref_one_parent_id;
alter table drop_ref_one_to_one drop constraint fk_drp_rf_n_t_n_prnt_d;
alter table migtest_mtm_c_migtest_mtm_m drop constraint fk_mgtst_mtm_c_mgtst_mt_93awga;
alter table migtest_mtm_c_migtest_mtm_m drop constraint fk_mgtst_mtm_c_mgtst_mt_93awgk;
alter table migtest_mtm_m_migtest_mtm_c drop constraint fk_mgtst_mtm_m_mgtst_mt_ggi34k;
alter table migtest_mtm_m_migtest_mtm_c drop constraint fk_mgtst_mtm_m_mgtst_mt_ggi34a;
alter table migtest_mtm_m_phone_numbers drop constraint fk_mgtst_mtm_m_phn_nmbr_s8neid;
drop index ix_migtest_e_basic_indextest3;
drop index ix_migtest_e_basic_indextest6;
drop index ix_migtest_e_basic_indextest7;
drop index ix_table_textfield2;
-- apply changes
create table "migtest_QuOtEd" (
  id                            varchar2(255) not null,
  status1                       varchar2(1),
  status2                       varchar2(1),
  constraint ck_migtest_quoted_status1 check ( status1 in ('N','A','I')),
  constraint ck_migtest_quoted_status2 check ( status2 in ('N','A','I')),
  constraint uq_migtest_quoted_status2 unique (status2),
  constraint pk_migtest_quoted primary key (id)
);

create table migtest_e_ref (
  id                            number(10) not null,
  name                          varchar2(127) not null,
  constraint uq_migtest_e_ref_name unique (name),
  constraint pk_migtest_e_ref primary key (id)
);
create sequence migtest_e_ref_seq;


update migtest_e_basic set status2 = 'N' where status2 is null;

update migtest_e_basic set a_lob = 'X' where a_lob is null;

update migtest_e_basic set user_id = 23 where user_id is null;

-- NOTE: table has @History - special migration may be necessary
update migtest_e_history6 set test_number2 = 7 where test_number2 is null;
-- apply alter tables
alter table migtest_e_basic modify status default null;
alter table migtest_e_basic modify status null;
alter table migtest_e_basic modify status2 varchar2(1);
alter table migtest_e_basic modify status2 default 'N';
alter table migtest_e_basic modify status2 not null;
alter table migtest_e_basic modify a_lob varchar2(255);
alter table migtest_e_basic modify a_lob default 'X';
alter table migtest_e_basic modify a_lob not null;
alter table migtest_e_basic modify default_test null;
alter table migtest_e_basic modify user_id default 23;
alter table migtest_e_basic modify user_id not null;
alter table migtest_e_basic add description_file blob;
alter table migtest_e_basic add old_boolean number(1) default 0 not null;
alter table migtest_e_basic add old_boolean2 number(1);
alter table migtest_e_basic add eref_id number(10);
alter table migtest_e_history2 modify test_string default null;
alter table migtest_e_history2 modify test_string null;
alter table migtest_e_history2 add obsolete_string1 varchar2(255);
alter table migtest_e_history2 add obsolete_string2 varchar2(255);
alter table migtest_e_history4 modify test_number number(10);
alter table migtest_e_history6 modify test_number1 default null;
alter table migtest_e_history6 modify test_number1 null;
alter table migtest_e_history6 modify test_number2 default 7;
alter table migtest_e_history6 modify test_number2 not null;
-- apply post alter
alter table migtest_e_basic add constraint ck_migtest_e_basic_status check ( status in ('N','A','I'));
alter table migtest_e_basic add constraint ck_migtest_e_basic_status2 check ( status2 in ('N','A','I'));
-- NOT YET IMPLEMENTED: alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest2 unique  (indextest2);
-- NOT YET IMPLEMENTED: alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest6 unique  (indextest6);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest7 unique  (indextest7);
alter table migtest_e_enum add constraint ck_migtest_e_enum_test_status check ( test_status in ('N','A','I'));
comment on column migtest_e_history.test_string is '';
comment on table migtest_e_history is '';
comment on column "table"."index" is 'this is a comment';
-- foreign keys and indices
alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade;
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete set null;
create index ix_migtest_e_basic_eref_id on migtest_e_basic (eref_id);
alter table migtest_e_basic add constraint fk_migtest_e_basic_eref_id foreign key (eref_id) references migtest_e_ref (id);

create index ix_migtest_e_basic_indextest1 on migtest_e_basic (indextest1);
create index ix_migtest_e_basic_indextest5 on migtest_e_basic (indextest5);
create index ix_migtest_quoted_status1 on "migtest_QuOtEd" (status1);
