-- Migrationscripts for ebean unittest
-- apply changes
CALL usp_ebean_drop_column('migtest_e_basic', 'old_boolean');

CALL usp_ebean_drop_column('migtest_e_basic', 'old_boolean2');

CALL usp_ebean_drop_column('migtest_e_basic', 'eref_id');

CALL usp_ebean_drop_column('migtest_e_history2', 'obsolete_string1');

CALL usp_ebean_drop_column('migtest_e_history2_history', 'obsolete_string1');
CALL usp_ebean_drop_column('migtest_e_history2', 'obsolete_string2');

CALL usp_ebean_drop_column('migtest_e_history2_history', 'obsolete_string2');
drop table migtest_e_ref cascade;
-- altering tables
alter table migtest_e_history2 drop system versioning;
alter table migtest_e_history2 add system versioning history table migtest_e_history2_history not validated;
alter table migtest_e_history2 drop system versioning;
alter table migtest_e_history2 add system versioning history table migtest_e_history2_history not validated;
