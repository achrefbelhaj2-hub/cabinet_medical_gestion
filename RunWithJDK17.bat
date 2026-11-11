@echo off
cd /d "C:\Users\user\Documents\NetBeansProjects\Cabinet_Medical"

echo =========================================
echo   FORCAGE JDK 17 POUR CABINET MEDICAL
echo =========================================

REM 1. Force JDK 17
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Version Java utilisee :
java -version

echo.
echo 2. Nettoyage et compilation...
call mvn clean compile -q

echo.
echo 3. Lancement de l'application Cabinet Medical...
echo =========================================

java --module-path "C:\Users\user\Desktop\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp "target\classes;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2.jar" com.cabinet.app.MainApp

pause