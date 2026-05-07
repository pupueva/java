@echo off
chcp 65001 >nul 2>&1
echo Запуск MainApp...
java -Dfile.encoding=UTF-8 -jar MainApp.jar
if errorlevel 1 (
    echo.
    echo ОШИБКА: Убедитесь, что установлена Java 11+
    echo Скачать: https://adoptium.net/
    pause
)
