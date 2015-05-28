#!/bin/bash
# -------------------------------------------
# IDDD IdentityAccess database setup
# -------------------------------------------

echo Creating IDDD IdentityAccess database...
cat iam.sql > create_iam.sql
cat ../../../../iddd_common/src/main/mysql/common.sql >> create_iam.sql
mysql -u root -p < create_iam.sql
rm -f create_iam.sql

echo Completed
