import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Оконное приложение.
 * Список окружностей задаётся в JTable (столбцы: X, Y, R).
 * Использует TaskLogic без изменений.
 */
public class WindowApp extends JFrame {

    private static final String[] COLUMNS = {"X (центр)", "Y (центр)", "R (радиус)"};

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea resultArea;
    private JTextField inputFileField;
    private JTextField outputFileField;
    private JLabel statusLabel;

    public WindowApp() {
        super("Три окружности с минимальным периметром треугольника центров");
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        setLayout(new BorderLayout(6, 6));

        // --- Верхняя панель ---
        JPanel top = new JPanel(new GridLayout(3, 1, 3, 3));
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        // Строка 1: входной файл
        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r1.add(new JLabel("Входной файл:"));
        inputFileField = new JTextField("input01.txt", 20);
        r1.add(inputFileField);
        JButton loadBtn = new JButton("Загрузить");
        loadBtn.addActionListener(e -> loadFromFile());
        r1.add(loadBtn);
        JButton addRowBtn = new JButton("+ Добавить строку");
        addRowBtn.addActionListener(e -> tableModel.addRow(new Object[]{"", "", ""}));
        r1.add(addRowBtn);
        top.add(r1);

        // Строка 2: выходной файл
        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r2.add(new JLabel("Выходной файл:"));
        outputFileField = new JTextField("output.txt", 20);
        r2.add(outputFileField);
        JButton saveBtn = new JButton("Сохранить результат");
        saveBtn.addActionListener(e -> saveResult());
        r2.add(saveBtn);
        top.add(r2);

        // Строка 3: кнопки действий
        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton calcBtn = new JButton("▶  Найти тройку с мин. периметром");
        calcBtn.setFont(calcBtn.getFont().deriveFont(Font.BOLD));
        calcBtn.addActionListener(e -> calculate());
        r3.add(calcBtn);
        JButton clearBtn = new JButton("Очистить таблицу");
        clearBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            resultArea.setText("");
            setStatus("Таблица очищена.", false);
        });
        r3.add(clearBtn);
        top.add(r3);

        add(top, BorderLayout.NORTH);

        // --- Центр: таблица + результат ---
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.55);

        // JTable
        tableModel = new DefaultTableModel(COLUMNS, 5) {
            @Override public boolean isCellEditable(int row, int col) { return true; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Monospaced", Font.PLAIN, 13));
        table.setGridColor(Color.LIGHT_GRAY);
        JScrollPane tableScroll = new JScrollPane(table);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Окружности (X, Y, R)"));
        leftPanel.add(tableScroll);
        split.setLeftComponent(leftPanel);

        // Результат
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultArea.setMargin(new Insets(6, 6, 6, 6));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Результат"));
        rightPanel.add(resultScroll);
        split.setRightComponent(rightPanel);

        add(split, BorderLayout.CENTER);

        // --- Статус ---
        statusLabel = new JLabel("Готово.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        add(statusLabel, BorderLayout.SOUTH);
    }

    // -------------------------------------------------------
    //  Функция: чтение данных из файла в двумерный массив
    // -------------------------------------------------------
    private String[][] readFileToArray(String filename) throws Exception {
        List<Circle> circles = TaskLogic.readCirclesFromFile(filename);
        return TaskLogic.circlesToTable(circles);
    }

    // -------------------------------------------------------
    //  Функция: отображение двумерного массива в JTable
    // -------------------------------------------------------
    private void displayArrayInTable(String[][] data) {
        tableModel.setRowCount(0);
        for (String[] row : data) {
            tableModel.addRow(row);
        }
    }

    // -------------------------------------------------------
    //  Функция: чтение данных из JTable в двумерный массив
    // -------------------------------------------------------
    private String[][] readTableToArray() {
        // Завершаем редактирование ячейки, если активно
        if (table.isEditing()) table.getCellEditor().stopCellEditing();

        int rows = tableModel.getRowCount();
        String[][] data = new String[rows][3];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 3; c++) {
                Object val = tableModel.getValueAt(r, c);
                data[r][c] = val == null ? "" : val.toString().trim();
            }
        }
        return data;
    }

    // -------------------------------------------------------
    //  Функция: запись двумерного массива в файл
    // -------------------------------------------------------
    private void writeArrayToFile(String filename, String[][] data) throws Exception {
        List<Circle> circles = TaskLogic.tableToCircles(data);
        TaskLogic.writeCirclesToFile(filename, circles);
    }

    // -------------------------------------------------------
    //  Действия кнопок
    // -------------------------------------------------------

    private void loadFromFile() {
        String filename = inputFileField.getText().trim();
        try {
            String[][] data = readFileToArray(filename);
            displayArrayInTable(data);
            resultArea.setText("");
            setStatus("Загружено " + data.length + " окружностей из: " + filename, false);
        } catch (Exception ex) {
            setStatus("Ошибка загрузки: " + ex.getMessage(), true);
        }
    }

    private void calculate() {
        String[][] data = readTableToArray();
        List<Circle> circles = TaskLogic.tableToCircles(data);
        if (circles.size() < 3) {
            setStatus("Нужно минимум 3 корректные окружности, введено: " + circles.size(), true);
            return;
        }
        CircleTriple result = TaskLogic.findMinPerimeterTriple(circles);
        resultArea.setText(result == null ? "Результат не найден." : result.toString());
        setStatus("Обработано " + circles.size() + " окружностей. Тройка найдена.", false);
    }

    private void saveResult() {
        String[][] data = readTableToArray();
        List<Circle> circles = TaskLogic.tableToCircles(data);
        CircleTriple result = circles.size() >= 3 ? TaskLogic.findMinPerimeterTriple(circles) : null;
        String filename = outputFileField.getText().trim();
        try {
            TaskLogic.writeResultToFile(filename, result);
            setStatus("Результат сохранён в: " + filename, false);
        } catch (Exception ex) {
            setStatus("Ошибка сохранения: " + ex.getMessage(), true);
        }
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 120, 0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WindowApp().setVisible(true));
    }
}
