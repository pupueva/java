import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Консольное приложение.
 * Использует общий модуль TaskLogic.
 *
 * Форматы запуска:
 *   java ConsoleApp                                                  <- полностью интерактивный режим
 *   java ConsoleApp input.txt output.txt
 *   java ConsoleApp -i input.txt -o output.txt
 *   java ConsoleApp --input-file=input.txt --output-file=output.txt
 */
public class ConsoleApp {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Удаление подряд идущих дублей из списка ===");
        System.out.println();

        TaskLogic.InputArgs parsed = TaskLogic.parseCmdArgs(args);

        // --- Получаем список чисел: из файла или вводим вручную ---
        List<Integer> input = null;

        if (parsed.inputFile != null) {
            // Файл задан через аргументы — читаем сразу
            input = loadFromFile(parsed.inputFile);
        } else {
            // Спрашиваем: файл или ручной ввод
            System.out.println("Откуда взять числа?");
            System.out.println("  1 - Загрузить из файла");
            System.out.println("  2 - Ввести вручную");
            System.out.print("Ваш выбор (1/2): ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Введите путь к входному файлу: ");
                String filename = scanner.nextLine().trim();
                if (filename.isEmpty()) {
                    System.err.println("Ошибка: имя файла не может быть пустым.");
                    System.exit(1);
                }
                parsed.inputFile = filename;
                input = loadFromFile(parsed.inputFile);
            } else {
                // Ввод вручную
                input = inputManually();
            }
        }

        if (input == null) System.exit(1);

        // --- Показываем текущий список и предлагаем редактировать ---
        System.out.println();
        System.out.println("Текущий список: " + TaskLogic.listToString(input));
        System.out.println();
        System.out.println("Что сделать со списком?");
        System.out.println("  1 - Оставить как есть");
        System.out.println("  2 - Изменить числа вручную");
        System.out.print("Ваш выбор (1/2): ");
        String editChoice = scanner.nextLine().trim();

        if (editChoice.equals("2")) {
            input = editList(input);
        }

        // --- Обработка ---
        System.out.println();
        System.out.println("Итоговый вход:  " + TaskLogic.listToString(input));
        List<Integer> output = TaskLogic.createNewList(input);
        System.out.println("Результат:      " + TaskLogic.listToString(output));
        System.out.println();

        // --- Куда сохранить результат ---
        if (parsed.outputFile == null) {
            System.out.println("Куда сохранить результат?");
            System.out.println("  1 - Сохранить в файл");
            System.out.println("  2 - Только показать на экране (не сохранять)");
            System.out.print("Ваш выбор (1/2): ");
            String saveChoice = scanner.nextLine().trim();

            if (saveChoice.equals("1")) {
                System.out.print("Введите путь к выходному файлу (Enter = output.txt): ");
                String line = scanner.nextLine().trim();
                parsed.outputFile = line.isEmpty() ? "output.txt" : line;
            }
        }

        if (parsed.outputFile != null) {
            try {
                TaskLogic.writeListToFile(parsed.outputFile, output);
                System.out.println("Результат записан в файл: " + parsed.outputFile);
            } catch (Exception e) {
                System.err.println("Ошибка при записи файла: " + e.getMessage());
                System.exit(4);
            }
        }

        System.out.println();
        System.out.println("Готово.");
    }

    /** Загружает список из файла, выводит ошибку и завершает программу при неудаче. */
    private static List<Integer> loadFromFile(String filename) {
        try {
            List<Integer> list = TaskLogic.readListFromFile(filename);
            System.out.println("Загружено из файла: " + filename);
            return list;
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Ошибка: файл не найден: " + filename);
            System.exit(2);
            return null;
        } catch (Exception e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.exit(3);
            return null;
        }
    }

    /** Позволяет ввести список чисел вручную с клавиатуры. */
    private static List<Integer> inputManually() {
        System.out.println("Введите числа через пробел или запятую (например: 1 2 2 3 3 3 4):");
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        return parseNumbers(line);
    }

    /**
     * Позволяет отредактировать существующий список:
     * показывает пронумерованные элементы и предлагает меню правки.
     */
    private static List<Integer> editList(List<Integer> list) {
        // Копируем в изменяемый список
        List<Integer> current = new ArrayList<>();
        for (Integer v : list) current.add(v);

        while (true) {
            System.out.println();
            System.out.println("Текущий список:");
            printNumbered(current);
            System.out.println();
            System.out.println("  1 - Изменить элемент по номеру");
            System.out.println("  2 - Добавить число в конец");
            System.out.println("  3 - Удалить элемент по номеру");
            System.out.println("  4 - Ввести список заново");
            System.out.println("  5 - Готово");
            System.out.print("Ваш выбор: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                changeElement(current);
            } else if (choice.equals("2")) {
                System.out.print("Введите число для добавления: ");
                String input = scanner.nextLine().trim();
                try {
                    current.add(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите целое число.");
                }
            } else if (choice.equals("3")) {
                removeElement(current);
            } else if (choice.equals("4")) {
                current = inputManually();
            } else if (choice.equals("5")) {
                break;
            } else {
                System.out.println("Неверный выбор, попробуйте снова.");
            }
        }

        return current;
    }

    /** Изменяет один элемент списка по номеру (с 1). */
    private static void changeElement(List<Integer> list) {
        System.out.print("Введите номер элемента (1.." + list.size() + "): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= list.size()) {
                System.out.println("Ошибка: номер вне диапазона.");
                return;
            }
            System.out.print("Новое значение: ");
            int val = Integer.parseInt(scanner.nextLine().trim());
            list.set(idx, val);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите целое число.");
        }
    }

    /** Удаляет элемент списка по номеру (с 1). */
    private static void removeElement(List<Integer> list) {
        if (list.isEmpty()) {
            System.out.println("Список пуст, нечего удалять.");
            return;
        }
        System.out.print("Введите номер элемента для удаления (1.." + list.size() + "): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= list.size()) {
                System.out.println("Ошибка: номер вне диапазона.");
                return;
            }
            list.remove(idx);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите целое число.");
        }
    }

    /** Выводит список с нумерацией с 1. */
    private static void printNumbered(List<Integer> list) {
        if (list.isEmpty()) {
            System.out.println("  (пустой список)");
            return;
        }
        int i = 1;
        for (Integer v : list) {
            System.out.println("  " + i + ". " + v);
            i++;
        }
    }

    /** Разбирает строку с числами (через пробел или запятую) в список. */
    private static List<Integer> parseNumbers(String line) {
        List<Integer> result = new ArrayList<>();
        if (line.isEmpty()) return result;
        String[] tokens = line.split("[,\\s]+");
        for (String token : tokens) {
            token = token.trim();
            if (!token.isEmpty()) {
                try {
                    result.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    System.out.println("Предупреждение: \"" + token + "\" — не число, пропущено.");
                }
            }
        }
        return result;
    }
}