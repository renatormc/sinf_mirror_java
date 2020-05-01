@echo off
mvn package && java -jar .\target\sinf-mirror\sinf-mirror.jar %*