import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Оконное приложение.
 * Использует общий модуль TaskLogic.
 * Позволяет загружать данные из файла в JTable, редактировать, обрабатывать
 * и сохранять результат в файл.
 */
public class WindowApp extends JFrame {

    private JTable inputTable;
    private JTable outputTable;
    private DefaultTableModel inputModel;
    private DefaultTableModel outputModel;

    private JTextField inputFileField;
    private JTextField outputFileField;
    private JTextField rowsField;
    private JTextField colsField;
    private JLabel statusLabel;

    public WindowApp() {
        super("Удаление подряд идущих дублей из списка");
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        // --- Верхняя панель: файлы и размер таблицы ---
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        // Строка 1: входной файл
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Входной файл:"));
        inputFileField = new JTextField("input01.txt", 22);
        row1.add(inputFileField);
        JButton loadBtn = new JButton("Загрузить из файла");
        loadBtn.addActionListener(e -> loadFromFile());
        row1.add(loadBtn);
        topPanel.add(row1);

        // Строка 2: выходной файл
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Выходной файл:"));
        outputFileField = new JTextField("output.txt", 22);
        row2.add(outputFileField);
        JButton saveBtn = new JButton("Сохранить в файл");
        saveBtn.addActionListener(e -> saveToFile());
        row2.add(saveBtn);
        topPanel.add(row2);

        // Строка 3: размер таблицы вручную
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Строк:"));
        rowsField = new JTextField("3", 4);
        row3.add(rowsField);
        row3.add(new JLabel("Столбцов:"));
        colsField = new JTextField("8", 4);
        row3.add(colsField);
        JButton createBtn = new JButton("Создать таблицу");
        createBtn.addActionListener(e -> createEmptyTable());
        row3.add(createBtn);
        JButton processBtn = new JButton("▶  Обработать");
        processBtn.setFont(processBtn.getFont().deriveFont(Font.BOLD));
        processBtn.addActionListener(e -> process());
        row3.add(processBtn);
        topPanel.add(row3);

        add(topPanel, BorderLayout.NORTH);

        // --- Центральная панель: два JTable ---
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);

        // Входная таблица
        inputModel = new DefaultTableModel(3, 8);
        inputTable = new JTable(inputModel);
        styleTable(inputTable);
        JScrollPane inputScroll = new JScrollPane(inputTable);
        JPanel inputWrap = new JPanel(new BorderLayout());
        inputWrap.setBorder(BorderFactory.createTitledBorder("Входные данные (можно редактировать)"));
        inputWrap.add(inputScroll);
        split.setTopComponent(inputWrap);

        // Выходная таблица
        outputModel = new DefaultTableModel(3, 8);
        outputTable = new JTable(outputModel);
        outputTable.setEnabled(false);
        styleTable(outputTable);
        JScrollPane outputScroll = new JScrollPane(outputTable);
        JPanel outputWrap = new JPanel(new BorderLayout());
        outputWrap.setBorder(BorderFactory.createTitledBorder("Результат (подряд идущие дубли удалены)"));
        outputWrap.add(outputScroll);
        split.setBottomComponent(outputWrap);

        add(split, BorderLayout.CENTER);

        // --- Нижняя строка статуса ---
        statusLabel = new JLabel("Готово.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setFont(new Font("Monospaced", Font.PLAIN, 13));
    }

    // -------------------------------------------------------
    //  Функция: чтение данных из файла в двумерный массив
    // -------------------------------------------------------
    private int[][] readFileToArray(String filename) throws Exception {
        return TaskLogic.readArrayFromFile(filename);
    }

    // -------------------------------------------------------
    //  Функция: отображение двумерного массива в JTable
    // -------------------------------------------------------
    private void displayArrayInTable(int[][] array, DefaultTableModel model) {
        if (array.length == 0) {
            model.setRowCount(0);
            model.setColumnCount(0);
            return;
        }
        // Найдём максимальную длину строки
        int maxCols = 0;
        for (int[] row : array) {
            if (row.length > maxCols) maxCols = row.length;
        }
        model.setRowCount(array.length);
        model.setColumnCount(maxCols);
        for (int r = 0; r < array.length; r++) {
            for (int c = 0; c < maxCols; c++) {
                if (c < array[r].length) {
                    model.setValueAt(array[r][c], r, c);
                } else {
                    model.setValueAt("", r, c);
                }
            }
        }
    }

    // -------------------------------------------------------
    //  Функция: чтение данных из JTable в двумерный массив
    // -------------------------------------------------------
    private int[][] readTableToArray(DefaultTableModel model) throws Exception {
        int rows = model.getRowCount();
        int cols = model.getColumnCount();
        List<int[]> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<Integer> rowList = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                Object val = model.getValueAt(r, c);
                if (val != null && !val.toString().trim().isEmpty()) {
                    rowList.add(Integer.parseInt(val.toString().trim()));
                }
            }
            if (rowList.size() > 0) {
                int[] row = new int[rowList.size()];
                int idx = 0;
                for (int v : rowList) row[idx++] = v;
                result.add(row);
            }
        }
        int[][] arr = new int[result.size()][];
        int idx = 0;
        for (int[] row : result) arr[idx++] = row;
        return arr;
    }

    // -------------------------------------------------------
    //  Функция: запись двумерного массива в файл
    // -------------------------------------------------------
    private void writeArrayToFileWrapped(String filename, int[][] array) throws Exception {
        TaskLogic.writeArrayToFile(filename, array);
    }

    // -------------------------------------------------------
    //  Действия кнопок
    // -------------------------------------------------------

    private void loadFromFile() {
        String filename = inputFileField.getText().trim();
        try {
            int[][] array = readFileToArray(filename);
            displayArrayInTable(array, inputModel);
            // Очистить выходную таблицу
            outputModel.setRowCount(0);
            outputModel.setColumnCount(0);
            setStatus("Загружено из файла: " + filename, false);
        } catch (Exception ex) {
            setStatus("Ошибка загрузки: " + ex.getMessage(), true);
        }
    }

    private void saveToFile() {
        String filename = outputFileField.getText().trim();
        try {
            int[][] array = readTableToArray(outputModel);
            if (array.length == 0) {
                setStatus("Таблица результата пуста — нечего сохранять.", true);
                return;
            }
            writeArrayToFileWrapped(filename, array);
            setStatus("Сохранено в файл: " + filename, false);
        } catch (Exception ex) {
            setStatus("Ошибка сохранения: " + ex.getMessage(), true);
        }
    }

    private void createEmptyTable() {
        try {
            int rows = Integer.parseInt(rowsField.getText().trim());
            int cols = Integer.parseInt(colsField.getText().trim());
            inputModel.setRowCount(rows);
            inputModel.setColumnCount(cols);
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    inputModel.setValueAt("", r, c);
            outputModel.setRowCount(0);
            outputModel.setColumnCount(0);
            setStatus("Создана пустая таблица " + rows + "×" + cols, false);
        } catch (NumberFormatException ex) {
            setStatus("Ошибка: неверный формат числа строк/столбцов.", true);
        }
    }

    private void process() {
        try {
            int[][] input = readTableToArray(inputModel);
            if (input.length == 0) {
                setStatus("Входная таблица пуста.", true);
                return;
            }
            List<List<Integer>> processed = TaskLogic.processArray(input);

            // Конвертируем обратно в int[][]
            int maxCols = 0;
            for (List<Integer> row : processed)
                if (row.size() > maxCols) maxCols = row.size();

            int[][] outArray = new int[processed.size()][];
            int idx = 0;
            for (List<Integer> row : processed) {
                int[] arr = new int[row.size()];
                int j = 0;
                for (int v : row) arr[j++] = v;
                outArray[idx++] = arr;
            }

            displayArrayInTable(outArray, outputModel);
            setStatus("Обработка выполнена успешно.", false);
        } catch (Exception ex) {
            setStatus("Ошибка обработки: " + ex.getMessage(), true);
        }
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 120, 0));
    }

    // -------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WindowApp().setVisible(true));
    }
}
