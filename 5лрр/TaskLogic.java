import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskLogic {

    public static List<Integer> removeDuplicates(List<Integer> list) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            if (result.isEmpty() || !result.get(result.size() - 1).equals(list.get(i))) {
                result.add(list.get(i));
            }
        }
        return result;
    }

    public static List<Integer> createNewList(List<Integer> list, int n) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<Integer>();
        }
        int size = list.size();
        n = n % size;
        if (n < 0) {
            n += size;
        }
        List<Integer> result = new ArrayList<Integer>();
        for (int i = size - n; i < size; i++) {
            result.add(list.get(i));
        }
        for (int i = 0; i < size - n; i++) {
            result.add(list.get(i));
        }
        return result;
    }

    public static List<Integer> readListFromFile(String filename) throws IOException {
        List<Integer> list = new ArrayList<Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        reader.close();
        if (line != null && !line.trim().isEmpty()) {
            String[] tokens = line.trim().split("[,\\s]+");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                if (!token.isEmpty()) {
                    list.add(Integer.parseInt(token));
                }
            }
        }
        return list;
    }

    public static void writeListToFile(String filename, List<Integer> list) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(list.get(i));
        }
        writer.write(sb.toString());
        writer.newLine();
        writer.close();
    }

    public static int[][] readArrayFromFile(String filename) throws IOException {
        List<int[]> rows = new ArrayList<int[]>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] tokens = line.split("[,\\s]+");
            int[] row = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                row[i] = Integer.parseInt(tokens[i].trim());
            }
            rows.add(row);
        }
        reader.close();
        int[][] array = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            array[i] = rows.get(i);
        }
        return array;
    }

    public static void writeArrayToFile(String filename, int[][] array) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (int r = 0; r < array.length; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < array[r].length; c++) {
                if (c > 0) {
                    sb.append(" ");
                }
                sb.append(array[r][c]);
            }
            writer.write(sb.toString());
            writer.newLine();
        }
        writer.close();
    }

    public static List<List<Integer>> processArrayRemoveDuplicates(int[][] array) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (int r = 0; r < array.length; r++) {
            List<Integer> rowList = new ArrayList<Integer>();
            for (int c = 0; c < array[r].length; c++) {
                rowList.add(array[r][c]);
            }
            result.add(removeDuplicates(rowList));
        }
        return result;
    }

    public static List<List<Integer>> processArrayShift(int[][] array, int n) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (int r = 0; r < array.length; r++) {
            List<Integer> rowList = new ArrayList<Integer>();
            for (int c = 0; c < array[r].length; c++) {
                rowList.add(array[r][c]);
            }
            result.add(createNewList(rowList, n));
        }
        return result;
    }

    public static String listToString(List<Integer> list) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(list.get(i));
        }
        sb.append("}");
        return sb.toString();
    }

    public static class InputArgs {
        public String inputFile = null;
        public String outputFile = null;
    }

    public static InputArgs parseCmdArgs(String[] args) {
        InputArgs result = new InputArgs();
        if (args == null || args.length == 0) {
            return result;
        }
        List<String> positional = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--input-file=")) {
                result.inputFile = arg.substring("--input-file=".length());
            } else if (arg.startsWith("--output-file=")) {
                result.outputFile = arg.substring("--output-file=".length());
            } else if (arg.equals("-i") && i + 1 < args.length) {
                i++;
                result.inputFile = args[i];
            } else if (arg.equals("-o") && i + 1 < args.length) {
                i++;
                result.outputFile = args[i];
            } else if (!arg.startsWith("-")) {
                positional.add(arg);
            }
        }
        if (result.inputFile == null && positional.size() >= 1) {
            result.inputFile = positional.get(0);
        }
        if (result.outputFile == null && positional.size() >= 2) {
            result.outputFile = positional.get(1);
        }
        return result;
    }
}
