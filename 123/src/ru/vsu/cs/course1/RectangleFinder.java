package ru.vsu.cs.course1;

/**
 * Утилита для поиска наибольшего прямоугольника из истинных значений,
 * окружённого ложными значениями или границами массива.
 *
 * «Окружён» означает: все клетки, смежные с прямоугольником
 * (включая угловые диагональные соседи), содержат false или являются
 * границей массива. То есть прямоугольник из 1-единиц не должен
 * касаться других единиц ни по сторонам, ни по углам.
 *
 * Результат: int[4] = { row, col, height, width }
 * Если прямоугольник не найден — { -1, -1, -1, -1 }.
 *
 * При нескольких прямоугольниках одинаковой максимальной площади
 * выбирается самый верхний (наименьший row), затем самый левый (наименьший col).
 */
public class RectangleFinder {

    public static final int[] NOT_FOUND = {-1, -1, -1, -1};

    /**
     * Ищет наибольший по площади прямоугольник из true-значений,
     * окружённый false-значениями или границами массива (включая диагонали).
     *
     * @param grid двумерный булев массив (не изменяется)
     * @return { row, col, height, width } или { -1, -1, -1, -1 }
     */
    public static int[] findLargestSurroundedRectangle(boolean[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0)
            return NOT_FOUND.clone();

        int rows = grid.length;
        int cols = grid[0].length;

        // 2D prefix sum: prefix[i][j] = кол-во true в [0..i-1][0..j-1]
        int[][] prefix = new int[rows + 1][cols + 1];
        for (int i = 1; i <= rows; i++)
            for (int j = 1; j <= cols; j++)
                prefix[i][j] = (grid[i-1][j-1] ? 1 : 0)
                        + prefix[i-1][j] + prefix[i][j-1] - prefix[i-1][j-1];

        int bestArea = 0;
        int bestRow = -1, bestCol = -1, bestH = -1, bestW = -1;

        for (int top = 0; top < rows; top++) {
            for (int bottom = top; bottom < rows; bottom++) {
                int h = bottom - top + 1;

                for (int left = 0; left < cols; left++) {
                    for (int right = left; right < cols; right++) {
                        int w = right - left + 1;
                        int area = h * w;

                        if (area <= bestArea) continue;

                        // Условие 1: все клетки внутри прямоугольника = true
                        int sumInside = prefix[bottom+1][right+1]
                                - prefix[top][right+1]
                                - prefix[bottom+1][left]
                                + prefix[top][left];
                        if (sumInside != area) continue;

                        // Условие 2: весь периметр снаружи (включая угловые диагонали) = false
                        if (!isSurrounded(grid, top, left, h, w, rows, cols)) continue;

                        bestArea = area;
                        bestRow  = top;
                        bestCol  = left;
                        bestH    = h;
                        bestW    = w;
                    }
                }
            }
        }

        if (bestArea == 0) return NOT_FOUND.clone();
        return new int[]{bestRow, bestCol, bestH, bestW};
    }

    /**
     * Проверяет, что прямоугольник [top, left] размером [h x w]
     * окружён false или границей со всех сторон, ВКЛЮЧАЯ угловые диагонали.
     *
     * Проверяемая зона — рамка вокруг прямоугольника толщиной 1:
     *   строка (top-1):    столбцы [left-1 .. right+1]   ← включает углы
     *   строка (bottom+1): столбцы [left-1 .. right+1]   ← включает углы
     *   столбец (left-1):  строки  [top .. bottom]        ← боковые (без углов)
     *   столбец (right+1): строки  [top .. bottom]        ← боковые (без углов)
     */
    private static boolean isSurrounded(
            boolean[][] grid, int top, int left, int h, int w,
            int rows, int cols) {

        int bottom = top + h - 1;
        int right  = left + w - 1;

        // Строка сверху — включая угловые клетки (left-1) и (right+1)
        if (top > 0) {
            int jStart = Math.max(0, left - 1);
            int jEnd   = Math.min(cols - 1, right + 1);
            for (int j = jStart; j <= jEnd; j++)
                if (grid[top - 1][j]) return false;
        }

        // Строка снизу — включая угловые клетки
        if (bottom < rows - 1) {
            int jStart = Math.max(0, left - 1);
            int jEnd   = Math.min(cols - 1, right + 1);
            for (int j = jStart; j <= jEnd; j++)
                if (grid[bottom + 1][j]) return false;
        }

        // Столбец слева — только боковые строки (углы уже проверены выше)
        if (left > 0) {
            for (int i = top; i <= bottom; i++)
                if (grid[i][left - 1]) return false;
        }

        // Столбец справа — только боковые строки
        if (right < cols - 1) {
            for (int i = top; i <= bottom; i++)
                if (grid[i][right + 1]) return false;
        }

        return true;
    }

    /**
     * Выводит булев массив в виде строки (1 = true, 0 = false).
     */
    public static String gridToString(boolean[][] grid) {
        if (grid == null || grid.length == 0) return "(пустой массив)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (j > 0) sb.append(" ");
                sb.append(grid[i][j] ? "1" : "0");
            }
            if (i < grid.length - 1) sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Конвертирует int[][] в boolean[][] (0 = false, иное = true).
     */
    public static boolean[][] fromIntArray(int[][] array) {
        if (array == null) return new boolean[0][0];
        boolean[][] grid = new boolean[array.length][];
        for (int i = 0; i < array.length; i++) {
            grid[i] = new boolean[array[i].length];
            for (int j = 0; j < array[i].length; j++)
                grid[i][j] = (array[i][j] != 0);
        }
        return grid;
    }
}