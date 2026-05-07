@echo off
REM === Сборка исполняемого JAR-файла ===
REM Требуется JDK 11+ (javac, jar)

echo Компиляция...
javac -encoding UTF-8 MainApp.java
if errorlevel 1 (
    echo ОШИБКА компиляции!
    pause
    exit /b 1
)

echo Создание JAR...
jar cfe MainApp.jar MainApp *.class
if errorlevel 1 (
    echo ОШИБКА создания JAR!
    pause
    exit /b 1
)

echo Очистка .class файлов...
del *.class

echo.
echo ============================================
echo   Готово! Запуск: java -jar MainApp.jar
echo   Или двойной клик по MainApp.jar
echo ============================================
pause
