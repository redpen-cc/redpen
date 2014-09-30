@echo off

SETLOCAL

if NOT DEFINED JAVA_HOME goto err

set SCRIPT_DIR=%~dp0

for %%I in ("%SCRIPT_DIR%..") do set REDPEN_HOME=%%~dpfI

set REDPEN_CLASSPATH=%REDPEN_HOME%/conf;%REDPEN_HOME%/lib/*
set JAVA_OPTS=%JAVA_OPTS%

"%JAVA_HOME%\bin\java" %JAVA_OPTS% -classpath "%REDPEN_CLASSPATH%" cc.redpen.Main %*
goto finally

:err
echo Error: JAVA_HOME is not defined. Can not start RedPen
pause

:finally
ENDLOCAL
