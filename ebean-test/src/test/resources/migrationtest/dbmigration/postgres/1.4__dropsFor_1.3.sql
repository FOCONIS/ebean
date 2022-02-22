-- Migrationscripts for ebean unittest
-- drop dependencies
drop trigger if exists migtest_e_history_history_upd on migtest_e_history cascade;
drop function if exists migtest_e_history_history_version();

-- apply changes
drop view migtest_e_history_with_history;
drop trigger if exists migtest_e_history2_history_upd on migtest_e_history2 cascade;
drop function if exists migtest_e_history2_history_version();

drop view migtest_e_history2_with_history;
drop trigger if exists migtest_e_history5_history_upd on migtest_e_history5 cascade;
drop function if exists migtest_e_history5_history_version();

drop view migtest_e_history5_with_history;
drop sequence if exists migtest_e_user_seq;
-- altering tables
alter table migtest_ckey_detail drop column one_key;
alter table migtest_ckey_detail drop column two_key;
alter table migtest_ckey_parent drop column assoc_id;
alter table migtest_e_basic drop column new_string_field;
alter table migtest_e_basic drop column new_boolean_field;
alter table migtest_e_basic drop column new_boolean_field2;
alter table migtest_e_basic drop column progress;
alter table migtest_e_basic drop column new_integer;
alter table migtest_e_history drop column sys_period;
alter table migtest_e_history2 drop column test_string2;
alter table migtest_e_history2 drop column test_string3;
alter table migtest_e_history2 drop column new_column;
alter table migtest_e_history2_history drop column test_string2;
alter table migtest_e_history2_history drop column test_string3;
alter table migtest_e_history2_history drop column new_column;
alter table migtest_e_history5 drop column test_boolean;
alter table migtest_e_history5_history drop column test_boolean;
alter table migtest_e_softdelete drop column deleted;
alter table migtest_oto_child drop column master_id;
-- post alter
drop table migtest_e_history_history;

create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;

create trigger migtest_e_history2_history_upd
  before update or delete on migtest_e_history2
  for each row execute procedure migtest_e_history2_history_version();

create view migtest_e_history5_with_history as select * from migtest_e_history5 union all select * from migtest_e_history5_history;

create trigger migtest_e_history5_history_upd
  before update or delete on migtest_e_history5
  for each row execute procedure migtest_e_history5_history_version();

drop table if exists migtest_e_user cascade;
drop table if exists migtest_mtm_c_migtest_mtm_m cascade;
drop table if exists migtest_mtm_m_migtest_mtm_c cascade;
drop table if exists migtest_mtm_m_phone_numbers cascade;
