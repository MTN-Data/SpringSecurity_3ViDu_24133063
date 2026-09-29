@echo off
cd /d "%~dp0"
call mvnw.cmd clean verify
set "TEST_RESULT=%ERRORLEVEL%"
pause
exit /b %TEST_RESULT%
