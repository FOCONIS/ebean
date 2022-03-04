-- Migrationscripts for ebean unittest
-- apply changes
SET @@system_versioning_alter_history = 1;
drop sequence if exists migtest_e_ref_seq;
-- apply alter tables
alter table migtest_e_basic drop column description_file;
alter table migtest_e_basic drop column old_boolean;
alter table migtest_e_basic drop column old_boolean2;
alter table migtest_e_basic drop column eref_id;
alter table migtest_e_history2 drop column obsolete_string1;
alter table migtest_e_history2 drop column obsolete_string2;
-- apply post alter
drop table if exists migtest_e_ref;
