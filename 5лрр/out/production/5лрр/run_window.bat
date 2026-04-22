@echo off
if not exist TaskLogic.class (
    echo Компиляция...
    javac TaskLogic.java ConsoleApp.java WindowApp.java
    if errorlevel 1 (
        echo Ошибка компиляции. Убедитесь что установлена Java JDK.
        pause
        exit /b 1
    )
)
java WindowApp
