-- Inital script to create stored procedures etc for sqlserver

-- create table-value-parameters
if not exists (select name  from sys.types where name = 'ebean_bigint_tvp') create type ebean_bigint_tvp as table (c1 bigint);
if not exists (select name  from sys.types where name = 'ebean_float_tvp') create type ebean_float_tvp as table (c1 float);
if not exists (select name  from sys.types where name = 'ebean_bit_tvp') create type ebean_bit_tvp as table (c1 bit);
if not exists (select name  from sys.types where name = 'ebean_date_tvp') create type ebean_date_tvp as table (c1 date);
if not exists (select name  from sys.types where name = 'ebean_time_tvp') create type ebean_time_tvp as table (c1 time);
if not exists (select name  from sys.types where name = 'ebean_uniqueidentifier_tvp') create type ebean_uniqueidentifier_tvp as table (c1 uniqueidentifier);
if not exists (select name  from sys.types where name = 'ebean_nvarchar_tvp') create type ebean_nvarchar_tvp as table (c1 nvarchar(max));

delimiter $$
------------------------------------------------------------------------
-- This procedure deletes all indices refering to @tableName.@columnName
------------------------------------------------------------------------
CREATE OR ALTER PROCEDURE usp_ebean_drop_indices @tableName nvarchar(255), @columnName nvarchar(255)
AS 
declare @sql nvarchar(1000)
declare @indexName nvarchar(255)
BEGIN
  DECLARE index_cursor CURSOR FOR SELECT i.name from sys.indexes i 
    join sys.index_columns ic on ic.object_id = i.object_id and ic.index_id = i.index_id
    join sys.columns c on c.object_id = ic.object_id and c.column_id = ic.column_id
    where i.object_id = OBJECT_ID(@tableName) AND c.name = @columnName;
  OPEN index_cursor
  FETCH NEXT FROM index_cursor INTO @indexName
  WHILE @@FETCH_STATUS = 0
    BEGIN
      set @sql = 'drop index ' + @indexName + ' on ' + @tableName;
      EXEC sp_executesql @sql;

      FETCH NEXT FROM index_cursor INTO @indexName
    END;
  CLOSE index_cursor;  
  DEALLOCATE index_cursor;  
END
$$

delimiter $$
----------------------------------------------------------------------------
-- This procedure deletes all constraints refering to @tableName.@columnName
----------------------------------------------------------------------------
CREATE OR ALTER PROCEDURE usp_ebean_drop_constraints @tableName nvarchar(255), @columnName nvarchar(255)
AS 
declare @sql nvarchar(1000)
declare @constraintName nvarchar(255)
BEGIN
  DECLARE name_cursor CURSOR FOR 
  SELECT dc.name from sys.default_constraints dc
    join sys.columns c on c.object_id = dc.parent_object_id and c.column_id = dc.parent_column_id
    where parent_object_id = OBJECT_ID(@tableName) AND c.name = @columnName
  UNION SELECT cc.name from sys.check_constraints cc
    join sys.columns c on c.object_id = cc.parent_object_id and c.column_id = cc.parent_column_id
    where parent_object_id = OBJECT_ID(@tableName) AND c.name = @columnName
  OPEN name_cursor
  FETCH NEXT FROM name_cursor INTO @constraintName
  WHILE @@FETCH_STATUS = 0
    BEGIN
      set @sql = 'alter table ' + @tableName + ' drop constraint ' + @constraintName;
      EXEC sp_executesql @sql;

      FETCH NEXT FROM name_cursor INTO @constraintName
    END;
  CLOSE name_cursor;  
  DEALLOCATE name_cursor;  
END
$$

delimiter $$
-----------------------------------------------------------
-- This procedure deletes the column @tableName.@columnName
-- it ensures that all references are dropped first
-----------------------------------------------------------
CREATE OR ALTER PROCEDURE usp_ebean_drop_column @tableName nvarchar(255), @columnName nvarchar(255)
AS 
declare @sql nvarchar(1000)
BEGIN
  EXEC usp_ebean_drop_indices @tableName, @columnName;
  EXEC usp_ebean_drop_constraints @tableName, @columnName;

  set @sql = 'alter table ' + @tableName + ' drop column ' + @columnName;
  EXEC sp_executesql @sql;
END
$$
