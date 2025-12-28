@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
echo JAVA_HOME is set to: %JAVA_HOME%
cd /d "%~dp0"
gradlew clean build --refresh-dependencies
pause