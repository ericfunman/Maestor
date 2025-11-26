@echo off
REM Script de demarrage de l'application Maestror avec Docker Compose
REM Double-cliquez pour lancer l'application

color 0A
echo.
echo ========================================
echo   DEMARRAGE DE MAESTROR
echo ========================================
echo.

REM Verifier que Docker est installe
docker --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo ERREUR: Docker n'est pas installe ou n'est pas dans le PATH
    echo Veuillez installer Docker Desktop
    pause
    exit /b 1
)

REM Aller dans le repertoire du projet
cd /d "%~dp0"

echo Demarrage des conteneurs...
echo.

REM Lancer docker-compose
docker-compose up -d

if errorlevel 1 (
    color 0C
    echo.
    echo ERREUR lors du demarrage des conteneurs
    pause
    exit /b 1
)

color 0B
echo.
echo ========================================
echo   APPLICATION DEMARREE AVEC SUCCES !
echo ========================================
echo.
echo Services disponibles:
echo  - Frontend:  http://localhost:4200
echo  - Backend:   http://localhost:8080
echo  - Database:  localhost:5432
echo  - Adminer:   http://localhost:8081
echo.
echo Appuyez sur une touche pour continuer...
pause

REM Ouvrir automatiquement le navigateur
start http://localhost:4200

exit /b 0
