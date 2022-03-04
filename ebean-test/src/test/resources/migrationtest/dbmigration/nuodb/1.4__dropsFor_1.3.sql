-- Migrationscripts for ebean unittest
-- drop dependencies
drop trigger migtest_e_history_history_upd;
drop trigger migtest_e_history_history_del;
drop view migtest_e_history_with_history;
drop table migtest_e_history_history;

-- apply changes
drop trigger migtest_e_history2_history_upd;
drop trigger migtest_e_history2_history_del;
drop view migtest_e_history2_with_history;
drop trigger migtest_e_history5_history_upd;
drop trigger migtest_e_history5_history_del;
drop view migtest_e_history5_with_history;
drop sequence if exists migtest_e_user_seq;
-- apply alter tables
alter table migtest_ckey_detail drop column one_key;
alter table migtest_ckey_detail drop column two_key;
alter table migtest_ckey_parent drop column assoc_id;
alter table migtest_e_basic drop column new_string_field;
alter table migtest_e_basic drop column new_boolean_field;
alter table migtest_e_basic drop column new_boolean_field2;
alter table migtest_e_basic drop column progress;
alter table migtest_e_basic drop column new_integer;
alter table migtest_e_history drop column sys_period_start;
alter table migtest_e_history drop column sys_period_end;
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
-- apply post alter
create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;

create view migtest_e_history5_with_history as select * from migtest_e_history5 union all select * from migtest_e_history5_history;

drop table if exists migtest_e_user;
drop table if exists migtest_mtm_c_migtest_mtm_m;
drop table if exists migtest_mtm_m_migtest_mtm_c;
drop table if exists migtest_mtm_m_phone_numbers;
