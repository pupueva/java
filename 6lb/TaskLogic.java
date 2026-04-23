import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskLogic {

    public static double distance(Circle a, Circle b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double trianglePerimeter(Circle a, Circle b, Circle c) {
        return distance(a, b) + distance(b, c) + distance(a, c);
    }

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

    public static List<Circle> readCirclesFromFile(String filename) throws IOException {
        List<Circle> list = new ArrayList<Circle>();
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

    public static void writeCirclesToFile(String filename, List<Circle> circles) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            for (int i = 0; i < circles.size(); i++) {
                writer.write(circles.get(i).toFileLine());
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    public static void writeResultToFile(String filename, CircleTriple result) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            if (result == null) {
                writer.write("Недостаточно окружностей для формирования тройки (нужно >= 3).");
            } else {
                writer.write("Тройка окружностей с минимальным периметром треугольника центров:");
                writer.newLine();
                writer.write("  " + result.a.toFileLine());
                writer.newLine();
                writer.write("  " + result.b.toFileLine());
                writer.newLine();
                writer.write("  " + result.c.toFileLine());
                writer.newLine();
                writer.write(String.format("Периметр = %.6f", result.perimeter));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    public static String[][] circlesToTable(List<Circle> circles) {
        String[][] data = new String[circles.size()][3];
        for (int i = 0; i < circles.size(); i++) {
            Circle c = circles.get(i);
            data[i][0] = String.valueOf(c.x);
            data[i][1] = String.valueOf(c.y);
            data[i][2] = String.valueOf(c.r);
        }
        return data;
    }

    public static List<Circle> tableToCircles(String[][] data) {
        List<Circle> list = new ArrayList<Circle>();
        for (int i = 0; i < data.length; i++) {
            String[] row = data[i];
            if (row[0] == null || row[0].trim().isEmpty()) continue;
            try {
                double x = Double.parseDouble(row[0].trim());
                double y = Double.parseDouble(row[1].trim());
                double r = Double.parseDouble(row[2].trim());
                list.add(new Circle(x, y, r));
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    public static List<List<Triangle>> groupBySimilarity(List<Triangle> triangles) {
        List<List<Triangle>> groups = new ArrayList<List<Triangle>>();
        boolean[] assigned = new boolean[triangles.size()];

        for (int i = 0; i < triangles.size(); i++) {
            if (assigned[i]) continue;
            List<Triangle> group = new ArrayList<Triangle>();
            group.add(triangles.get(i));
            assigned[i] = true;
            for (int j = i + 1; j < triangles.size(); j++) {
                if (!assigned[j] && Triangle.areSimilar(triangles.get(i), triangles.get(j))) {
                    group.add(triangles.get(j));
                    assigned[j] = true;
                }
            }
            groups.add(group);
        }
        return groups;
    }

    public static List<Triangle> readTrianglesFromFile(String filename) throws IOException {
        List<Triangle> list = new ArrayList<Triangle>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    list.add(Triangle.fromFileLine(line));
                } catch (Exception e) {
                    throw new IOException("Ошибка в строке " + lineNum + ": " + e.getMessage());
                }
            }
        } finally {
            reader.close();
        }
        return list;
    }

    public static void writeTrianglesToFile(String filename, List<Triangle> triangles) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            for (int i = 0; i < triangles.size(); i++) {
                writer.write(triangles.get(i).toFileLine());
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    public static void writeGroupsToFile(String filename, List<List<Triangle>> groups) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        try {
            writer.write("Подмножества подобных треугольников (" + groups.size() + " групп):");
            writer.newLine();
            for (int g = 0; g < groups.size(); g++) {
                List<Triangle> group = groups.get(g);
                writer.write("Группа " + (g + 1) + " (" + group.size() + " треугольн.):");
                writer.newLine();
                for (int i = 0; i < group.size(); i++) {
                    writer.write("  " + group.get(i).toString());
                    writer.newLine();
                }
            }
        } finally {
            writer.close();
        }
    }

    public static String[][] trianglesToTable(List<Triangle> triangles) {
        String[][] data = new String[triangles.size()][6];
        for (int i = 0; i < triangles.size(); i++) {
            Triangle t = triangles.get(i);
            data[i][0] = String.valueOf(t.x1);
            data[i][1] = String.valueOf(t.y1);
            data[i][2] = String.valueOf(t.x2);
            data[i][3] = String.valueOf(t.y2);
            data[i][4] = String.valueOf(t.x3);
            data[i][5] = String.valueOf(t.y3);
        }
        return data;
    }

    public static List<Triangle> tableToTriangles(String[][] data) {
        List<Triangle> list = new ArrayList<Triangle>();
        for (int i = 0; i < data.length; i++) {
            String[] row = data[i];
            if (row[0] == null || row[0].trim().isEmpty()) continue;
            try {
                double x1 = Double.parseDouble(row[0].trim());
                double y1 = Double.parseDouble(row[1].trim());
                double x2 = Double.parseDouble(row[2].trim());
                double y2 = Double.parseDouble(row[3].trim());
                double x3 = Double.parseDouble(row[4].trim());
                double y3 = Double.parseDouble(row[5].trim());
                Triangle t = new Triangle(x1, y1, x2, y2, x3, y3);
                if (t.isValid()) list.add(t);
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    public static class InputArgs {
        public String inputFile = null;
        public String outputFile = null;
    }

    public static InputArgs parseCmdArgs(String[] args) {
        InputArgs result = new InputArgs();
        if (args == null || args.length == 0) return result;
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
        if (result.inputFile == null && positional.size() >= 1) result.inputFile = positional.get(0);
        if (result.outputFile == null && positional.size() >= 2) result.outputFile = positional.get(1);
        return result;
    }
}
