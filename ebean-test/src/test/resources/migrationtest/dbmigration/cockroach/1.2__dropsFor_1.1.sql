-- Migrationscripts for ebean unittest
-- apply alter tables
alter table migtest_e_basic drop column description_file;
alter table migtest_e_basic drop column old_boolean;
alter table migtest_e_basic drop column old_boolean2;
alter table migtest_e_basic drop column eref_id;
alter table migtest_e_history2 drop column obsolete_string1;
alter table migtest_e_history2 drop column obsolete_string2;
-- apply post alter
drop table if exists migtest_drop_main cascade;
drop sequence if exists migtest_drop_main_seq;
drop table if exists migtest_drop_main_migtest_drop_ref_many cascade;
drop table if exists migtest_drop_ref_many cascade;
drop sequence if exists migtest_drop_ref_many_seq;
drop table if exists migtest_drop_ref_one cascade;
drop sequence if exists migtest_drop_ref_one_seq;
drop table if exists migtest_drop_ref_one_to_one cascade;
drop sequence if exists migtest_drop_ref_one_to_one_seq;
drop table if exists "migtest_QuOtEd" cascade;
drop table if exists migtest_e_ref cascade;
drop sequence if exists migtest_e_ref_seq;
