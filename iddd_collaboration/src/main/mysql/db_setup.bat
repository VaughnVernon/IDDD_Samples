@echo off
rem -------------------------------------------
rem IDDD collaboration views database setup
rem -------------------------------------------

echo Creating IDDD Collaboration Event Store and Views database...
mysql -u root -p < collaboration.sql

echo Completed
