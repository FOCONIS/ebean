-- Migrationscripts for ebean unittest
-- apply changes
drop trigger migtest_e_history2_history_upd;
drop trigger migtest_e_history2_history_del;
drop view migtest_e_history2_with_history;
-- altering tables
CALL usp_ebean_drop_column('migtest_e_basic', 'old_boolean');
CALL usp_ebean_drop_column('migtest_e_basic', 'old_boolean2');
CALL usp_ebean_drop_column('migtest_e_basic', 'eref_id');
CALL usp_ebean_drop_column('migtest_e_history2', 'obsolete_string1');
CALL usp_ebean_drop_column('migtest_e_history2', 'obsolete_string2');
CALL usp_ebean_drop_column('migtest_e_history2_history', 'obsolete_string1');
CALL usp_ebean_drop_column('migtest_e_history2_history', 'obsolete_string2');
-- post alter
create view migtest_e_history2_with_history as select * from migtest_e_history2 union all select * from migtest_e_history2_history;

delimiter $$
create trigger migtest_e_history2_history_upd before update on migtest_e_history2 for each row begin
    insert into migtest_e_history2_history (sys_period_start,sys_period_end,id, test_string, test_string3, new_column) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string, OLD.test_string3, OLD.new_column);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger migtest_e_history2_history_del before delete on migtest_e_history2 for each row begin
    insert into migtest_e_history2_history (sys_period_start,sys_period_end,id, test_string, test_string3, new_column) values (OLD.sys_period_start, now(6),OLD.id, OLD.test_string, OLD.test_string3, OLD.new_column);
end$$
drop table if exists migtest_e_ref;
