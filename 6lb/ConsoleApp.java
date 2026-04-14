import java.util.*;

/**
 * Консольное приложение.
 * Форматы запуска:
 *   java ConsoleApp                          ← интерактивный режим
 *   java ConsoleApp input.txt output.txt
 *   java ConsoleApp -i input.txt -o output.txt
 *   java ConsoleApp --input-file=in.txt --output-file=out.txt
 */
public class ConsoleApp {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Три окружности с минимальным периметром треугольника центров");
        System.out.println();

        TaskLogic.InputArgs parsed = TaskLogic.parseCmdArgs(args);
        List<Circle> circles = null;

        // --- Получение данных ---
        if (parsed.inputFile != null) {
            circles = loadFromFile(parsed.inputFile);
        } else {
            System.out.println("Откуда взять данные об окружностях?");
            System.out.println("  1 - Загрузить из файла");
            System.out.println("  2 - Ввести вручную");
            System.out.print("Ваш выбор (1/2): ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Путь к входному файлу: ");
                String f = scanner.nextLine().trim();
                if (f.isEmpty()) { System.err.println("Имя файла не может быть пустым."); System.exit(1); }
                parsed.inputFile = f;
                circles = loadFromFile(f);
            } else {
                circles = inputManually();
            }
        }

        if (circles == null) System.exit(1);

        // --- Показ и редактирование ---
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

        // --- Обработка ---
        if (circles.size() < 3) {
            System.err.println("Ошибка: нужно минимум 3 окружности, введено: " + circles.size());
            System.exit(5);
        }

        CircleTriple result = TaskLogic.findMinPerimeterTriple(circles);
        System.out.println();
        System.out.println("РЕЗУЛЬТАТ");
        System.out.println(result);
        System.out.println();

        // --- Сохранение ---
        if (parsed.outputFile == null) {
            System.out.println("Сохранить результат в файл?");
            System.out.println("  1 - Да");
            System.out.println("  2 - Нет");
            System.out.print("Ваш выбор (1/2): ");
            if (scanner.nextLine().trim().equals("1")) {
                System.out.print("Путь к выходному файлу (Enter = output.txt): ");
                String f = scanner.nextLine().trim();
                parsed.outputFile = f.isEmpty() ? "output.txt" : f;
            }
        }

        if (parsed.outputFile != null) {
            try {
                TaskLogic.writeResultToFile(parsed.outputFile, result);
                System.out.println("Результат записан в: " + parsed.outputFile);
            } catch (Exception e) {
                System.err.println("Ошибка записи: " + e.getMessage());
                System.exit(4);
            }
        }

        System.out.println("Готово.");
    }

    // -------------------------------------------------------

    private static List<Circle> loadFromFile(String filename) {
        try {
            List<Circle> list = TaskLogic.readCirclesFromFile(filename);
            System.out.println("Загружено окружностей: " + list.size() + " из файла: " + filename);
            return list;
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Ошибка: файл не найден: " + filename);
            System.exit(2); return null;
        } catch (Exception e) {
            System.err.println("Ошибка чтения: " + e.getMessage());
            System.exit(3); return null;
        }
    }

    private static List<Circle> inputManually() {
        List<Circle> list = new ArrayList<>();
        System.out.println("Вводите окружности по одной (пустая строка — завершить ввод).");
        System.out.println("Формат каждой строки: x y r  (например: 1.5 2.0 3.0)");
        int num = 1;
        while (true) {
            System.out.print("Окружность " + num + " (или Enter для завершения): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                if (list.size() < 3) {
                    System.out.println("Введено " + list.size() + " — нужно минимум 3. Продолжайте.");
                    continue;
                }
                break;
            }
            try {
                list.add(Circle.fromFileLine(line));
                num++;
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
        return list;
    }

    private static List<Circle> editCircles(List<Circle> list) {
        List<Circle> current = new ArrayList<>();
        for (Circle c : list) current.add(new Circle(c.x, c.y, c.r));

        while (true) {
            System.out.println();
            printCircles(current);
            System.out.println();
            System.out.println("  1 - Изменить окружность по номеру");
            System.out.println("  2 - Добавить окружность");
            System.out.println("  3 - Удалить окружность по номеру");
            System.out.println("  4 - Ввести список заново");
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
                current = inputManually();
            } else if (ch.equals("5")) {
                break;
            } else {
                System.out.println("Неверный выбор.");
            }
        }
        return current;
    }

    private static void printCircles(List<Circle> list) {
        if (list.isEmpty()) { System.out.println("  (список пуст)"); return; }
        System.out.println("Список окружностей (" + list.size() + " шт.):");
        int i = 1;
        for (Circle c : list) {
            System.out.printf("  %d. x=%.2f  y=%.2f  r=%.2f%n", i++, c.x, c.y, c.r);
        }
    }
}
