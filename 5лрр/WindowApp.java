import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class WindowApp extends JFrame {

    private JTable inputTableDup;
    private JTable outputTableDup;
    private DefaultTableModel inputModelDup;
    private DefaultTableModel outputModelDup;
    private JTextField inputFileFieldDup;
    private JTextField outputFileFieldDup;
    private JTextField rowsFieldDup;
    private JTextField colsFieldDup;
    private JLabel statusLabelDup;

    private JTable inputTableShift;
    private JTable outputTableShift;
    private DefaultTableModel inputModelShift;
    private DefaultTableModel outputModelShift;
    private JTextField inputFileFieldShift;
    private JTextField outputFileFieldShift;
    private JTextField rowsFieldShift;
    private JTextField colsFieldShift;
    private JSpinner shiftSpinner;
    private JLabel statusLabelShift;

    public WindowApp() {
        super("Операции со списком");
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Удаление дублей", buildDupPanel());
        tabs.addTab("Циклический сдвиг", buildShiftPanel());
        setContentPane(tabs);
    }

    private JPanel buildDupPanel() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 4, 4));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Входной файл:"));
        inputFileFieldDup = new JTextField("input.txt", 22);
        row1.add(inputFileFieldDup);
        JButton loadBtn = new JButton("Загрузить из файла");
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadFromFileDup();
            }
        });
        row1.add(loadBtn);
        topPanel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Выходной файл:"));
        outputFileFieldDup = new JTextField("output.txt", 22);
        row2.add(outputFileFieldDup);
        JButton saveBtn = new JButton("Сохранить результат");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveToFileDup();
            }
        });
        row2.add(saveBtn);
        topPanel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Строк:"));
        rowsFieldDup = new JTextField("3", 4);
        row3.add(rowsFieldDup);
        row3.add(new JLabel("Столбцов:"));
        colsFieldDup = new JTextField("8", 4);
        row3.add(colsFieldDup);
        JButton createBtn = new JButton("Создать таблицу");
        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createEmptyTableDup();
            }
        });
        row3.add(createBtn);
        JButton processBtn = new JButton("Обработать");
        processBtn.setFont(processBtn.getFont().deriveFont(Font.BOLD));
        processBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processDup();
            }
        });
        row3.add(processBtn);
        topPanel.add(row3);

        main.add(topPanel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);

        inputModelDup = new DefaultTableModel(3, 8);
        inputTableDup = new JTable(inputModelDup);
        styleTable(inputTableDup);
        JPanel inputWrap = new JPanel(new BorderLayout());
        inputWrap.setBorder(BorderFactory.createTitledBorder("Входные данные"));
        inputWrap.add(new JScrollPane(inputTableDup));
        split.setTopComponent(inputWrap);

        outputModelDup = new DefaultTableModel(3, 8);
        outputTableDup = new JTable(outputModelDup);
        outputTableDup.setEnabled(false);
        styleTable(outputTableDup);
        JPanel outputWrap = new JPanel(new BorderLayout());
        outputWrap.setBorder(BorderFactory.createTitledBorder("Результат — дубли удалены"));
        outputWrap.add(new JScrollPane(outputTableDup));
        split.setBottomComponent(outputWrap);

        main.add(split, BorderLayout.CENTER);

        statusLabelDup = new JLabel("Введите данные и нажмите Обработать.");
        statusLabelDup.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        main.add(statusLabelDup, BorderLayout.SOUTH);

        return main;
    }

    private JPanel buildShiftPanel() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 4, 4));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Входной файл:"));
        inputFileFieldShift = new JTextField("input.txt", 22);
        row1.add(inputFileFieldShift);
        JButton loadBtn = new JButton("Загрузить из файла");
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadFromFileShift();
            }
        });
        row1.add(loadBtn);
        topPanel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Выходной файл:"));
        outputFileFieldShift = new JTextField("output.txt", 22);
        row2.add(outputFileFieldShift);
        JButton saveBtn = new JButton("Сохранить результат");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveToFileShift();
            }
        });
        row2.add(saveBtn);
        topPanel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Строк:"));
        rowsFieldShift = new JTextField("3", 4);
        row3.add(rowsFieldShift);
        row3.add(new JLabel("Столбцов:"));
        colsFieldShift = new JTextField("6", 4);
        row3.add(colsFieldShift);
        JButton createBtn = new JButton("Создать таблицу");
        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createEmptyTableShift();
            }
        });
        row3.add(createBtn);
        row3.add(new JLabel("  n:"));
        shiftSpinner = new JSpinner(new SpinnerNumberModel(1, -100000, 100000, 1));
        ((JSpinner.DefaultEditor) shiftSpinner.getEditor()).getTextField().setColumns(5);
        row3.add(shiftSpinner);
        row3.add(new JLabel("(+ вправо, - влево)"));
        JButton processBtn = new JButton("Выполнить сдвиг");
        processBtn.setFont(processBtn.getFont().deriveFont(Font.BOLD));
        processBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processShift();
            }
        });
        row3.add(processBtn);
        topPanel.add(row3);

        main.add(topPanel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);

        inputModelShift = new DefaultTableModel(3, 6);
        inputTableShift = new JTable(inputModelShift);
        styleTable(inputTableShift);
        JPanel inputWrap = new JPanel(new BorderLayout());
        inputWrap.setBorder(BorderFactory.createTitledBorder("Входные данные"));
        inputWrap.add(new JScrollPane(inputTableShift));
        split.setTopComponent(inputWrap);

        outputModelShift = new DefaultTableModel(3, 6);
        outputTableShift = new JTable(outputModelShift);
        outputTableShift.setEnabled(false);
        styleTable(outputTableShift);
        JPanel outputWrap = new JPanel(new BorderLayout());
        outputWrap.setBorder(BorderFactory.createTitledBorder("Результат — после сдвига"));
        outputWrap.add(new JScrollPane(outputTableShift));
        split.setBottomComponent(outputWrap);

        main.add(split, BorderLayout.CENTER);

        statusLabelShift = new JLabel("Введите данные, укажите n и нажмите Выполнить сдвиг.");
        statusLabelShift.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        main.add(statusLabelShift, BorderLayout.SOUTH);

        return main;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setFont(new Font("Monospaced", Font.PLAIN, 13));
    }

    private void stopEditing(JTable table) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    private int[][] readTableToArray(JTable table, DefaultTableModel model) throws Exception {
        stopEditing(table);
        int rows = model.getRowCount();
        int cols = model.getColumnCount();
        List<int[]> result = new ArrayList<int[]>();
        for (int r = 0; r < rows; r++) {
            List<Integer> rowList = new ArrayList<Integer>();
            for (int c = 0; c < cols; c++) {
                Object val = model.getValueAt(r, c);
                if (val != null && !val.toString().trim().isEmpty()) {
                    rowList.add(Integer.parseInt(val.toString().trim()));
                }
            }
            if (!rowList.isEmpty()) {
                int[] row = new int[rowList.size()];
                for (int i = 0; i < rowList.size(); i++) {
                    row[i] = rowList.get(i);
                }
                result.add(row);
            }
        }
        int[][] arr = new int[result.size()][];
        for (int i = 0; i < result.size(); i++) {
            arr[i] = result.get(i);
        }
        return arr;
    }

    private void displayArrayInTable(int[][] array, DefaultTableModel model) {
        if (array.length == 0) {
            model.setRowCount(0);
            model.setColumnCount(0);
            return;
        }
        int maxCols = 0;
        for (int r = 0; r < array.length; r++) {
            if (array[r].length > maxCols) {
                maxCols = array[r].length;
            }
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

    private void setStatus(JLabel label, String msg, boolean isError) {
        label.setText(msg);
        label.setForeground(isError ? Color.RED : new Color(0, 120, 0));
    }

    private void loadFromFileDup() {
        String filename = inputFileFieldDup.getText().trim();
        if (filename.isEmpty()) {
            setStatus(statusLabelDup, "Укажите имя файла.", true);
            return;
        }
        try {
            int[][] array = TaskLogic.readArrayFromFile(filename);
            displayArrayInTable(array, inputModelDup);
            outputModelDup.setRowCount(0);
            outputModelDup.setColumnCount(0);
            setStatus(statusLabelDup, "Загружено из файла: " + filename, false);
        } catch (Exception ex) {
            setStatus(statusLabelDup, "Ошибка загрузки: " + ex.getMessage(), true);
        }
    }

    private void saveToFileDup() {
        String filename = outputFileFieldDup.getText().trim();
        if (filename.isEmpty()) {
            setStatus(statusLabelDup, "Укажите имя файла.", true);
            return;
        }
        try {
            int[][] array = readTableToArray(outputTableDup, outputModelDup);
            if (array.length == 0) {
                setStatus(statusLabelDup, "Таблица результата пуста.", true);
                return;
            }
            TaskLogic.writeArrayToFile(filename, array);
            setStatus(statusLabelDup, "Сохранено в файл: " + filename, false);
        } catch (Exception ex) {
            setStatus(statusLabelDup, "Ошибка сохранения: " + ex.getMessage(), true);
        }
    }

    private void createEmptyTableDup() {
        try {
            int rows = Integer.parseInt(rowsFieldDup.getText().trim());
            int cols = Integer.parseInt(colsFieldDup.getText().trim());
            inputModelDup.setRowCount(rows);
            inputModelDup.setColumnCount(cols);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    inputModelDup.setValueAt("", r, c);
                }
            }
            outputModelDup.setRowCount(0);
            outputModelDup.setColumnCount(0);
            setStatus(statusLabelDup, "Создана таблица " + rows + "x" + cols + ". Введите данные.", false);
        } catch (NumberFormatException ex) {
            setStatus(statusLabelDup, "Ошибка: неверный формат числа строк/столбцов.", true);
        }
    }

    private void processDup() {
        try {
            int[][] input = readTableToArray(inputTableDup, inputModelDup);
            if (input.length == 0) {
                setStatus(statusLabelDup, "Входная таблица пуста.", true);
                return;
            }
            List<List<Integer>> processed = TaskLogic.processArrayRemoveDuplicates(input);
            int[][] outArray = new int[processed.size()][];
            for (int i = 0; i < processed.size(); i++) {
                List<Integer> row = processed.get(i);
                outArray[i] = new int[row.size()];
                for (int j = 0; j < row.size(); j++) {
                    outArray[i][j] = row.get(j);
                }
            }
            displayArrayInTable(outArray, outputModelDup);
            setStatus(statusLabelDup, "Обработка выполнена.", false);
        } catch (Exception ex) {
            setStatus(statusLabelDup, "Ошибка: " + ex.getMessage(), true);
        }
    }

    private void loadFromFileShift() {
        String filename = inputFileFieldShift.getText().trim();
        if (filename.isEmpty()) {
            setStatus(statusLabelShift, "Укажите имя файла.", true);
            return;
        }
        try {
            int[][] array = TaskLogic.readArrayFromFile(filename);
            displayArrayInTable(array, inputModelShift);
            outputModelShift.setRowCount(0);
            outputModelShift.setColumnCount(0);
            setStatus(statusLabelShift, "Загружено из файла: " + filename, false);
        } catch (Exception ex) {
            setStatus(statusLabelShift, "Ошибка загрузки: " + ex.getMessage(), true);
        }
    }

    private void saveToFileShift() {
        String filename = outputFileFieldShift.getText().trim();
        if (filename.isEmpty()) {
            setStatus(statusLabelShift, "Укажите имя файла.", true);
            return;
        }
        try {
            int[][] array = readTableToArray(outputTableShift, outputModelShift);
            if (array.length == 0) {
                setStatus(statusLabelShift, "Таблица результата пуста.", true);
                return;
            }
            TaskLogic.writeArrayToFile(filename, array);
            setStatus(statusLabelShift, "Сохранено в файл: " + filename, false);
        } catch (Exception ex) {
            setStatus(statusLabelShift, "Ошибка сохранения: " + ex.getMessage(), true);
        }
    }

    private void createEmptyTableShift() {
        try {
            int rows = Integer.parseInt(rowsFieldShift.getText().trim());
            int cols = Integer.parseInt(colsFieldShift.getText().trim());
            inputModelShift.setRowCount(rows);
            inputModelShift.setColumnCount(cols);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    inputModelShift.setValueAt("", r, c);
                }
            }
            outputModelShift.setRowCount(0);
            outputModelShift.setColumnCount(0);
            setStatus(statusLabelShift, "Создана таблица " + rows + "x" + cols + ". Введите данные.", false);
        } catch (NumberFormatException ex) {
            setStatus(statusLabelShift, "Ошибка: неверный формат числа строк/столбцов.", true);
        }
    }

    private void processShift() {
        try {
            int[][] input = readTableToArray(inputTableShift, inputModelShift);
            if (input.length == 0) {
                setStatus(statusLabelShift, "Входная таблица пуста.", true);
                return;
            }
            int n = (Integer) shiftSpinner.getValue();
            List<List<Integer>> processed = TaskLogic.processArrayShift(input, n);
            int[][] outArray = new int[processed.size()][];
            for (int i = 0; i < processed.size(); i++) {
                List<Integer> row = processed.get(i);
                outArray[i] = new int[row.size()];
                for (int j = 0; j < row.size(); j++) {
                    outArray[i][j] = row.get(j);
                }
            }
            displayArrayInTable(outArray, outputModelShift);
            setStatus(statusLabelShift, "Сдвиг на n=" + n + " выполнен.", false);
        } catch (Exception ex) {
            setStatus(statusLabelShift, "Ошибка: " + ex.getMessage(), true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                new WindowApp().setVisible(true);
            }
        });
    }
}
