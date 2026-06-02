@echo off
setlocal
cd /d "%~dp0"
mvn -DskipTests compile exec:java
