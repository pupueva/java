import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        TaskLogic.InputArgs parsed = TaskLogic.parseCmdArgs(args);

        System.out.println("Выберите задачу:");
        System.out.println("  1 - Удаление подряд идущих дублей из списка");
        System.out.println("  2 - Циклический сдвиг списка на n позиций");
        System.out.print("Ваш выбор (1/2): ");
        String taskChoice = scanner.nextLine().trim();

        if (taskChoice.equals("1")) {
            runRemoveDuplicates(parsed);
        } else if (taskChoice.equals("2")) {
            runCyclicShift(parsed);
        } else {
            System.err.println("Неверный выбор. Введите 1 или 2.");
            System.exit(1);
        }
    }

    private static void runRemoveDuplicates(TaskLogic.InputArgs parsed) {
        List<Integer> input = getInputList(parsed);
        if (input == null) {
            System.exit(1);
        }

        System.out.println();
        System.out.println("Текущий список: " + TaskLogic.listToString(input));
        System.out.println();
        System.out.println("Что сделать со списком?");
        System.out.println("  1 - Оставить как есть");
        System.out.println("  2 - Изменить вручную");
        System.out.print("Ваш выбор (1/2): ");
        String editChoice = scanner.nextLine().trim();
        if (editChoice.equals("2")) {
            input = editList(input);
        }

        System.out.println();
        System.out.println("Вход:      " + TaskLogic.listToString(input));
        List<Integer> output = TaskLogic.removeDuplicates(input);
        System.out.println("Результат: " + TaskLogic.listToString(output));
        System.out.println();

        saveResult(parsed, output);
    }

    private static void runCyclicShift(TaskLogic.InputArgs parsed) {
        List<Integer> input = getInputList(parsed);
        if (input == null) {
            System.exit(1);
        }

        System.out.println();
        System.out.println("Текущий список: " + TaskLogic.listToString(input));
        System.out.println();
        System.out.println("Что сделать со списком?");
        System.out.println("  1 - Оставить как есть");
        System.out.println("  2 - Изменить вручную");
        System.out.print("Ваш выбор (1/2): ");
        String editChoice = scanner.nextLine().trim();
        if (editChoice.equals("2")) {
            input = editList(input);
        }

        System.out.println();
        System.out.print("Введите n (>0 сдвиг вправо, <0 сдвиг влево): ");
        int n;
        try {
            n = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: введите целое число.");
            System.exit(1);
            return;
        }

        System.out.println();
        System.out.println("Вход:      " + TaskLogic.listToString(input));
        List<Integer> output = TaskLogic.createNewList(input, n);
        System.out.println("Результат: " + TaskLogic.listToString(output));
        System.out.println();

        saveResult(parsed, output);
    }

    private static List<Integer> getInputList(TaskLogic.InputArgs parsed) {
        if (parsed.inputFile != null) {
            return loadFromFile(parsed.inputFile);
        }

        System.out.println();
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
            return loadFromFile(filename);
        } else {
            return inputManually();
        }
    }

    private static void saveResult(TaskLogic.InputArgs parsed, List<Integer> output) {
        if (parsed.outputFile == null) {
            System.out.println("Куда сохранить результат?");
            System.out.println("  1 - Сохранить в файл");
            System.out.println("  2 - Только показать на экране");
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

        System.out.println("Готово.");
    }

    private static List<Integer> loadFromFile(String filename) {
        try {
            List<Integer> list = TaskLogic.readListFromFile(filename);
            System.out.println("Загружено из файла: " + filename);
            return list;
        } catch (FileNotFoundException e) {
            System.err.println("Ошибка: файл не найден: " + filename);
            System.exit(2);
            return null;
        } catch (Exception e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.exit(3);
            return null;
        }
    }

    private static List<Integer> inputManually() {
        System.out.println("Введите числа через пробел или запятую:");
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        return parseNumbers(line);
    }

    private static List<Integer> editList(List<Integer> list) {
        List<Integer> current = new ArrayList<Integer>(list);

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
                try {
                    current.add(Integer.parseInt(scanner.nextLine().trim()));
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
                System.out.println("Неверный выбор.");
            }
        }

        return current;
    }

    private static void changeElement(List<Integer> list) {
        System.out.print("Введите номер элемента (1.." + list.size() + "): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= list.size()) {
                System.out.println("Номер вне диапазона.");
                return;
            }
            System.out.print("Новое значение: ");
            list.set(idx, Integer.parseInt(scanner.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите целое число.");
        }
    }

    private static void removeElement(List<Integer> list) {
        if (list.isEmpty()) {
            System.out.println("Список пуст.");
            return;
        }
        System.out.print("Введите номер элемента для удаления (1.." + list.size() + "): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= list.size()) {
                System.out.println("Номер вне диапазона.");
                return;
            }
            list.remove(idx);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите целое число.");
        }
    }

    private static void printNumbered(List<Integer> list) {
        if (list.isEmpty()) {
            System.out.println("  (пустой список)");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + list.get(i));
        }
    }

    private static List<Integer> parseNumbers(String line) {
        List<Integer> result = new ArrayList<Integer>();
        if (line.isEmpty()) {
            return result;
        }
        String[] tokens = line.split("[,\\s]+");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (!token.isEmpty()) {
                try {
                    result.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    System.out.println("Предупреждение: \"" + token + "\" не число, пропущено.");
                }
            }
        }
        return result;
    }
}
