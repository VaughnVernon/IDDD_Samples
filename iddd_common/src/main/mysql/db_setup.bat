@echo off
rem -------------------------------------------
rem IDDD Common database setup
rem -------------------------------------------

echo Creating IDDD Common test database...
type test_common.sql common.sql > create_test_common.sql
mysql -u root -p < create_test_common.sql
del /Q create_test_common.sql

echo Completed
