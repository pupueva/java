package ru.vsu.cs.course1;

/**
 * Утилита для поиска наибольшего прямоугольника из истинных значений,
 * окружённого ложными значениями или границами массива.
 *
 * Прямоугольник считается «окружённым», если каждая клетка непосредственно
 * снаружи его границ (сверху, снизу, слева, справа) либо является границей
 * массива, либо содержит false.
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
     * окружённый false-значениями или границами массива.
     *
     * @param grid двумерный булев массив (не изменяется)
     * @return { row, col, height, width } или { -1, -1, -1, -1 }
     */
    public static int[] findLargestSurroundedRectangle(boolean[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0)
            return NOT_FOUND.clone();

        int rows = grid.length;
        int cols = grid[0].length;

        int bestArea = 0;
        int bestRow = -1, bestCol = -1, bestH = -1, bestW = -1;

        for (int r = 0; r < rows; r++) {
            // heights[j] — высота непрерывного столбца из true, заканчивающегося в строке r
            int[] heights = new int[cols];
            for (int j = 0; j < cols; j++) {
                if (grid[r][j]) {
                    int h = 0;
                    for (int i = r; i >= 0 && grid[i][j]; i--) h++;
                    heights[j] = h;
                }
            }

            for (int h = 1; h <= r + 1; h++) {
                int topRow = r - h + 1;
                int j = 0;
                while (j < cols) {
                    if (heights[j] < h) { j++; continue; }

                    int startCol = j;
                    while (j < cols && heights[j] >= h) j++;
                    int endCol = j - 1;
                    int w = endCol - startCol + 1;
                    int area = h * w;

                    if (isSurrounded(grid, topRow, startCol, h, w, rows, cols)) {
                        if (area > bestArea ||
                            (area == bestArea && (topRow < bestRow ||
                                (topRow == bestRow && startCol < bestCol)))) {
                            bestArea = area;
                            bestRow  = topRow;
                            bestCol  = startCol;
                            bestH    = h;
                            bestW    = w;
                        }
                    }
                }
            }
        }

        if (bestArea == 0) return NOT_FOUND.clone();
        return new int[]{bestRow, bestCol, bestH, bestW};
    }

    /**
     * Проверяет, что прямоугольник [topRow, startCol] x [h, w]
     * со всех сторон окружён false или границей массива.
     */
    private static boolean isSurrounded(
            boolean[][] grid, int topRow, int startCol, int h, int w,
            int rows, int cols) {

        int bottomRow = topRow + h - 1;
        int endCol    = startCol + w - 1;

        // Строка сверху
        if (topRow > 0)
            for (int j = startCol; j <= endCol; j++)
                if (grid[topRow - 1][j]) return false;

        // Строка снизу
        if (bottomRow < rows - 1)
            for (int j = startCol; j <= endCol; j++)
                if (grid[bottomRow + 1][j]) return false;

        // Столбец слева
        if (startCol > 0)
            for (int i = topRow; i <= bottomRow; i++)
                if (grid[i][startCol - 1]) return false;

        // Столбец справа
        if (endCol < cols - 1)
            for (int i = topRow; i <= bottomRow; i++)
                if (grid[i][endCol + 1]) return false;

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
     * Удобно для использования совместно с ArrayShiftUtils.
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
