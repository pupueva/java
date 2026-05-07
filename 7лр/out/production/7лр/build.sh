#!/bin/bash
# === Сборка исполняемого JAR-файла ===
# Требуется JDK 11+ (javac, jar)

set -e

echo "Компиляция..."
javac -encoding UTF-8 MainApp.java

echo "Создание JAR..."
jar cfe MainApp.jar MainApp *.class

echo "Очистка .class файлов..."
rm -f *.class

echo ""
echo "============================================"
echo "  Готово! Запуск: java -jar MainApp.jar"
echo "============================================"
