-- Migrationscripts for ebean unittest
-- drop dependencies
drop trigger migtest_e_history_history_upd;
drop trigger migtest_e_history_history_del;
-- apply changes
CALL usp_ebean_drop_column('migtest_ckey_detail', 'one_key');
CALL usp_ebean_drop_column('migtest_ckey_detail', 'two_key');
CALL usp_ebean_drop_column('migtest_ckey_parent', 'assoc_id');
CALL usp_ebean_drop_column('migtest_e_basic', 'new_string_field');
CALL usp_ebean_drop_column('migtest_e_basic', 'new_boolean_field');
CALL usp_ebean_drop_column('migtest_e_basic', 'new_boolean_field2');
CALL usp_ebean_drop_column('migtest_e_basic', 'progress');
CALL usp_ebean_drop_column('migtest_e_basic', 'new_integer');
drop view migtest_e_history_with_history;
CALL usp_ebean_drop_column('migtest_e_history', 'sys_period_start');
CALL usp_ebean_drop_column('migtest_e_history', 'sys_period_end');
CALL usp_ebean_drop_column('migtest_e_history2', 'test_string2');
CALL usp_ebean_drop_column('migtest_e_history2_history', 'test_string2');
CALL usp_ebean_drop_column('migtest_e_history2', 'test_string3');
CALL usp_ebean_drop_column('migtest_e_history2_history', 'test_string3');
CALL usp_ebean_drop_column('migtest_e_history2', 'new_column');
CALL usp_ebean_drop_column('migtest_e_history2_history', 'new_column');
CALL usp_ebean_drop_column('migtest_e_history5', 'test_boolean');
CALL usp_ebean_drop_column('migtest_e_history5_history', 'test_boolean');
CALL usp_ebean_drop_column('migtest_e_softdelete', 'deleted');
CALL usp_ebean_drop_column('migtest_oto_child', 'master_id');
-- post alter
drop table migtest_e_history_history;

drop table if exists migtest_e_user;
drop table if exists migtest_mtm_c_migtest_mtm_m;
drop table if exists migtest_mtm_m_migtest_mtm_c;
drop table if exists migtest_mtm_m_phone_numbers;
