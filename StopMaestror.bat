@echo off
REM Script d'arret de l'application Maestror

color 0C
echo.
echo ========================================
echo   ARRET DE MAESTROR
echo ========================================
echo.

cd /d "%~dp0"

echo Arret des conteneurs...
docker-compose down

echo.
echo ========================================
echo   APPLICATION ARRETEE
echo ========================================
echo.
pause

exit /b 0
