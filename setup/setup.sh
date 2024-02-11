#!/bin/bash
set -e

# SQL Serverの起動待機(荒業)
# 3秒に1度SQLを投げてみて正常実行されるようになるのを待つ
while :
do
  sleep 3
  /opt/mssql-tools/bin/sqlcmd -U $MSSQL_USER -P $MSSQL_PASSWORD -Q "select top 1 name from SYS.DATABASES;" > /dev/null || {
    continue
  }
  break
done

echo === initiate ddl ===
# create database
/opt/mssql-tools/bin/sqlcmd -U $MSSQL_USER -P $MSSQL_PASSWORD -Q "create database discord"
# create ddl
/opt/mssql-tools/bin/sqlcmd -U $MSSQL_USER -P $MSSQL_PASSWORD -i /db/ddl/schema.sql

echo === insert initial data ===
/opt/mssql-tools/bin/sqlcmd -U $MSSQL_USER -P $MSSQL_PASSWORD -i /db/data/data.sql


echo === Done ===