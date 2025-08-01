-- Migrationscripts for ebean unittest
-- drop dependencies
alter table if exists migtest_fk_cascade drop constraint if exists fk_migtest_fk_cascade_one_id;
alter table if exists migtest_fk_set_null drop constraint if exists fk_migtest_fk_set_null_one_id;
alter table migtest_e_basic drop constraint if exists ck_migtest_e_basic_status;
alter table migtest_e_basic drop constraint if exists ck_migtest_e_basic_status2;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest2;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest6;
alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest7;
alter table migtest_e_enum drop constraint if exists ck_migtest_e_enum_test_status;
drop index if exists ix_migtest_e_basic_indextest1;
drop index if exists ix_migtest_e_basic_indextest5;
drop index if exists ix_migtest_quoted_status1;
drop index if exists idxd_migtest_0;
drop index concurrently if exists ix_migtest_oto_child_lowername_id;
drop index if exists ix_migtest_oto_child_lowername;
-- apply changes
create table drop_main (
  id                            serial not null,
  constraint pk_drop_main primary key (id)
);

create table drop_main_drop_ref_many (
  drop_main_id                  integer not null,
  drop_ref_many_id              integer not null,
  constraint pk_drop_main_drop_ref_many primary key (drop_main_id,drop_ref_many_id)
);

create table drop_ref_many (
  id                            serial not null,
  constraint pk_drop_ref_many primary key (id)
);

create table drop_ref_one (
  id                            serial not null,
  parent_id                     integer,
  constraint pk_drop_ref_one primary key (id)
);

create table drop_ref_one_to_one (
  id                            serial not null,
  parent_id                     integer,
  constraint uq_drop_ref_one_to_one_parent_id unique (parent_id),
  constraint pk_drop_ref_one_to_one primary key (id)
);

create table migtest_e_test_binary (
  id                            serial not null,
  test_byte16                   bytea,
  test_byte256                  bytea,
  test_byte512                  bytea,
  test_byte1k                   bytea,
  test_byte2k                   bytea,
  test_byte4k                   bytea,
  test_byte8k                   bytea,
  test_byte16k                  bytea,
  test_byte32k                  bytea,
  test_byte64k                  bytea,
  test_byte128k                 bytea,
  test_byte256k                 bytea,
  test_byte512k                 bytea,
  test_byte1m                   bytea,
  test_byte2m                   bytea,
  test_byte4m                   bytea,
  test_byte8m                   bytea,
  test_byte16m                  bytea,
  test_byte32m                  bytea,
  constraint pk_migtest_e_test_binary primary key (id)
);

create table migtest_e_test_json (
  id                            serial not null,
  json255                       json,
  json256                       json,
  json512                       json,
  json1k                        json,
  json2k                        json,
  json4k                        json,
  json8k                        json,
  json16k                       json,
  json32k                       json,
  json64k                       json,
  json128k                      json,
  json256k                      json,
  json512k                      json,
  json1m                        json,
  json2m                        json,
  json4m                        json,
  json8m                        json,
  json16m                       json,
  json32m                       json,
  constraint pk_migtest_e_test_json primary key (id)
);

create table migtest_e_test_lob (
  id                            serial not null,
  lob255                        text,
  lob256                        text,
  lob512                        text,
  lob1k                         text,
  lob2k                         text,
  lob4k                         text,
  lob8k                         text,
  lob16k                        text,
  lob32k                        text,
  lob64k                        text,
  lob128k                       text,
  lob256k                       text,
  lob512k                       text,
  lob1m                         text,
  lob2m                         text,
  lob4m                         text,
  lob8m                         text,
  lob16m                        text,
  lob32m                        text,
  constraint pk_migtest_e_test_lob primary key (id)
);

create table migtest_e_test_varchar (
  id                            serial not null,
  varchar8k                     varchar(8192),
  varchar255                    varchar(255),
  varchar256                    varchar(256),
  varchar512                    varchar(512),
  varchar1k                     varchar(1024),
  varchar2k                     varchar(2048),
  varchar4k                     varchar(4096),
  varchar16k                    varchar(16384),
  varchar32k                    varchar(32768),
  varchar64k                    varchar(65536),
  varchar128k                   varchar(131072),
  varchar256k                   varchar(262144),
  varchar512k                   varchar(524288),
  varchar1m                     varchar(1048576),
  varchar2m                     varchar(2097152),
  varchar4m                     varchar(4194304),
  varchar8m                     varchar(8388608),
  varchar16m                    text,
  varchar32m                    text,
  constraint pk_migtest_e_test_varchar primary key (id)
);

create table migtest_e_user (
  id                            serial not null,
  constraint pk_migtest_e_user primary key (id)
);

create table migtest_mtm_c_migtest_mtm_m (
  migtest_mtm_c_id              integer not null,
  migtest_mtm_m_id              bigint not null,
  constraint pk_migtest_mtm_c_migtest_mtm_m primary key (migtest_mtm_c_id,migtest_mtm_m_id)
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


update migtest_e_basic set status = 'A' where status is null;

-- rename all collisions;

update migtest_e_basic set default_test = 0 where default_test is null;

insert into migtest_e_user (id) select distinct user_id from migtest_e_basic;

alter table migtest_e_history alter column test_string TYPE bigint USING (test_string::integer);

-- NOTE: table has @History - special migration may be necessary
update migtest_e_history2 set test_string = 'unknown' where test_string is null;
drop trigger if exists migtest_e_history2_history_upd on migtest_e_history2 cascade;
drop function if exists migtest_e_history2_history_version();

drop view migtest_e_history2_with_history;
drop trigger if exists migtest_e_history3_history_upd on migtest_e_history3 cascade;
drop function if exists migtest_e_history3_history_version();

drop view migtest_e_history3_with_history;
drop trigger if exists migtest_e_history4_history_upd on migtest_e_history4 cascade;
drop function if exists migtest_e_history4_history_version();

drop view migtest_e_history4_with_history;
drop trigger if exists migtest_e_history5_history_upd on migtest_e_history5 cascade;
drop function if exists migtest_e_history5_history_version();

drop view migtest_e_history5_with_history;

-- NOTE: table has @History - special migration may be necessary
update migtest_e_history6 set test_number1 = 42 where test_number1 is null;
drop trigger if exists migtest_e_history6_history_upd on migtest_e_history6 cascade;
drop function if exists migtest_e_history6_history_version();

drop view migtest_e_history6_with_history;
drop trigger if exists table_history_upd on "table" cascade;
drop function if exists table_history_version();

drop view table_with_history;
-- apply alter tables
alter table "table" alter column textfield drop not null;
alter table "table" add column if not exists "select" varchar(255);
alter table "table" add column if not exists textfield2 varchar(255);
alter table migtest_ckey_detail add column if not exists one_key integer;
alter table migtest_ckey_detail add column if not exists two_key varchar(127);
alter table migtest_ckey_parent add column if not exists assoc_id integer;
alter table migtest_e_basic alter column status set default 'A';
alter table migtest_e_basic alter column status set not null;
alter table migtest_e_basic alter column status2 type varchar(127);
alter table migtest_e_basic alter column status2 drop default;
alter table migtest_e_basic alter column status2 drop not null;
alter table migtest_e_basic alter column a_lob drop default;
alter table migtest_e_basic alter column a_lob drop not null;
alter table migtest_e_basic alter column default_test set not null;
alter table migtest_e_basic alter column user_id drop not null;
alter table migtest_e_basic add column if not exists new_string_field varchar(255) default 'foo''bar' not null;
alter table migtest_e_basic add column if not exists new_boolean_field boolean default true not null;
alter table migtest_e_basic add column if not exists new_boolean_field2 boolean default true not null;
alter table migtest_e_basic add column if not exists progress integer default 0 not null;
alter table migtest_e_basic add column if not exists new_integer integer default 42 not null;
alter table migtest_e_history add column if not exists sys_period tstzrange not null default tstzrange(current_timestamp, null);
alter table migtest_e_history alter column test_string type bigint using test_string::bigint;
alter table migtest_e_history2 alter column test_string set default 'unknown';
alter table migtest_e_history2 alter column test_string set not null;
alter table migtest_e_history2 add column if not exists test_string2 varchar(255);
alter table migtest_e_history2 add column if not exists test_string3 varchar(255) default 'unknown' not null;
alter table migtest_e_history2 add column if not exists new_column varchar(20);
alter table migtest_e_history2_history add column if not exists test_string2 varchar(255);
alter table migtest_e_history2_history add column if not exists test_string3 varchar(255) default 'unknown';
alter table migtest_e_history2_history add column if not exists new_column varchar(20);
alter table migtest_e_history4 alter column test_number type bigint using test_number::bigint;
alter table migtest_e_history4_history alter column test_number type bigint using test_number::bigint;
alter table migtest_e_history5 add column if not exists test_boolean boolean default false not null;
alter table migtest_e_history5_history add column if not exists test_boolean boolean default false;
alter table migtest_e_history6 alter column test_number1 set default 42;
alter table migtest_e_history6 alter column test_number1 set not null;
alter table migtest_e_history6 alter column test_number2 drop not null;
alter table migtest_e_history6_history alter column test_number2 drop not null;
alter table migtest_e_softdelete add column if not exists deleted boolean default false not null;
alter table migtest_oto_child add column if not exists master_id bigint;
alter table table_history alter column textfield drop not null;
alter table table_history add column if not exists "select" varchar(255);
alter table table_history add column if not exists textfield2 varchar(255);
-- apply post alter
alter table migtest_e_basic add constraint ck_migtest_e_basic_status check ( status in ('N','A','I','?'));
alter table migtest_e_basic add constraint uq_migtest_e_basic_description unique  (description);
update migtest_e_basic set new_boolean_field = old_boolean;

alter table migtest_e_basic add constraint ck_migtest_e_basic_progress check ( progress in (0,1,2));
alter table migtest_e_basic add constraint uq_migtest_e_basic_status_indextest1 unique  (status,indextest1);
alter table migtest_e_basic add constraint uq_migtest_e_basic_name unique  (name);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest4 unique  (indextest4);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest5 unique  (indextest5);
create table migtest_e_history_history(like migtest_e_history);
create view migtest_e_history_with_history as select * from migtest_e_history union all select * from migtest_e_history_history;
create or replace function migtest_e_history_history_version() returns trigger as $$
-- play-ebean-start
declare
  lowerTs timestamptz;
  upperTs timestamptz;
begin
  lowerTs = lower(OLD.sys_period);
  upperTs = greatest(lowerTs + '1 microsecond',current_timestamp);
  if (TG_OP = 'UPDATE') then
    insert into migtest_e_history_history (sys_period,id, test_string) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history_history (sys_period,id, test_string) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string);
    return old;
  end if;
end;
-- play-ebean-end
$$ LANGUAGE plpgsql;

create trigger migtest_e_history_history_upd
  before update or delete on migtest_e_history
  for each row execute procedure migtest_e_history_history_version();


comment on column migtest_e_history.test_string is 'Column altered to long now';
comment on table migtest_e_history is 'We have history now';
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
    insert into migtest_e_history2_history (sys_period,id, test_string, test_string3, new_column, obsolete_string1, obsolete_string2) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string, OLD.test_string3, OLD.new_column, OLD.obsolete_string1, OLD.obsolete_string2);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history2_history (sys_period,id, test_string, test_string3, new_column, obsolete_string1, obsolete_string2) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_string, OLD.test_string3, OLD.new_column, OLD.obsolete_string1, OLD.obsolete_string2);
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
    insert into migtest_e_history3_history (sys_period,id) values (tstzrange(lowerTs,upperTs), OLD.id);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history3_history (sys_period,id) values (tstzrange(lowerTs,upperTs), OLD.id);
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

create view migtest_e_history5_with_history as select * from migtest_e_history5 union all select * from migtest_e_history5_history;
create or replace function migtest_e_history5_history_version() returns trigger as $$
-- play-ebean-start
declare
  lowerTs timestamptz;
  upperTs timestamptz;
begin
  lowerTs = lower(OLD.sys_period);
  upperTs = greatest(lowerTs + '1 microsecond',current_timestamp);
  if (TG_OP = 'UPDATE') then
    insert into migtest_e_history5_history (sys_period,id, test_number, test_boolean) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_number, OLD.test_boolean);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into migtest_e_history5_history (sys_period,id, test_number, test_boolean) values (tstzrange(lowerTs,upperTs), OLD.id, OLD.test_number, OLD.test_boolean);
    return old;
  end if;
end;
-- play-ebean-end
$$ LANGUAGE plpgsql;

create trigger migtest_e_history5_history_upd
  before update or delete on migtest_e_history5
  for each row execute procedure migtest_e_history5_history_version();

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

comment on column "table"."index" is 'this is an other comment';
create view table_with_history as select * from "table" union all select * from table_history;
create or replace function table_history_version() returns trigger as $$
-- play-ebean-start
declare
  lowerTs timestamptz;
  upperTs timestamptz;
begin
  lowerTs = lower(OLD.sys_period);
  upperTs = greatest(lowerTs + '1 microsecond',current_timestamp);
  if (TG_OP = 'UPDATE') then
    insert into table_history (sys_period,"index", "from", "to", "varchar", "select", "foreign", textfield, textfield2) values (tstzrange(lowerTs,upperTs), OLD."index", OLD."from", OLD."to", OLD."varchar", OLD."select", OLD."foreign", OLD.textfield, OLD.textfield2);
    NEW.sys_period = tstzrange(upperTs,null);
    return new;
  elsif (TG_OP = 'DELETE') then
    insert into table_history (sys_period,"index", "from", "to", "varchar", "select", "foreign", textfield, textfield2) values (tstzrange(lowerTs,upperTs), OLD."index", OLD."from", OLD."to", OLD."varchar", OLD."select", OLD."foreign", OLD.textfield, OLD.textfield2);
    return old;
  end if;
end;
-- play-ebean-end
$$ LANGUAGE plpgsql;

create trigger table_history_upd
  before update or delete on "table"
  for each row execute procedure table_history_version();

alter table "table" add constraint uq_table_select unique  ("select");
-- foreign keys and indices
create index ix_drop_main_drop_ref_many_drop_main on drop_main_drop_ref_many (drop_main_id);
alter table drop_main_drop_ref_many add constraint fk_drop_main_drop_ref_many_drop_main foreign key (drop_main_id) references drop_main (id) on delete restrict on update restrict;

create index ix_drop_main_drop_ref_many_drop_ref_many on drop_main_drop_ref_many (drop_ref_many_id);
alter table drop_main_drop_ref_many add constraint fk_drop_main_drop_ref_many_drop_ref_many foreign key (drop_ref_many_id) references drop_ref_many (id) on delete restrict on update restrict;

create index ix_drop_ref_one_parent_id on drop_ref_one (parent_id);
alter table drop_ref_one add constraint fk_drop_ref_one_parent_id foreign key (parent_id) references drop_main (id) on delete restrict on update restrict;

alter table drop_ref_one_to_one add constraint fk_drop_ref_one_to_one_parent_id foreign key (parent_id) references drop_main (id) on delete restrict on update restrict;

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

alter table migtest_ckey_detail add constraint fk_migtest_ckey_detail_parent foreign key (one_key,two_key) references migtest_ckey_parent (one_key,two_key) on delete restrict on update restrict;
create index ix_migtest_ckey_parent_assoc_id on migtest_ckey_parent (assoc_id);
alter table migtest_ckey_parent add constraint fk_migtest_ckey_parent_assoc_id foreign key (assoc_id) references migtest_ckey_assoc (id) on delete restrict on update restrict;

alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete restrict on update restrict;
alter table migtest_fk_none add constraint fk_migtest_fk_none_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;
alter table migtest_fk_none_via_join add constraint fk_migtest_fk_none_via_join_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;
alter table migtest_e_basic add constraint fk_migtest_e_basic_user_id foreign key (user_id) references migtest_e_user (id) on delete restrict on update restrict;
alter table migtest_oto_child add constraint fk_migtest_oto_child_master_id foreign key (master_id) references migtest_oto_master (id) on delete restrict on update restrict;

create index if not exists ix_migtest_e_basic_indextest3 on migtest_e_basic (indextest3);
create index if not exists ix_migtest_e_basic_indextest6 on migtest_e_basic (indextest6);
create index if not exists ix_migtest_e_basic_indextest7 on migtest_e_basic (indextest7);
create index if not exists ix_table_textfield2 on "table" (textfield2);
create index if not exists ix_migtest_oto_child_name on migtest_oto_child (name);
