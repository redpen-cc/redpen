@echo off
rem
rem RedPen Server startup script
rem

rem Find RedPen home
set REDPEN_HOME=%~dp0\..

rem Make REDPEN_HOME absolute
pushd %REDPEN_HOME%
set REDPEN_HOME=%CD%
popd


rem
rem
rem Configurations
rem
rem

rem Port number for RedPen server
set REDPEN_PORT=8080

rem RedPen log file
set REDPEN_LOG_DIR=%REDPEN_HOME%\logs
set REDPEN_LOG_FILENAME=redpen.log

rem RedPen configuration file
rem NOTE: If you want to specify RedPen configuration file, please uncomment out the following line.
rem NOTE: the configuration file need to be under REDPEN_HOME.
rem set REDPEN_CONF_FILE=%REDPEN_HOME%\conf\redpen-conf-en.xml

rem
rem
rem Main procedure
rem
rem

rem set command
set COMMAND=start
if not [%1] == [] (
    set COMMAND=%1
)

set JAVA_OPTS=%JAVAOPTS% -Dfile.encoding=UTF-8
set REDPEN_WAR_FILE=%REDPEN_HOME%\bin\redpen-server.war
set JAVA_CMD=java.exe

set JAVA_CMD_AT=
@for %%i in (%JAVA_CMD%) do @if NOT "%%~$PATH:i"=="" set JAVA_CMD_AT="%%~$PATH"
if ["%JAVA_CMD_AT%"] == [] (
    if ["%JAVA_HOME%"] == [] (
        echo Error: JAVA_HOME is not defined. Can not start RedPen
        exit /b 1
    ) else (
        set JAVA_CMD="%JAVA_HOME%\bin\java.exe"
        rem Assuming java.exe in its HOME is always executable under the Windows
    )
)

if not exist "%REDPEN_LOG_DIR%" (
    echo not found log dir: %REDPEN_LOG_DIR%
    echo creating log dir: %REDPEN_LOG_DIR%
    mkdir "%REDPEN_LOG_DIR%" 2>nul
    if exist "%REDPEN_LOG_DIR%" (
        echo created log dir: %REDPEN_LOG_DIR%
    ) else (
        if not ["%TEMP%"] == [] (
            set REDPEN_LOG_DIR="%TEMP%"
            echo temporarily changed log dir: %REDPEN_LOG_DIR%
        ) else (
            echo Error: TEMP is not defined.  Can not start RedPen
            exit /b 1
        )
    )
)
set REDPEN_LOG_FILE=%REDPEN_LOG_DIR%\%REDPEN_LOG_FILENAME%

if ["%COMMAND%"] == ["start"] (
    echo starting RedPen server [Ctrl-C to stop]...
    if ["%REDPEN_CONF_FILE%"] == [] (
        echo starting RedPen server without specified configuration file...
        "%JAVA_CMD%" -jar %JAVA_OPTS% "%REDPEN_WAR_FILE%" -p %REDPEN_PORT% >> "%REDPEN_LOG_FILE%"
    ) else (
        echo starting RedPen server specifying a configuration file %REDPEN_CONF_FILE% ...
        "%JAVA_CMD%" -jar %JAVA_OPTS% "%REDPEN_WAR_FILE%" -p %REDPEN_PORT% -c %REDPEN_CONF_FILE% >> "%REDPEN_LOG_FILE%"
    )
) else (
    echo Invalid command: %COMMAND%
    exit /b 1
)
