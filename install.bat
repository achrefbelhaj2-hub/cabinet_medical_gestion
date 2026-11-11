@echo off
chcp 65001 >nul
echo ========================================
echo    INSTALLATION CABINET MEDICAL
echo ========================================
echo.

REM Vérifier Java
echo [1/4] Vérification de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo   ERREUR: Java n'est pas installé!
    echo   Téléchargez Java 11+ depuis: https://adoptium.net/
    echo.
    pause
    exit /b 1
)
echo   ✓ Java est installé

echo.
echo [2/4] Vérification de MySQL...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo   ! MySQL n'est pas installé
    echo   ! L'application fonctionnera sans base de données
    goto :skip_db
)

echo.
echo [3/4] Création de la base de données...
if exist "database\cabinet_medical.sql" (
    echo   Base trouvée: database\cabinet_medical.sql
    echo   Entrez votre mot de passe MySQL (tapez Entrée si vide):
    mysql -u root -p < "database\cabinet_medical.sql"
    if errorlevel 1 (
        echo   ! Impossible de créer la base
        echo   ! Vous pouvez l'ignorer pour le moment
    ) else (
        echo   ✓ Base de données créée!
    )
) else (
    echo   ! Fichier SQL non trouvé
)

:skip_db
echo.
echo [4/4] Librairies nécessaires:
echo.
echo   Créez un dossier "lib" et copiez:
echo.
echo   1. mysql-connector-java-8.0.33.jar
echo      Source: https://dev.mysql.com/downloads/connector/j/
echo.
echo   2. Tous les .jar de javafx-sdk-21\lib\
echo      Source: https://gluonhq.com/products/javafx/
echo.
echo   Structure finale:
echo   votre_projet/
echo   ├── lib/
echo   │   ├── mysql-connector-java-8.0.33.jar
echo   │   ├── javafx.base.jar
echo   │   └── ...
echo   └── ...

echo.
echo ========================================
echo    INSTALLATION TERMINÉE
echo ========================================
echo.
echo Pour lancer: double-cliquez sur execute.bat
pause