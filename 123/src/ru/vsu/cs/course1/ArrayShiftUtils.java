package ru.vsu.cs.course1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Модуль с логикой задачи сдвига и функциями чтения/записи.
 * Используется без изменений как в консольном, так и в оконном приложении.
 *
 * Задача: циклический сдвиг столбцов или строк двумерного массива на n позиций.
 */
public class ArrayShiftUtils {

    // ======================== Чтение / Запись ========================

    /**
     * Читает двумерный массив из текстового файла.
     * Формат:
     *   Первая строка: ROWS|COLS n   (направление сдвига и величина)
     *   Далее — строки матрицы, элементы через пробел.
     */
    public static ShiftTask readFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String header = br.readLine();
            if (header == null || header.trim().isEmpty())
                throw new IOException("Файл пуст или отсутствует заголовок");

            String[] headerParts = header.trim().split("\\s+");
            if (headerParts.length < 2)
                throw new IOException("Неверный формат заголовка. Ожидается: ROWS|COLS n");

            String direction = headerParts[0].toUpperCase();
            if (!direction.equals("ROWS") && !direction.equals("COLS"))
                throw new IOException("Направление должно быть ROWS или COLS, получено: " + direction);

            int shift;
            try {
                shift = Integer.parseInt(headerParts[1]);
            } catch (NumberFormatException e) {
                throw new IOException("Величина сдвига должна быть целым числом: " + headerParts[1]);
            }

            List<int[]> rows = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                int[] row = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    try {
                        row[i] = Integer.parseInt(parts[i]);
                    } catch (NumberFormatException e) {
                        throw new IOException("Некорректное число: " + parts[i]);
                    }
                }
                rows.add(row);
            }

            if (rows.isEmpty()) throw new IOException("Массив пуст");

            int[][] array = rows.toArray(new int[0][]);
            checkRectangular(array);

            return new ShiftTask(array, direction, shift);
        }
    }

    /**
     * Записывает двумерный массив в текстовый файл.
     */
    public static void writeToFile(String filename, int[][] array) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (int[] row : array) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < row.length; j++) {
                    if (j > 0) sb.append(" ");
                    sb.append(row[j]);
                }
                pw.println(sb.toString());
            }
        }
    }

    // ======================== Проверка прямоугольности ========================

    public static void checkRectangular(int[][] array) throws IOException {
        if (array.length == 0) return;
        int cols = array[0].length;
        for (int i = 1; i < array.length; i++)
            if (array[i].length != cols)
                throw new IOException(
                    "Массив не прямоугольный: строка 0 имеет " + cols +
                    " элементов, строка " + i + " имеет " + array[i].length);
    }

    // ======================== Основная логика ========================

    /**
     * Выполняет циклический сдвиг строк или столбцов.
     *
     * @param array     исходный массив
     * @param direction "ROWS" — сдвиг строк вниз, "COLS" — сдвиг столбцов вправо
     * @param n         величина сдвига (отрицательное — в обратную сторону)
     */
    public static int[][] cyclicShift(int[][] array, String direction, int n) {
        if (array.length == 0 || array[0].length == 0) return copyArray(array);
        return direction.equalsIgnoreCase("ROWS") ? shiftRows(array, n) : shiftCols(array, n);
    }

    static int[][] shiftRows(int[][] array, int n) {
        int rows = array.length;
        int cols = array[0].length;
        int[][] result = new int[rows][cols];
        n = normalizeShift(n, rows);
        for (int i = 0; i < rows; i++)
            result[(i + n) % rows] = copyRow(array[i]);
        return result;
    }

    static int[][] shiftCols(int[][] array, int n) {
        int rows = array.length;
        int cols = array[0].length;
        int[][] result = new int[rows][cols];
        n = normalizeShift(n, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result[i][(j + n) % cols] = array[i][j];
        return result;
    }

    static int normalizeShift(int n, int size) {
        if (size == 0) return 0;
        n = n % size;
        if (n < 0) n += size;
        return n;
    }

    static int[] copyRow(int[] row) {
        int[] copy = new int[row.length];
        System.arraycopy(row, 0, copy, 0, row.length);
        return copy;
    }

    public static int[][] copyArray(int[][] array) {
        int[][] copy = new int[array.length][];
        for (int i = 0; i < array.length; i++) copy[i] = copyRow(array[i]);
        return copy;
    }

    public static String arrayToString(int[][] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (j > 0) sb.append("\t");
                sb.append(array[i][j]);
            }
            if (i < array.length - 1) sb.append("\n");
        }
        return sb.toString();
    }

    // ======================== Вспомогательный класс ========================

    public static class ShiftTask {
        public int[][] array;
        public String direction;
        public int shift;

        public ShiftTask(int[][] array, String direction, int shift) {
            this.array = array;
            this.direction = direction;
            this.shift = shift;
        }
    }
}
