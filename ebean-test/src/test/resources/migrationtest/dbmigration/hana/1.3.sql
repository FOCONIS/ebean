-- Migrationscripts for ebean unittest
-- drop dependencies
alter table migtest_ckey_detail drop constraint  fk_migtest_ckey_detail_parent;
alter table migtest_fk_cascade drop constraint  fk_migtest_fk_cascade_one_id;
alter table migtest_fk_none drop constraint  fk_migtest_fk_none_one_id;
alter table migtest_fk_none_via_join drop constraint  fk_migtest_fk_none_via_join_one_id;
alter table migtest_fk_set_null drop constraint  fk_migtest_fk_set_null_one_id;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint ck_migtest_e_basic_status';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint ck_migtest_e_basic_status2';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_description';
end;
$$;
alter table migtest_e_basic drop constraint  fk_migtest_e_basic_user_id;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_status_indextest1';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_name';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest4';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest5';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_enum drop constraint ck_migtest_e_enum_test_status';
end;
$$;
alter table migtest_mtm_c_migtest_mtm_m drop constraint  fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_c;
alter table migtest_mtm_c_migtest_mtm_m drop constraint  fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_m;
alter table migtest_mtm_m_migtest_mtm_c drop constraint  fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_m;
alter table migtest_mtm_m_migtest_mtm_c drop constraint  fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_c;
alter table migtest_mtm_m_phone_numbers drop constraint  fk_migtest_mtm_m_phone_numbers_migtest_mtm_m_id;
delimiter $$
do
begin
declare exit handler for sql_error_code 261 begin end;
exec 'drop index ix_migtest_e_basic_indextest3';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 261 begin end;
exec 'drop index ix_migtest_e_basic_indextest6';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 261 begin end;
exec 'drop index ix_table_textfield2';
end;
$$;
-- apply changes
create column table migtest_drop_main (
  id                            integer generated by default as identity not null,
  constraint pk_migtest_drop_main primary key (id)
);

create column table migtest_drop_main_migtest_drop_ref_many (
  migtest_drop_main_id          integer not null,
  migtest_drop_ref_many_id      integer not null,
  constraint pk_migtest_drop_main_migtest_drop_ref_many primary key (migtest_drop_main_id,migtest_drop_ref_many_id)
);

create column table migtest_drop_ref_many (
  id                            integer generated by default as identity not null,
  constraint pk_migtest_drop_ref_many primary key (id)
);

create column table migtest_drop_ref_one (
  id                            integer generated by default as identity not null,
  parent_id                     integer,
  constraint pk_migtest_drop_ref_one primary key (id)
);

create column table migtest_drop_ref_one_to_one (
  id                            integer generated by default as identity not null,
  parent_id                     integer,
  constraint uq_migtest_drop_ref_one_to_one_parent_id unique (parent_id),
  constraint pk_migtest_drop_ref_one_to_one primary key (id)
);

create column table "migtest_QuOtEd" (
  id                            nvarchar(255) not null,
  status1                       nvarchar(1),
  status2                       nvarchar(1),
  constraint ck_migtest_quoted_status1 check ( status1 in ('N','A','I')),
  constraint ck_migtest_quoted_status2 check ( status2 in ('N','A','I')),
  constraint uq_migtest_quoted_status2 unique (status2),
  constraint pk_migtest_quoted primary key (id)
);

create column table migtest_e_ref (
  id                            integer generated by default as identity not null,
  name                          nvarchar(127) not null,
  constraint uq_migtest_e_ref_name unique (name),
  constraint pk_migtest_e_ref primary key (id)
);


update migtest_e_basic set status2 = 'N' where status2 is null;

update migtest_e_basic set a_lob = 'X' where a_lob is null;

update migtest_e_basic set user_id = 23 where user_id is null;
alter table migtest_e_history2 drop system versioning;
alter table migtest_e_history3 drop system versioning;
alter table migtest_e_history4 drop system versioning;
alter table migtest_e_history6 drop system versioning;

-- NOTE: table has @History - special migration may be necessary
update migtest_e_history6 set test_number2 = 7 where test_number2 is null;
-- apply alter tables
alter table migtest_e_basic alter (status nvarchar(1) default null,
   status2 nclob not null);
alter table migtest_e_basic alter (status2 nvarchar(1) default 'N' not null,
   a_lob nvarchar(255) default 'X' not null,
   user_id integer default 23 not null);
alter table migtest_e_basic add (description_file blob,
   old_boolean boolean default false not null,
   old_boolean2 boolean,
   eref_id integer);
alter table migtest_e_history2 alter (test_string nvarchar(255) default null);
alter table migtest_e_history2 add (obsolete_string1 nvarchar(255),
   obsolete_string2 nvarchar(255));
alter table migtest_e_history2_history alter (test_string nvarchar(255));
alter table migtest_e_history2_history add (obsolete_string1 nvarchar(255),
   obsolete_string2 nvarchar(255));
alter table migtest_e_history4 alter (test_number decimal);
alter table migtest_e_history4 alter (test_number integer);
alter table migtest_e_history4_history alter (test_number decimal);
alter table migtest_e_history4_history alter (test_number integer);
alter table migtest_e_history6 alter (test_number1 integer default null,
   test_number2 integer default 7 not null);
alter table migtest_e_history6_history alter (test_number1 integer);
-- apply post alter
alter table migtest_e_basic add constraint ck_migtest_e_basic_status check ( status in ('N','A','I'));
alter table migtest_e_basic add constraint ck_migtest_e_basic_status2 check ( status2 in ('N','A','I'));
-- cannot create unique index "uq_migtest_e_basic_indextest2" on table "migtest_e_basic" with nullable columns;
-- cannot create unique index "uq_migtest_e_basic_indextest6" on table "migtest_e_basic" with nullable columns;
alter table migtest_e_enum add constraint ck_migtest_e_enum_test_status check ( test_status in ('N','A','I'));
comment on column migtest_e_history.test_string is '';
comment on table migtest_e_history is '';
alter table migtest_e_history2 add system versioning history table migtest_e_history2_history not validated;
alter table migtest_e_history3 add system versioning history table migtest_e_history3_history not validated;
alter table migtest_e_history4 add system versioning history table migtest_e_history4_history not validated;
alter table migtest_e_history6 add system versioning history table migtest_e_history6_history not validated;
comment on column "table"."index" is 'this is a comment';
-- foreign keys and indices
-- explicit index "ix_migtest_drop_main_migtest_drop_ref_many_migtest_drop_m_1" for single column "migtest_drop_main_id" of table "migtest_drop_main_migtest_drop_ref_many" is not necessary;
alter table migtest_drop_main_migtest_drop_ref_many add constraint fk_migtest_drop_main_migtest_drop_ref_many_migtest_drop_m_1 foreign key (migtest_drop_main_id) references migtest_drop_main (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_drop_main_migtest_drop_ref_many_migtest_drop_r_2" for single column "migtest_drop_ref_many_id" of table "migtest_drop_main_migtest_drop_ref_many" is not necessary;
alter table migtest_drop_main_migtest_drop_ref_many add constraint fk_migtest_drop_main_migtest_drop_ref_many_migtest_drop_r_2 foreign key (migtest_drop_ref_many_id) references migtest_drop_ref_many (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_drop_ref_one_parent_id" for single column "parent_id" of table "migtest_drop_ref_one" is not necessary;
alter table migtest_drop_ref_one add constraint fk_migtest_drop_ref_one_parent_id foreign key (parent_id) references migtest_drop_main (id) on delete restrict on update restrict;

alter table migtest_drop_ref_one_to_one add constraint fk_migtest_drop_ref_one_to_one_parent_id foreign key (parent_id) references migtest_drop_main (id) on delete restrict on update restrict;

alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade on update restrict;
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete set null on update restrict;
-- explicit index "ix_migtest_e_basic_eref_id" for single column "eref_id" of table "migtest_e_basic" is not necessary;
alter table migtest_e_basic add constraint fk_migtest_e_basic_eref_id foreign key (eref_id) references migtest_e_ref (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_e_basic_indextest1" for single column "indextest1" of table "migtest_e_basic" is not necessary;
-- explicit index "ix_migtest_e_basic_indextest5" for single column "indextest5" of table "migtest_e_basic" is not necessary;
-- explicit index "ix_migtest_quoted_status1" for single column "status1" of table ""migtest_QuOtEd"" is not necessary;
