-- Migrationscripts for ebean unittest
-- drop dependencies
alter table if exists migtest_ckey_detail drop constraint if exists fk_migtest_ckey_detail_parent;
alter table if exists migtest_fk_cascade drop constraint if exists fk_migtest_fk_cascade_one_id;
alter table if exists migtest_fk_none drop constraint if exists fk_migtest_fk_none_one_id;
alter table if exists migtest_fk_none_via_join drop constraint if exists fk_migtest_fk_none_via_join_one_id;
alter table if exists migtest_fk_set_null drop constraint if exists fk_migtest_fk_set_null_one_id;
alter table migtest_e_basic drop constraint if exists ck_migtest_e_basic_status;
alter table migtest_e_basic drop constraint if exists ck_migtest_e_basic_status2;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_description;
alter table if exists migtest_e_basic drop constraint if exists fk_migtest_e_basic_user_id;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_status_indextest1;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_name;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest4;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest5;
alter table migtest_e_enum drop constraint if exists ck_migtest_e_enum_test_status;
alter table if exists drop_main_drop_ref_many drop constraint if exists fk_drop_main_drop_ref_many_drop_main;
alter table if exists drop_main_drop_ref_many drop constraint if exists fk_drop_main_drop_ref_many_drop_ref_many;
alter table if exists drop_ref_one drop constraint if exists fk_drop_ref_one_parent_id;
alter table if exists drop_ref_one_to_one drop constraint if exists fk_drop_ref_one_to_one_parent_id;
alter table if exists migtest_mtm_c_migtest_mtm_m drop constraint if exists fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_c;
alter table if exists migtest_mtm_c_migtest_mtm_m drop constraint if exists fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_m;
alter table if exists migtest_mtm_m_migtest_mtm_c drop constraint if exists fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_m;
alter table if exists migtest_mtm_m_migtest_mtm_c drop constraint if exists fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_c;
alter table if exists migtest_mtm_m_phone_numbers drop constraint if exists fk_migtest_mtm_m_phone_numbers_migtest_mtm_m_id;
drop index if exists ix_migtest_e_basic_indextest3;
drop index if exists ix_migtest_e_basic_indextest6;
drop index if exists ix_migtest_e_basic_indextest7;
drop index if exists ix_table_textfield2;
-- apply changes
create table "migtest_QuOtEd" (
  id                            varchar(255) not null,
  status1                       varchar(1),
  status2                       varchar(1),
  constraint ck_migtest_quoted_status1 check ( status1 in ('N','A','I')),
  constraint ck_migtest_quoted_status2 check ( status2 in ('N','A','I')),
  constraint uq_migtest_quoted_status2 unique (status2),
  constraint pk_migtest_quoted primary key (id)
);

create table migtest_e_ref (
  id                            serial not null,
  name                          varchar(127) not null,
  constraint uq_migtest_e_ref_name unique (name),
  constraint pk_migtest_e_ref primary key (id)
);


update migtest_e_basic set status2 = 'N' where status2 is null;

update migtest_e_basic set a_lob = 'X' where a_lob is null;

update migtest_e_basic set user_id = 23 where user_id is null;
drop trigger if exists migtest_e_history2_history_upd on migtest_e_history2 cascade;
drop function if exists migtest_e_history2_history_version();

drop view migtest_e_history2_with_history;
drop trigger if exists migtest_e_history3_history_upd on migtest_e_history3 cascade;
drop function if exists migtest_e_history3_history_version();

drop view migtest_e_history3_with_history;
drop trigger if exists migtest_e_history4_history_upd on migtest_e_history4 cascade;
drop function if exists migtest_e_history4_history_version();

drop view migtest_e_history4_with_history;
drop trigger if exists migtest_e_history6_history_upd on migtest_e_history6 cascade;
drop function if exists migtest_e_history6_history_version();

drop view migtest_e_history6_with_history;

-- NOTE: table has @History - special migration may be necessary
update migtest_e_history6 set test_number2 = 7 where test_number2 is null;
-- apply alter tables
alter table migtest_e_basic alter column status drop default;
alter table migtest_e_basic alter column status drop not null;
alter table migtest_e_basic alter column status2 type varchar(1);
alter table migtest_e_basic alter column status2 set default 'N';
alter table migtest_e_basic alter column status2 set not null;
alter table migtest_e_basic alter column a_lob type varchar(255) using a_lob::varchar(255);
alter table migtest_e_basic alter column a_lob set default 'X';
alter table migtest_e_basic alter column a_lob set not null;
alter table migtest_e_basic alter column default_test drop not null;
alter table migtest_e_basic alter column user_id set default 23;
alter table migtest_e_basic alter column user_id set not null;
alter table migtest_e_basic add column if not exists description_file bytea;
alter table migtest_e_basic add column if not exists old_boolean boolean default false not null;
alter table migtest_e_basic add column if not exists old_boolean2 boolean;
alter table migtest_e_basic add column if not exists eref_id integer;
alter table migtest_e_history2 alter column test_string drop default;
alter table migtest_e_history2 alter column test_string drop not null;
alter table migtest_e_history2 add column if not exists obsolete_string1 varchar(255);
alter table migtest_e_history2 add column if not exists obsolete_string2 varchar(255);
alter table migtest_e_history2_history alter column test_string drop default;
alter table migtest_e_history2_history alter column test_string drop not null;
alter table migtest_e_history2_history add column if not exists obsolete_string1 varchar(255);
alter table migtest_e_history2_history add column if not exists obsolete_string2 varchar(255);
alter table migtest_e_history4 alter column test_number type integer using test_number::integer;
alter table migtest_e_history4_history alter column test_number type integer using test_number::integer;
alter table migtest_e_history6 alter column test_number1 drop default;
alter table migtest_e_history6 alter column test_number1 drop not null;
alter table migtest_e_history6 alter column test_number2 set default 7;
alter table migtest_e_history6 alter column test_number2 set not null;
alter table migtest_e_history6_history alter column test_number1 drop default;
alter table migtest_e_history6_history alter column test_number1 drop not null;
-- apply post alter
alter table migtest_e_basic add constraint ck_migtest_e_basic_status check ( status in ('N','A','I'));
alter table migtest_e_basic add constraint ck_migtest_e_basic_status2 check ( status2 in ('N','A','I'));
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest2 unique  (indextest2);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest6 unique  (indextest6);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest7 unique  (indextest7);
alter table migtest_e_enum add constraint ck_migtest_e_enum_test_status check ( test_status in ('N','A','I'));
comment on column migtest_e_history.test_string is '';
comment on table migtest_e_history is '';
create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;
create or replace function migtest_e_history2_history_version() returns trigger as $$
-- play-ebean-start
declare
  lowerTs timestamptz;
  upperTs timestamptz;
begin
  lowerTs = lower(OLD.sys_period);
  upperTs = greatest(lowerTs + '1 microsecond',current_timestamp);
  if (TG_OP = 'UPDATE') then
    insert into migtest_e_history2_history (sys_period,id, test_string, obsolete_string2, test_string2, test_string3, new_column) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string, OLD.obsolete_string2, OLD.test_string2, OLD.test_string3, OLD.new_column);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history2_history (sys_period,id, test_string, obsolete_string2, test_string2, test_string3, new_column) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string, OLD.obsolete_string2, OLD.test_string2, OLD.test_string3, OLD.new_column);
    return old;
  end if;
end;
-- play-ebean-end
$$ LANGUAGE plpgsql;

create trigger migtest_e_history2_history_upd
  before update or delete on migtest_e_history2
  for each row execute procedure migtest_e_history2_history_version();

create view migtest_e_history3_with_history as select * from migtest_e_history3 union all select * from migtest_e_history3_history;
create or replace function migtest_e_history3_history_version() returns trigger as $$
-- play-ebean-start
declare
  lowerTs timestamptz;
  upperTs timestamptz;
begin
  lowerTs = lower(OLD.sys_period);
  upperTs = greatest(lowerTs + '1 microsecond',current_timestamp);
  if (TG_OP = 'UPDATE') then
    insert into migtest_e_history3_history (sys_period,id, test_string) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history3_history (sys_period,id, test_string) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string);
    return old;
  end if;
end;
-- play-ebean-end
$$ LANGUAGE plpgsql;

create trigger migtest_e_history3_history_upd
  before update or delete on migtest_e_history3
  for each row execute procedure migtest_e_history3_history_version();

create view migtest_e_history4_with_history as select * from migtest_e_history4 union all select * from migtest_e_history4_history;
create or replace function migtest_e_history4_history_version() returns trigger as $$
-- play-ebean-start
declare
  lowerTs timestamptz;
  upperTs timestamptz;
begin
  lowerTs = lower(OLD.sys_period);
  upperTs = greatest(lowerTs + '1 microsecond',current_timestamp);
  if (TG_OP = 'UPDATE') then
    insert into migtest_e_history4_history (sys_period,id, test_number) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_number);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history4_history (sys_period,id, test_number) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_number);
    return old;
  end if;
end;
-- play-ebean-end
$$ LANGUAGE plpgsql;

create trigger migtest_e_history4_history_upd
  before update or delete on migtest_e_history4
  for each row execute procedure migtest_e_history4_history_version();

create view migtest_e_history6_with_history as select * from migtest_e_history6 union all select * from migtest_e_history6_history;
create or replace function migtest_e_history6_history_version() returns trigger as $$
-- play-ebean-start
declare
  lowerTs timestamptz;
  upperTs timestamptz;
begin
  lowerTs = lower(OLD.sys_period);
  upperTs = greatest(lowerTs + '1 microsecond',current_timestamp);
  if (TG_OP = 'UPDATE') then
    insert into migtest_e_history6_history (sys_period,id, test_number1, test_number2) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_number1, OLD.test_number2);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history6_history (sys_period,id, test_number1, test_number2) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_number1, OLD.test_number2);
    return old;
  end if;
end;
-- play-ebean-end
$$ LANGUAGE plpgsql;

create trigger migtest_e_history6_history_upd
  before update or delete on migtest_e_history6
  for each row execute procedure migtest_e_history6_history_version();

comment on column "table"."index" is 'this is a comment';
-- foreign keys and indices
alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade on update restrict;
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete set null on update restrict;
create index ix_migtest_e_basic_eref_id on migtest_e_basic (eref_id);
alter table migtest_e_basic add constraint fk_migtest_e_basic_eref_id foreign key (eref_id) references migtest_e_ref (id) on delete restrict on update restrict;

create index if not exists ix_migtest_e_basic_indextest1 on migtest_e_basic (indextest1);
create index if not exists ix_migtest_e_basic_indextest5 on migtest_e_basic (indextest5);
create index if not exists ix_migtest_quoted_status1 on "migtest_QuOtEd" (status1);
create index if not exists ix_m12_otoc71 on migtest_oto_child (name);
create unique index if not exists uq_m12_otoc71 on migtest_oto_child (lower(name));
create unique index if not exists ix_migtest_oto_master_lowername on migtest_oto_master (lower(name));
