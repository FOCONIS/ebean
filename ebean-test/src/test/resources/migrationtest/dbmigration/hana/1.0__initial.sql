-- Migrationscripts for ebean unittest
-- apply changes
create column table migtest_ckey_assoc (
  id                            integer generated by default as identity not null,
  assoc_one                     nvarchar(255),
  constraint pk_migtest_ckey_assoc primary key (id)
);

create column table migtest_ckey_detail (
  id                            integer generated by default as identity not null,
  something                     nvarchar(255),
  constraint pk_migtest_ckey_detail primary key (id)
);

create column table migtest_ckey_parent (
  one_key                       integer not null,
  two_key                       nvarchar(127) not null,
  name                          nvarchar(255),
  version                       integer not null,
  constraint pk_migtest_ckey_parent primary key (one_key,two_key)
);

create column table migtest_fk_cascade (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_cascade primary key (id)
);

create column table migtest_fk_cascade_one (
  id                            bigint generated by default as identity not null,
  constraint pk_migtest_fk_cascade_one primary key (id)
);

create column table migtest_fk_none (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none primary key (id)
);

create column table migtest_fk_none_via_join (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none_via_join primary key (id)
);

create column table migtest_fk_one (
  id                            bigint generated by default as identity not null,
  constraint pk_migtest_fk_one primary key (id)
);

create column table migtest_fk_set_null (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_set_null primary key (id)
);

create column table migtest_e_basic (
  id                            integer generated by default as identity not null,
  status                        nvarchar(1),
  status2                       nvarchar(1) default 'N' not null,
  name                          nvarchar(127),
  description                   nvarchar(127),
  some_date                     timestamp,
  old_boolean                   boolean default false not null,
  old_boolean2                  boolean,
  eref_id                       integer,
  indextest1                    nvarchar(127),
  indextest2                    nvarchar(127),
  indextest3                    nvarchar(127),
  indextest4                    nvarchar(127),
  indextest5                    nvarchar(127),
  indextest6                    nvarchar(127),
  user_id                       integer not null,
  constraint ck_migtest_e_basic_status check ( status in ('N','A','I')),
  constraint ck_migtest_e_basic_status2 check ( status2 in ('N','A','I')),
  constraint uq_migtest_e_basic_indextest2 unique (indextest2),
  constraint uq_migtest_e_basic_indextest6 unique (indextest6),
  constraint pk_migtest_e_basic primary key (id)
);

create column table migtest_e_enum (
  id                            integer generated by default as identity not null,
  test_status                   nvarchar(1),
  constraint ck_migtest_e_enum_test_status check ( test_status in ('N','A','I')),
  constraint pk_migtest_e_enum primary key (id)
);

create column table migtest_e_history (
  id                            integer generated by default as identity not null,
  test_string                   nvarchar(255),
  constraint pk_migtest_e_history primary key (id)
);

create column table migtest_e_history2 (
  id                            integer generated by default as identity not null,
  test_string                   nvarchar(255),
  obsolete_string1              nvarchar(255),
  obsolete_string2              nvarchar(255),
  constraint pk_migtest_e_history2 primary key (id)
);
create column table migtest_e_history2_history (
 id integer,
 test_string nvarchar(255),
 obsolete_string1 nvarchar(255),
 obsolete_string2 nvarchar(255),
 sys_period_start timestamp,
 sys_period_end timestamp
);

create column table migtest_e_history3 (
  id                            integer generated by default as identity not null,
  test_string                   nvarchar(255),
  constraint pk_migtest_e_history3 primary key (id)
);
create column table migtest_e_history3_history (
 id integer,
 test_string nvarchar(255),
 sys_period_start timestamp,
 sys_period_end timestamp
);

create column table migtest_e_history4 (
  id                            integer generated by default as identity not null,
  test_number                   integer,
  constraint pk_migtest_e_history4 primary key (id)
);
create column table migtest_e_history4_history (
 id integer,
 test_number integer,
 sys_period_start timestamp,
 sys_period_end timestamp
);

create column table migtest_e_history5 (
  id                            integer generated by default as identity not null,
  test_number                   integer,
  constraint pk_migtest_e_history5 primary key (id)
);
create column table migtest_e_history5_history (
 id integer,
 test_number integer,
 sys_period_start timestamp,
 sys_period_end timestamp
);

create column table migtest_e_history6 (
  id                            integer generated by default as identity not null,
  test_number1                  integer,
  test_number2                  integer not null,
  constraint pk_migtest_e_history6 primary key (id)
);
create column table migtest_e_history6_history (
 id integer,
 test_number1 integer,
 test_number2 integer not null,
 sys_period_start timestamp,
 sys_period_end timestamp
);

create column table migtest_e_ref (
  id                            integer generated by default as identity not null,
  name                          nvarchar(127) not null,
  constraint uq_migtest_e_ref_name unique (name),
  constraint pk_migtest_e_ref primary key (id)
);

create column table migtest_e_softdelete (
  id                            integer generated by default as identity not null,
  test_string                   nvarchar(255),
  constraint pk_migtest_e_softdelete primary key (id)
);

create column table migtest_mtm_c (
  id                            integer generated by default as identity not null,
  name                          nvarchar(255),
  constraint pk_migtest_mtm_c primary key (id)
);

create column table migtest_mtm_m (
  id                            bigint generated by default as identity not null,
  name                          nvarchar(255),
  constraint pk_migtest_mtm_m primary key (id)
);

create column table migtest_oto_child (
  id                            integer generated by default as identity not null,
  name                          nvarchar(255),
  constraint pk_migtest_oto_child primary key (id)
);

create column table migtest_oto_master (
  id                            bigint generated by default as identity not null,
  name                          nvarchar(255),
  constraint pk_migtest_oto_master primary key (id)
);

-- altering tables
alter table migtest_e_history2 add (sys_period_start TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START,
   sys_period_end TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END);
alter table migtest_e_history2 add period for system_time(sys_period_start,sys_period_end);
alter table migtest_e_history2 add system versioning history table migtest_e_history2_history;
alter table migtest_e_history3 add (sys_period_start TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START,
   sys_period_end TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END);
alter table migtest_e_history3 add period for system_time(sys_period_start,sys_period_end);
alter table migtest_e_history3 add system versioning history table migtest_e_history3_history;
alter table migtest_e_history4 add (sys_period_start TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START,
   sys_period_end TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END);
alter table migtest_e_history4 add period for system_time(sys_period_start,sys_period_end);
alter table migtest_e_history4 add system versioning history table migtest_e_history4_history;
alter table migtest_e_history5 add (sys_period_start TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START,
   sys_period_end TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END);
alter table migtest_e_history5 add period for system_time(sys_period_start,sys_period_end);
alter table migtest_e_history5 add system versioning history table migtest_e_history5_history;
alter table migtest_e_history6 add (sys_period_start TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START,
   sys_period_end TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END);
alter table migtest_e_history6 add period for system_time(sys_period_start,sys_period_end);
alter table migtest_e_history6 add system versioning history table migtest_e_history6_history;
-- indices/constraints
-- explicit index "ix_migtest_fk_cascade_one_id" for single column "one_id" of table "migtest_fk_cascade" is not necessary;
alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade on update restrict;

-- explicit index "ix_migtest_fk_set_null_one_id" for single column "one_id" of table "migtest_fk_set_null" is not necessary;
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete set null on update restrict;

-- explicit index "ix_migtest_e_basic_eref_id" for single column "eref_id" of table "migtest_e_basic" is not necessary;
alter table migtest_e_basic add constraint fk_migtest_e_basic_eref_id foreign key (eref_id) references migtest_e_ref (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_e_basic_indextest1" for single column "indextest1" of table "migtest_e_basic" is not necessary;
-- explicit index "ix_migtest_e_basic_indextest5" for single column "indextest5" of table "migtest_e_basic" is not necessary;
