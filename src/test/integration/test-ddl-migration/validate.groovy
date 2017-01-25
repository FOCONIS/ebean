File dump = new File(basedir, 'dump-create-all.sql')
assert dump.exists()

// there must be a migration table in each tenant
assert dump.text.contains("CREATE MEMORY TABLE PUBLIC.DB_MIGRATION")
assert dump.text.contains("CREATE MEMORY TABLE TENANT_1.DB_MIGRATION")
assert dump.text.contains("CREATE MEMORY TABLE TENANT_2.DB_MIGRATION")


// public elements
assert dump.text.contains("CREATE MEMORY TABLE PUBLIC.GLOBAL_CACHABLE")
assert dump.text.contains("CREATE MEMORY TABLE PUBLIC.GLOBAL_TEST_MODEL")

// Tables per tenant
assert dump.text.contains("CREATE MEMORY TABLE TENANT_1.LOCAL_CACHABLE")
assert dump.text.contains("CREATE MEMORY TABLE TENANT_1.LOCAL_MODEL")
assert dump.text.contains("CREATE MEMORY TABLE TENANT_2.LOCAL_CACHABLE")
assert dump.text.contains("CREATE MEMORY TABLE TENANT_2.LOCAL_MODEL")

// Keys up to PUBLIC
assert dump.text.contains("ALTER TABLE TENANT_1.LOCAL_MODEL ADD CONSTRAINT TENANT_1.FK_LOCAL_MODEL_GLOBAL_TEST_MODEL_ID FOREIGN KEY(GLOBAL_TEST_MODEL_ID) REFERENCES PUBLIC.GLOBAL_TEST_MODEL(ID) NOCHECK;")               
assert dump.text.contains("ALTER TABLE TENANT_2.LOCAL_MODEL ADD CONSTRAINT TENANT_2.FK_LOCAL_MODEL_GLOBAL_TEST_MODEL_ID FOREIGN KEY(GLOBAL_TEST_MODEL_ID) REFERENCES PUBLIC.GLOBAL_TEST_MODEL(ID) NOCHECK;")               
