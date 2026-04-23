import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        TaskLogic.InputArgs parsed = TaskLogic.parseCmdArgs(args);

        System.out.println("Выберите задачу:");
        System.out.println("  1 - Три окружности с минимальным периметром треугольника центров");
        System.out.println("  2 - Разбить треугольники на подмножества подобных");
        System.out.print("Ваш выбор (1/2): ");
        String taskChoice = scanner.nextLine().trim();

        if (taskChoice.equals("1")) {
            runCircles(parsed);
        } else if (taskChoice.equals("2")) {
            runTriangles(parsed);
        } else {
            System.err.println("Неверный выбор. Введите 1 или 2.");
            System.exit(1);
        }
    }

    private static void runCircles(TaskLogic.InputArgs parsed) {
        List<Circle> circles = getCircles(parsed);
        if (circles == null) System.exit(1);

        System.out.println();
        printCircles(circles);
        System.out.println();
        System.out.println("Что сделать со списком?");
        System.out.println("  1 - Оставить как есть");
        System.out.println("  2 - Редактировать");
        System.out.print("Ваш выбор (1/2): ");
        if (scanner.nextLine().trim().equals("2")) {
            circles = editCircles(circles);
        }

        if (circles.size() < 3) {
            System.err.println("Нужно минимум 3 окружности, введено: " + circles.size());
            System.exit(5);
        }

        CircleTriple result = TaskLogic.findMinPerimeterTriple(circles);
        System.out.println();
        System.out.println(result);
        System.out.println();

        saveCircleResult(parsed, result);
    }

    private static void runTriangles(TaskLogic.InputArgs parsed) {
        List<Triangle> triangles = getTriangles(parsed);
        if (triangles == null) System.exit(1);

        System.out.println();
        printTriangles(triangles);
        System.out.println();
        System.out.println("Что сделать со списком?");
        System.out.println("  1 - Оставить как есть");
        System.out.println("  2 - Редактировать");
        System.out.print("Ваш выбор (1/2): ");
        if (scanner.nextLine().trim().equals("2")) {
            triangles = editTriangles(triangles);
        }

        if (triangles.isEmpty()) {
            System.err.println("Список треугольников пуст.");
            System.exit(5);
        }

        List<List<Triangle>> groups = TaskLogic.groupBySimilarity(triangles);
        System.out.println();
        printGroups(groups);
        System.out.println();

        saveTriangleGroups(parsed, groups);
    }

    private static List<Circle> getCircles(TaskLogic.InputArgs parsed) {
        if (parsed.inputFile != null) {
            return loadCirclesFromFile(parsed.inputFile);
        }
        System.out.println();
        System.out.println("Откуда взять данные?");
        System.out.println("  1 - Загрузить из файла");
        System.out.println("  2 - Ввести вручную");
        System.out.print("Ваш выбор (1/2): ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print("Путь к файлу: ");
            String f = scanner.nextLine().trim();
            if (f.isEmpty()) { System.err.println("Имя файла не может быть пустым."); System.exit(1); }
            return loadCirclesFromFile(f);
        } else {
            return inputCirclesManually();
        }
    }

    private static List<Triangle> getTriangles(TaskLogic.InputArgs parsed) {
        if (parsed.inputFile != null) {
            return loadTrianglesFromFile(parsed.inputFile);
        }
        System.out.println();
        System.out.println("Откуда взять данные?");
        System.out.println("  1 - Загрузить из файла");
        System.out.println("  2 - Ввести вручную");
        System.out.print("Ваш выбор (1/2): ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print("Путь к файлу: ");
            String f = scanner.nextLine().trim();
            if (f.isEmpty()) { System.err.println("Имя файла не может быть пустым."); System.exit(1); }
            return loadTrianglesFromFile(f);
        } else {
            return inputTrianglesManually();
        }
    }

    private static List<Circle> loadCirclesFromFile(String filename) {
        try {
            List<Circle> list = TaskLogic.readCirclesFromFile(filename);
            System.out.println("Загружено окружностей: " + list.size());
            return list;
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + filename); System.exit(2); return null;
        } catch (Exception e) {
            System.err.println("Ошибка чтения: " + e.getMessage()); System.exit(3); return null;
        }
    }

    private static List<Triangle> loadTrianglesFromFile(String filename) {
        try {
            List<Triangle> list = TaskLogic.readTrianglesFromFile(filename);
            System.out.println("Загружено треугольников: " + list.size());
            return list;
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + filename); System.exit(2); return null;
        } catch (Exception e) {
            System.err.println("Ошибка чтения: " + e.getMessage()); System.exit(3); return null;
        }
    }

    private static List<Circle> inputCirclesManually() {
        List<Circle> list = new ArrayList<Circle>();
        System.out.println("Формат каждой строки: x y r  (например: 1.5 2.0 3.0)");
        System.out.println("Пустая строка — завершить ввод (нужно минимум 3).");
        int num = 1;
        while (true) {
            System.out.print("Окружность " + num + " (Enter для завершения): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                if (list.size() < 3) { System.out.println("Нужно минимум 3. Продолжайте."); continue; }
                break;
            }
            try {
                list.add(Circle.fromFileLine(line));
                num++;
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        return list;
    }

    private static List<Triangle> inputTrianglesManually() {
        List<Triangle> list = new ArrayList<Triangle>();
        System.out.println("Формат каждой строки: x1 y1 x2 y2 x3 y3  (например: 0 0 3 0 0 4)");
        System.out.println("Пустая строка — завершить ввод.");
        int num = 1;
        while (true) {
            System.out.print("Треугольник " + num + " (Enter для завершения): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                if (list.isEmpty()) { System.out.println("Нужен хотя бы 1 треугольник. Продолжайте."); continue; }
                break;
            }
            try {
                Triangle t = Triangle.fromFileLine(line);
                if (!t.isValid()) { System.out.println("Треугольник вырожденный (стороны не образуют треугольник)."); continue; }
                list.add(t);
                num++;
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        return list;
    }

    private static List<Circle> editCircles(List<Circle> list) {
        List<Circle> current = new ArrayList<Circle>();
        for (int i = 0; i < list.size(); i++) {
            Circle c = list.get(i);
            current.add(new Circle(c.x, c.y, c.r));
        }
        while (true) {
            System.out.println();
            printCircles(current);
            System.out.println();
            System.out.println("  1 - Изменить по номеру");
            System.out.println("  2 - Добавить");
            System.out.println("  3 - Удалить по номеру");
            System.out.println("  4 - Ввести заново");
            System.out.println("  5 - Готово");
            System.out.print("Ваш выбор: ");
            String ch = scanner.nextLine().trim();
            if (ch.equals("1")) {
                System.out.print("Номер (1.." + current.size() + "): ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx < 0 || idx >= current.size()) { System.out.println("Неверный номер."); continue; }
                    System.out.print("Новые значения x y r: ");
                    current.set(idx, Circle.fromFileLine(scanner.nextLine()));
                } catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
            } else if (ch.equals("2")) {
                System.out.print("Введите x y r: ");
                try { current.add(Circle.fromFileLine(scanner.nextLine())); }
                catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
            } else if (ch.equals("3")) {
                if (current.isEmpty()) { System.out.println("Список пуст."); continue; }
                System.out.print("Номер (1.." + current.size() + "): ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx < 0 || idx >= current.size()) { System.out.println("Неверный номер."); continue; }
                    current.remove(idx);
                } catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
            } else if (ch.equals("4")) {
                current = inputCirclesManually();
            } else if (ch.equals("5")) {
                break;
            } else {
                System.out.println("Неверный выбор.");
            }
        }
        return current;
    }

    private static List<Triangle> editTriangles(List<Triangle> list) {
        List<Triangle> current = new ArrayList<Triangle>(list);
        while (true) {
            System.out.println();
            printTriangles(current);
            System.out.println();
            System.out.println("  1 - Изменить по номеру");
            System.out.println("  2 - Добавить");
            System.out.println("  3 - Удалить по номеру");
            System.out.println("  4 - Ввести заново");
            System.out.println("  5 - Готово");
            System.out.print("Ваш выбор: ");
            String ch = scanner.nextLine().trim();
            if (ch.equals("1")) {
                System.out.print("Номер (1.." + current.size() + "): ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx < 0 || idx >= current.size()) { System.out.println("Неверный номер."); continue; }
                    System.out.print("Новые значения x1 y1 x2 y2 x3 y3: ");
                    Triangle t = Triangle.fromFileLine(scanner.nextLine());
                    if (!t.isValid()) { System.out.println("Вырожденный треугольник."); continue; }
                    current.set(idx, t);
                } catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
            } else if (ch.equals("2")) {
                System.out.print("Введите x1 y1 x2 y2 x3 y3: ");
                try {
                    Triangle t = Triangle.fromFileLine(scanner.nextLine());
                    if (!t.isValid()) { System.out.println("Вырожденный треугольник."); continue; }
                    current.add(t);
                } catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
            } else if (ch.equals("3")) {
                if (current.isEmpty()) { System.out.println("Список пуст."); continue; }
                System.out.print("Номер (1.." + current.size() + "): ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (idx < 0 || idx >= current.size()) { System.out.println("Неверный номер."); continue; }
                    current.remove(idx);
                } catch (Exception e) { System.out.println("Ошибка: " + e.getMessage()); }
            } else if (ch.equals("4")) {
                current = inputTrianglesManually();
            } else if (ch.equals("5")) {
                break;
            } else {
                System.out.println("Неверный выбор.");
            }
        }
        return current;
    }

    private static void saveCircleResult(TaskLogic.InputArgs parsed, CircleTriple result) {
        if (parsed.outputFile == null) {
            System.out.println("Сохранить результат в файл?");
            System.out.println("  1 - Да");
            System.out.println("  2 - Нет");
            System.out.print("Ваш выбор (1/2): ");
            if (scanner.nextLine().trim().equals("1")) {
                System.out.print("Путь к файлу (Enter = output.txt): ");
                String f = scanner.nextLine().trim();
                parsed.outputFile = f.isEmpty() ? "output.txt" : f;
            }
        }
        if (parsed.outputFile != null) {
            try {
                TaskLogic.writeResultToFile(parsed.outputFile, result);
                System.out.println("Результат записан в: " + parsed.outputFile);
            } catch (Exception e) {
                System.err.println("Ошибка записи: " + e.getMessage()); System.exit(4);
            }
        }
        System.out.println("Готово.");
    }

    private static void saveTriangleGroups(TaskLogic.InputArgs parsed, List<List<Triangle>> groups) {
        if (parsed.outputFile == null) {
            System.out.println("Сохранить результат в файл?");
            System.out.println("  1 - Да");
            System.out.println("  2 - Нет");
            System.out.print("Ваш выбор (1/2): ");
            if (scanner.nextLine().trim().equals("1")) {
                System.out.print("Путь к файлу (Enter = output.txt): ");
                String f = scanner.nextLine().trim();
                parsed.outputFile = f.isEmpty() ? "output.txt" : f;
            }
        }
        if (parsed.outputFile != null) {
            try {
                TaskLogic.writeGroupsToFile(parsed.outputFile, groups);
                System.out.println("Результат записан в: " + parsed.outputFile);
            } catch (Exception e) {
                System.err.println("Ошибка записи: " + e.getMessage()); System.exit(4);
            }
        }
        System.out.println("Готово.");
    }

    private static void printCircles(List<Circle> list) {
        if (list.isEmpty()) { System.out.println("  (список пуст)"); return; }
        System.out.println("Окружностей: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            Circle c = list.get(i);
            System.out.printf("  %d. x=%.2f  y=%.2f  r=%.2f%n", i + 1, c.x, c.y, c.r);
        }
    }

    private static void printTriangles(List<Triangle> list) {
        if (list.isEmpty()) { System.out.println("  (список пуст)"); return; }
        System.out.println("Треугольников: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            Triangle t = list.get(i);
            System.out.printf("  %d. (%.2f,%.2f) (%.2f,%.2f) (%.2f,%.2f)   стороны: %.4f %.4f %.4f%n",
                i + 1, t.x1, t.y1, t.x2, t.y2, t.x3, t.y3,
                t.sideA(), t.sideB(), t.sideC());
        }
    }

    private static void printGroups(List<List<Triangle>> groups) {
        System.out.println("Найдено групп подобных треугольников: " + groups.size());
        for (int g = 0; g < groups.size(); g++) {
            List<Triangle> group = groups.get(g);
            System.out.println("  Группа " + (g + 1) + " (" + group.size() + " треугольн.):");
            for (int i = 0; i < group.size(); i++) {
                Triangle t = group.get(i);
                System.out.printf("    %d. (%.2f,%.2f) (%.2f,%.2f) (%.2f,%.2f)   стороны: %.4f %.4f %.4f%n",
                    i + 1, t.x1, t.y1, t.x2, t.y2, t.x3, t.y3,
                    t.sideA(), t.sideB(), t.sideC());
            }
        }
    }
}
