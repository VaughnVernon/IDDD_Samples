@echo off
rem -------------------------------------------
rem ShiftMETHOD IdentityAccess database setup
rem -------------------------------------------

echo Creating IDDD IdentityAccess database...
type iam.sql ..\..\..\..\iddd_common\src\main\mysql\common.sql > create_iam.sql
mysql -u root -p < create_iam.sql
del /Q create_iam.sql
echo Completed
