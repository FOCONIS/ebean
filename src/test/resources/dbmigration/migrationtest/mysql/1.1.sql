-- apply changes
UPDATE migtest_e_basic set status = 'A' WHERE status is null;
alter table migtest_e_basic alter status set default A;
alter table migtest_e_basic modify status varchar(1) not null;
alter table migtest_e_basic add constraint ck_migtest_e_basic_status check ( status in ('N','A','I','?'));
alter table migtest_e_basic add constraint uq_migtest_e_basic_description unique  (description);
UPDATE migtest_e_basic set some_date = '2000-01-01T00:00:00' WHERE some_date is null;
alter table migtest_e_basic alter some_date set default '2000-01-01T00:00:00';
alter table migtest_e_basic modify some_date datetime(6) not null;
alter table migtest_e_basic add constraint fk_migtest_e_basic_user_id foreign key (user_id) references migtest_e_user (id) on delete restrict on update restrict;
alter table migtest_e_basic modify user_id integer;
alter table migtest_e_basic add column new_string_field varchar(255) not null default 'foo';
alter table migtest_e_basic add column new_boolean_field tinyint(1) not null default 1;
update migtest_e_basic set new_boolean_field = old_boolean;

alter table migtest_e_basic add column new_boolean_field2 tinyint(1) not null default 0;
alter table migtest_e_basic add column progress integer not null constraint ck_migtest_e_basic_progress check ( progress in ('0','1','2')) default 0;

alter table migtest_e_basic drop index uq_migtest_e_basic_indextest2;
alter table migtest_e_basic drop index uq_migtest_e_basic_indextest6;
alter table migtest_e_basic add constraint uq_migtest_e_basic_name unique  (name);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest4 unique  (indextest4);
alter table migtest_e_basic add constraint uq_migtest_e_basic_indextest5 unique  (indextest5);
comment on column migtest_e_history.test_string is 'Column altered to long now';
alter table migtest_e_history modify test_string bigint;
alter table migtest_e_history comment = 'We have history now';
create table migtest_e_user (
  id                            integer auto_increment not null,
  constraint pk_migtest_e_user primary key (id)
);

create index ix_migtest_e_basic_indextest3 on migtest_e_basic (indextest3);
create index ix_migtest_e_basic_indextest6 on migtest_e_basic (indextest6);
drop index ix_migtest_e_basic_indextest1 on migtest_e_basic;
drop index ix_migtest_e_basic_indextest5 on migtest_e_basic;
alter table migtest_e_history add column sys_period_start datetime(6) default now(6);
alter table migtest_e_history add column sys_period_end datetime(6);
create table migtest_e_history_history(
  id                            integer,
  test_string                   bigint,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view migtest_e_history_with_history as select * from migtest_e_history union all select * from migtest_e_history_history;

delimiter $$
create trigger migtest_e_history_history_upd before update on migtest_e_history for each row begin
    insert into migtest_e_history_history (sys_period_start,sys_period_end,id, test_string) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history_history_del before delete on migtest_e_history for each row begin
    insert into migtest_e_history_history (sys_period_start,sys_period_end,id, test_string) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string);
end$$
