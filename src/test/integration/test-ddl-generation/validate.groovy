File pgshared = new File(basedir, 'src/main/resources/dbmigration/myapp/postgres/shared/V1.0__initial.sql')
File pgtenant = new File(basedir, 'src/main/resources/dbmigration/myapp/postgres/tenant/V1.0__initial.sql')
assert pgshared.exists()
assert pgtenant.exists()
assert pgshared.text.contains("create table public.global_test_model")
assert pgtenant.text.contains("create table local_model")
assert pgtenant.text.contains("create table model_under_test_a")
assert pgtenant.text.contains("create index ix_model_under_test_a_name on model_under_test_a (name);")
assert pgtenant.text.contains("alter table model_under_test_a add constraint fk_model_under_test_a_test_model_id foreign key (test_model_id) references public.global_test_model (id) on delete restrict on update restrict;")
