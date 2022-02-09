-- Migrationscripts for ebean unittest DbMigrationDropHistoryTest
-- drop dependencies
drop trigger migtest_e_history7_history_upd;
-- apply changes
drop view migtest_e_history7_with_history;
-- altering tables
alter table migtest_e_history7 drop column sys_period_start;
alter table migtest_e_history7 drop column sys_period_end;
-- post alter
drop table migtest_e_history7_history;

