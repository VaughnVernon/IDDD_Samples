#!/bin/bash
# -------------------------------------------
# IDDD collaboration views database setup
# -------------------------------------------

echo Creating IDDD Collaboration Event Store and Views database...
mysql -u root -p < collaboration.sql

echo Completed
