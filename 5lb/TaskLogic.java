import java.io.*;
import java.util.*;

/**
 * Общий модуль: логика задачи и операции с файлами.
 * Используется как консольным, так и оконным приложением.
 */
public class TaskLogic {

    // =========================================================
    //  Логика задачи
    // =========================================================

    /**
     * Вспомогательная функция: сравнивает два Integer на равенство.
     */
    private static boolean areEqual(Integer a, Integer b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.intValue() == b.intValue();
    }

    /**
     * Вспомогательная функция: возвращает последний элемент списка,
     * или null если список пуст.
     */
    private static Integer lastElement(List<Integer> list) {
        int size = list.size();
        if (size == 0) return null;
        // Перебираем, чтобы добраться до последнего (без get)
        Integer last = null;
        for (Integer v : list) {
            last = v;
        }
        return last;
    }

    /**
     * Основная функция задачи:
     * Из списка чисел создаёт новый список, в котором
     * подряд идущие одинаковые числа заменены одним вхождением.
     * Пример: {2,11,11,11,2,3,2,2,6,6,2,3,3,3,10} → {2,11,2,3,2,6,2,3,10}
     */
    public static List<Integer> createNewList(List<Integer> list) {
        List<Integer> result = new ArrayList<>();
        for (Integer v : list) {
            if (!areEqual(v, lastElement(result))) {
                result.add(v);
            }
        }
        return result;
    }

    // =========================================================
    //  Чтение / запись файлов (для 1D списка)
    // =========================================================

    /**
     * Читает список целых чисел из файла.
     * Все числа должны быть на одной строке, разделённые пробелами/запятыми.
     */
    public static List<Integer> readListFromFile(String filename) throws IOException {
        List<Integer> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] tokens = line.trim().split("[,\\s]+");
                for (String token : tokens) {
                    token = token.trim();
                    if (!token.isEmpty()) {
                        list.add(Integer.parseInt(token));
                    }
                }
            }
        } finally {
            reader.close();
        }
        return list;
    }

    /**
     * Записывает список целых чисел в файл (числа через пробел на одной строке).
     */
    public static void writeListToFile(String filename, List<Integer> list) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Integer v : list) {
                if (!first) sb.append(" ");
                sb.append(v);
                first = false;
            }
            writer.write(sb.toString());
            writer.newLine();
        } finally {
            writer.close();
        }
    }

    // =========================================================
    //  Чтение / запись файлов (для 2D массива — используется JTable)
    // =========================================================

    /**
     * Читает двумерный массив из файла.
     * Каждая строка файла — строка массива, числа разделены пробелами/запятыми.
     */
    public static int[][] readArrayFromFile(String filename) throws IOException {
        List<int[]> rows = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.trim().split("[,\\s]+");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i].trim());
                }
                rows.add(row);
            }
        } finally {
            reader.close();
        }
        int[][] array = new int[rows.size()][];
        int idx = 0;
        for (int[] row : rows) {
            array[idx++] = row;
        }
        return array;
    }

    /**
     * Записывает двумерный массив в файл.
     * Каждая строка массива — на отдельной строке файла.
     */
    public static void writeArrayToFile(String filename, int[][] array) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            for (int[] row : array) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) sb.append(" ");
                    sb.append(row[i]);
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Конвертирует двумерный массив int[][] в List<Integer> (построчно)
     * для применения логики задачи к каждой строке.
     */
    public static List<Integer> arrayRowToList(int[][] array, int row) {
        List<Integer> list = new ArrayList<>();
        for (int v : array[row]) {
            list.add(v);
        }
        return list;
    }

    /**
     * Применяет createNewList к каждой строке двумерного массива.
     * Возвращает массив обработанных строк (строки могут иметь разную длину).
     */
    public static List<List<Integer>> processArray(int[][] array) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] row : array) {
            List<Integer> rowList = new ArrayList<>();
            for (int v : row) rowList.add(v);
            result.add(createNewList(rowList));
        }
        return result;
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
     * Поддерживает форматы:
     *   java ConsoleApp input.txt output.txt
     *   java ConsoleApp -i input.txt -o output.txt
     *   java ConsoleApp --input-file=input.txt --output-file=output.txt
     */
    public static InputArgs parseCmdArgs(String[] args) {
        InputArgs result = new InputArgs();
        if (args == null || args.length == 0) return result;

        // Позиционные (без флагов)
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

        // Если именованные не заполнены, берём позиционные
        if (result.inputFile == null && positional.size() >= 1) {
            result.inputFile = positional.get(0);
        }
        if (result.outputFile == null && positional.size() >= 2) {
            result.outputFile = positional.get(1);
        }

        return result;
    }

    // =========================================================
    //  Утилиты форматирования
    // =========================================================

    /** Преобразует список в строку вида {a, b, c} */
    public static String listToString(List<Integer> list) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Integer v : list) {
            if (!first) sb.append(", ");
            sb.append(v);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
