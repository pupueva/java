package ru.vsu.cs.course1;

import java.io.File;
import java.util.Scanner;

/**
 * Консольное приложение:
 *   — Режим 1: циклический сдвиг строк/столбцов двумерного int-массива
 *   — Режим 2: поиск наибольшего окружённого прямоугольника в булевом массиве
 *
 * Два способа запуска:
 *   1) С аргументами командной строки (чтение из файла):
 *      java ru.vsu.cs.course1.Task8 -i input.txt -o output.txt
 *   2) Без аргументов — интерактивный режим (выбор задачи в меню).
 */
public class Task8 {

    private static final String USAGE =
            "Использование с файлами:\n" +
            "  java ru.vsu.cs.course1.Task8 <input> <output>\n" +
            "  java ru.vsu.cs.course1.Task8 -i <input> -o <output>\n" +
            "  java ru.vsu.cs.course1.Task8 --input-file=<input> --output-file=<output>\n\n" +
            "Или запустите без аргументов для интерактивного ввода.";

    // ======================== Разбор аргументов ========================

    public static InputArgs parseCmdArgs(String[] args) {
        String inputFile = null;
        String outputFile = null;

        int i = 0;
        while (i < args.length) {
            String arg = args[i];
            if (arg.startsWith("--input-file=")) {
                inputFile = arg.substring("--input-file=".length());
            } else if (arg.startsWith("--output-file=")) {
                outputFile = arg.substring("--output-file=".length());
            } else if (arg.equals("-i") && i + 1 < args.length) {
                inputFile = args[++i];
            } else if (arg.equals("-o") && i + 1 < args.length) {
                outputFile = args[++i];
            } else if (!arg.startsWith("-")) {
                if (inputFile == null) inputFile = arg;
                else if (outputFile == null) outputFile = arg;
            } else {
                System.err.println("Неизвестный параметр: " + arg);
                return null;
            }
            i++;
        }

        if (inputFile == null || outputFile == null) return null;
        return new InputArgs(inputFile, outputFile);
    }


    private static void runInteractive(Scanner scanner) {
        System.out.println(" Лабораторная работа: двумерные массивы ");
        System.out.println();
        System.out.println("Выберите задачу:");
        System.out.println("  1 — Циклический сдвиг строк/столбцов int-массива");
        System.out.println("  2 — Поиск наибольшего окружённого прямоугольника (булев массив)");
        System.out.print("Ваш выбор (1/2): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> runShiftInteractive(scanner);
            case "2" -> runRectangleInteractive(scanner);
            default -> {
                System.err.println("Ошибка: введите 1 или 2");
                System.exit(1);
            }
        }
    }

    // ─── Задача 1: сдвиг ────────────────────────────────────────────────

    private static void runShiftInteractive(Scanner scanner) {
        System.out.println();
        System.out.println("=== Циклический сдвиг строк/столбцов двумерного массива ===");
        System.out.println();

        System.out.print("Введите количество строк: ");
        int rows = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Введите количество столбцов: ");
        int cols = Integer.parseInt(scanner.nextLine().trim());

        int[][] array = new int[rows][cols];
        System.out.println("Введите элементы матрицы (по " + cols + " чисел в строке через пробел):");
        for (int i = 0; i < rows; i++) {
            System.out.print("  Строка " + (i + 1) + ": ");
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length != cols) {
                System.err.println("Ошибка: ожидалось " + cols + " элементов, получено " + parts.length);
                System.exit(1);
            }
            for (int j = 0; j < cols; j++)
                array[i][j] = Integer.parseInt(parts[j]);
        }

        System.out.print("Сдвигать строки или столбцы? (ROWS / COLS): ");
        String direction = scanner.nextLine().trim().toUpperCase();
        if (!direction.equals("ROWS") && !direction.equals("COLS")) {
            System.err.println("Ошибка: введите ROWS или COLS");
            System.exit(1);
        }

        System.out.print("На сколько позиций сдвинуть (целое число, можно отрицательное): ");
        int shift = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("\nИсходный массив:");
        System.out.println(ArrayShiftUtils.arrayToString(array));

        int[][] result = ArrayShiftUtils.cyclicShift(array, direction, shift);

        System.out.println("\nРезультат (" + direction + ", сдвиг на " + shift + "):");
        System.out.println(ArrayShiftUtils.arrayToString(result));

        System.out.println();
        System.out.print("Сохранить результат в файл? (да/нет): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("да") || answer.equals("yes") || answer.equals("y") || answer.equals("д")) {
            System.out.print("Введите имя файла (например output.txt): ");
            String filename = scanner.nextLine().trim();
            try {
                ArrayShiftUtils.writeToFile(filename, result);
                System.out.println("Результат сохранён в файл: " + new File(filename).getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Ошибка при сохранении: " + e.getMessage());
            }
        }

        System.out.println("\nГотово!");
    }


    private static void runRectangleInteractive(Scanner scanner) {
        System.out.println();
        System.out.println("=== Поиск наибольшего окружённого прямоугольника ===");
        System.out.println("Прямоугольник должен состоять из истинных (1) значений");
        System.out.println("и со всех сторон граничить с ложными (0) значениями или краем массива.");
        System.out.println();

        System.out.print("Введите количество строк: ");
        int rows = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Введите количество столбцов: ");
        int cols = Integer.parseInt(scanner.nextLine().trim());

        boolean[][] grid = new boolean[rows][cols];
        System.out.println("Введите строки матрицы (по " + cols + " значений через пробел, 0=false, 1=true):");
        for (int i = 0; i < rows; i++) {
            System.out.print("  Строка " + (i + 1) + ": ");
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length != cols) {
                System.err.println("Ошибка: ожидалось " + cols + " элементов, получено " + parts.length);
                System.exit(1);
            }
            for (int j = 0; j < cols; j++) {
                int val = Integer.parseInt(parts[j]);
                if (val != 0 && val != 1) {
                    System.err.println("Ошибка: допустимы только 0 и 1");
                    System.exit(1);
                }
                grid[i][j] = (val == 1);
            }
        }

        System.out.println("\nВведённый массив:");
        System.out.println(RectangleFinder.gridToString(grid));

        int[] result = RectangleFinder.findLargestSurroundedRectangle(grid);

        System.out.println();
        if (result[0] == -1) {
            System.out.println("Результат: прямоугольник не найден — (-1, -1, -1, -1)");
        } else {
            System.out.printf("Результат: строка=%d, столбец=%d, высота=%d, ширина=%d%n",
                    result[0], result[1], result[2], result[3]);
            System.out.printf("Площадь: %d%n", result[2] * result[3]);
            System.out.println("\nМассив с выделенным прямоугольником (* — найденные клетки):");
            System.out.println(gridToStringWithRect(grid, result[0], result[1], result[2], result[3]));
        }

        System.out.println("\nГотово!");
    }

    /**
     * Визуализирует булев массив с выделенным прямоугольником символом '*'.
     */
    private static String gridToStringWithRect(boolean[][] grid, int rr, int rc, int rh, int rw) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (j > 0) sb.append(" ");
                boolean inRect = (i >= rr && i < rr + rh && j >= rc && j < rc + rw);
                if (inRect) sb.append("*");
                else sb.append(grid[i][j] ? "1" : "0");
            }
            if (i < grid.length - 1) sb.append("\n");
        }
        return sb.toString();
    }

    // ======================== Файловый режим ========================

    private static void runWithFiles(InputArgs parsedArgs) {
        File inputFile = new File(parsedArgs.inputFile);
        if (!inputFile.exists()) {
            System.err.println("Ошибка: входной файл не найден: " + parsedArgs.inputFile);
            System.exit(2);
        }

        try {
            ArrayShiftUtils.ShiftTask task = ArrayShiftUtils.readFromFile(parsedArgs.inputFile);

            System.out.println("Исходный массив:");
            System.out.println(ArrayShiftUtils.arrayToString(task.array));
            System.out.println("Направление: " + task.direction + ", сдвиг: " + task.shift);

            int[][] result = ArrayShiftUtils.cyclicShift(task.array, task.direction, task.shift);

            System.out.println("\nРезультат:");
            System.out.println(ArrayShiftUtils.arrayToString(result));

            ArrayShiftUtils.writeToFile(parsedArgs.outputFile, result);
            System.out.println("\nРезультат записан в файл: " + parsedArgs.outputFile);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(3);
        }
    }

    // ======================== main ========================

    public static void main(String[] args) {
        InputArgs parsedArgs = parseCmdArgs(args);

        if (parsedArgs != null) {
            runWithFiles(parsedArgs);
        } else {
            Scanner scanner = new Scanner(System.in);
            runInteractive(scanner);
        }
    }
}
