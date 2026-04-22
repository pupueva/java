#!/bin/bash
if [ ! -f TaskLogic.class ]; then
    echo "Компиляция..."
    javac TaskLogic.java ConsoleApp.java WindowApp.java
    if [ $? -ne 0 ]; then
        echo "Ошибка компиляции. Убедитесь что установлена Java JDK."
        exit 1
    fi
fi
java WindowApp
