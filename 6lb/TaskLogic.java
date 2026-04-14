import java.io.*;
import java.util.*;

/**
 * Общий модуль: логика задачи и операции с файлами.
 * Используется ConsoleApp и WindowApp без изменений.
 *
 * Задача: для набора окружностей найти три, для которых периметр
 * треугольника с вершинами в центрах этих окружностей минимален.
 */
public class TaskLogic {

    // =========================================================
    //  Вспомогательные математические функции
    // =========================================================

    /** Евклидово расстояние между центрами двух окружностей. */
    public static double distance(Circle a, Circle b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Периметр треугольника по трём вершинам (центрам окружностей). */
    public static double trianglePerimeter(Circle a, Circle b, Circle c) {
        return distance(a, b) + distance(b, c) + distance(a, c);
    }

    /** Минимум из двух чисел (вместо Math.min). */
    private static double min(double x, double y) {
        return x < y ? x : y;
    }

    // =========================================================
    //  Основная логика задачи
    // =========================================================

    /**
     * Находит тройку окружностей из списка, для которых периметр
     * треугольника с вершинами в центрах минимален.
     *
     * @param circles список окружностей (не менее 3)
     * @return CircleTriple с минимальным периметром, или null если < 3 окружностей
     */
    public static CircleTriple findMinPerimeterTriple(List<Circle> circles) {
        if (circles == null || circles.size() < 3) return null;

        CircleTriple best = null;
        double bestPerimeter = Double.MAX_VALUE;

        int n = circles.size();
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    Circle a = circles.get(i);
                    Circle b = circles.get(j);
                    Circle c = circles.get(k);
                    double p = trianglePerimeter(a, b, c);
                    if (p < bestPerimeter) {
                        bestPerimeter = p;
                        best = new CircleTriple(a, b, c, p);
                    }
                }
            }
        }
        return best;
    }

    // =========================================================
    //  Чтение / запись файлов
    // =========================================================

    /**
     * Читает список окружностей из файла.
     * Формат: каждая строка — "x y r"
     */
    public static List<Circle> readCirclesFromFile(String filename) throws IOException {
        List<Circle> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    list.add(Circle.fromFileLine(line));
                } catch (Exception e) {
                    throw new IOException("Ошибка в строке " + lineNum + ": " + e.getMessage());
                }
            }
        } finally {
            reader.close();
        }
        return list;
    }

    /**
     * Записывает список окружностей в файл.
     * Формат: каждая строка — "x y r"
     */
    public static void writeCirclesToFile(String filename, List<Circle> circles) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            for (Circle c : circles) {
                writer.write(c.toFileLine());
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Записывает результат (тройку + периметр) в выходной файл.
     */
    public static void writeResultToFile(String filename, CircleTriple result) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            if (result == null) {
                writer.write("Недостаточно окружностей для формирования тройки (нужно >= 3).");
            } else {
                writer.write("Тройка окружностей с минимальным периметром треугольника центров:");
                writer.newLine();
                writer.write("  " + result.a.toFileLine() + "   # x y r");
                writer.newLine();
                writer.write("  " + result.b.toFileLine() + "   # x y r");
                writer.newLine();
                writer.write("  " + result.c.toFileLine() + "   # x y r");
                writer.newLine();
                writer.write(String.format("Периметр = %.6f", result.perimeter));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    // =========================================================
    //  Конвертация список ↔ двумерный массив (для JTable)
    // =========================================================

    /** Преобразует список окружностей в двумерный массив строк для JTable. */
    public static String[][] circlesToTable(List<Circle> circles) {
        String[][] data = new String[circles.size()][3];
        int i = 0;
        for (Circle c : circles) {
            data[i][0] = String.valueOf(c.x);
            data[i][1] = String.valueOf(c.y);
            data[i][2] = String.valueOf(c.r);
            i++;
        }
        return data;
    }

    /** Преобразует двумерный массив строк из JTable в список окружностей. */
    public static List<Circle> tableToCircles(String[][] data) {
        List<Circle> list = new ArrayList<>();
        for (String[] row : data) {
            if (row[0] == null || row[0].trim().isEmpty()) continue;
            try {
                double x = Double.parseDouble(row[0].trim());
                double y = Double.parseDouble(row[1].trim());
                double r = Double.parseDouble(row[2].trim());
                list.add(new Circle(x, y, r));
            } catch (Exception ignored) {
                // Пропускаем незаполненные / некорректные строки
            }
        }
        return list;
    }

    // =========================================================
    //  Разбор аргументов командной строки
    // =========================================================

    public static class InputArgs {
        public String inputFile;
        public String outputFile;
    }

    /**
     * Разбирает аргументы командной строки.
     * Форматы:
     *   program input.txt output.txt
     *   program -i input.txt -o output.txt
     *   program --input-file=input.txt --output-file=output.txt
     */
    public static InputArgs parseCmdArgs(String[] args) {
        InputArgs result = new InputArgs();
        if (args == null || args.length == 0) return result;

        List<String> positional = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--input-file=")) {
                result.inputFile = arg.substring("--input-file=".length());
            } else if (arg.startsWith("--output-file=")) {
                result.outputFile = arg.substring("--output-file=".length());
            } else if (arg.equals("-i") && i + 1 < args.length) {
                result.inputFile = args[++i];
            } else if (arg.equals("-o") && i + 1 < args.length) {
                result.outputFile = args[++i];
            } else if (!arg.startsWith("-")) {
                positional.add(arg);
            }
        }
        if (result.inputFile == null && positional.size() >= 1) result.inputFile  = positional.get(0);
        if (result.outputFile == null && positional.size() >= 2) result.outputFile = positional.get(1);
        return result;
    }
}
