-- Migrationscripts for ebean unittest
-- apply changes
alter table migtest_ckey_detail drop column one_key;

alter table migtest_ckey_detail drop column two_key;

alter table migtest_ckey_parent drop column assoc_id;

alter table migtest_e_basic drop column new_string_field;

alter table migtest_e_basic drop column new_boolean_field;

alter table migtest_e_basic drop column new_boolean_field2;

alter table migtest_e_basic drop column progress;

alter table migtest_e_basic drop column new_integer;

alter table migtest_e_history2 drop column test_string2;

alter table migtest_e_history2 drop column test_string3;

alter table migtest_e_history2 drop column new_column;

alter table migtest_e_history5 drop column test_boolean;

alter table migtest_e_softdelete drop column deleted;

alter table migtest_oto_child drop column master_id;

drop table migtest_e_user;
delimiter $$
begin
if exists (select seqschema from syscat.sequences where seqschema = current_schema and seqname='migtest_e_user_seq') then
  prepare stmt from 'drop sequence migtest_e_user_seq';
  execute stmt;
end if;
end$$;
drop table migtest_mtm_c_migtest_mtm_m;
drop table migtest_mtm_m_migtest_mtm_c;
call sysproc.admin_cmd('reorg table migtest_e_history2') /* reorg #1 */;
call sysproc.admin_cmd('reorg table migtest_e_softdelete') /* reorg #2 */;
call sysproc.admin_cmd('reorg table migtest_oto_child') /* reorg #3 */;
call sysproc.admin_cmd('reorg table migtest_ckey_parent') /* reorg #4 */;
call sysproc.admin_cmd('reorg table migtest_e_history5') /* reorg #5 */;
call sysproc.admin_cmd('reorg table migtest_ckey_detail') /* reorg #6 */;
call sysproc.admin_cmd('reorg table migtest_e_basic') /* reorg #7 */;
