-- Migrationscripts for ebean unittest
-- apply changes
drop trigger if exists migtest_e_history2_history_upd on migtest_e_history2 cascade;
drop function if exists migtest_e_history2_history_version();

drop view migtest_e_history2_with_history;
drop sequence if exists migtest_e_ref_seq;
-- altering tables
alter table migtest_e_basic drop column old_boolean;
alter table migtest_e_basic drop column old_boolean2;
alter table migtest_e_basic drop column eref_id;
alter table migtest_e_history2 drop column obsolete_string1;
alter table migtest_e_history2 drop column obsolete_string2;
alter table migtest_e_history2_history drop column obsolete_string1;
alter table migtest_e_history2_history drop column obsolete_string2;
-- post alter
create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;

create trigger migtest_e_history2_history_upd
  before update or delete on migtest_e_history2
  for each row execute procedure migtest_e_history2_history_version();

drop table if exists migtest_e_ref cascade;
