package ru.vsu.cs.course1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * Оконное приложение с двумя вкладками:
 *   1) Циклический сдвиг строк/столбцов int-массива (исходная задача)
 *   2) Поиск наибольшего окружённого прямоугольника в булевом массиве
 */
public class Task8GUI extends JFrame {

    // ── Вкладка 1: сдвиг ──
    private JTable shiftTable;
    private DefaultTableModel shiftTableModel;
    private JComboBox<String> directionCombo;
    private JSpinner shiftSpinner;
    private JSpinner shiftRowsSpinner;
    private JSpinner shiftColsSpinner;

    // ── Вкладка 2: прямоугольник ──
    private JTable rectTable;
    private DefaultTableModel rectTableModel;
    private JSpinner rectRowsSpinner;
    private JSpinner rectColsSpinner;
    private JLabel rectResultLabel;

    // Координаты найденного прямоугольника (для подсветки)
    private int foundRectRow = -1, foundRectCol = -1, foundRectH = -1, foundRectW = -1;

    public Task8GUI() {
        super("Двумерные массивы");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 560);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Сдвиг строк/столбцов", buildShiftPanel());
        tabs.addTab("Поиск прямоугольника", buildRectPanel());
        setContentPane(tabs);
    }

    // ================================================================
    //  ВКЛАДКА 1 — Циклический сдвиг
    // ================================================================

    private JPanel buildShiftPanel() {
        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верх: размер таблицы
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.add(new JLabel("Строки:"));
        shiftRowsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 100, 1));
        sizePanel.add(shiftRowsSpinner);
        sizePanel.add(new JLabel("  Столбцы:"));
        shiftColsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
        sizePanel.add(shiftColsSpinner);
        JButton resizeBtn = new JButton("Создать таблицу");
        resizeBtn.addActionListener(e -> resizeShiftTable());
        sizePanel.add(resizeBtn);
        main.add(sizePanel, BorderLayout.NORTH);

        // Центр: таблица
        shiftTableModel = new DefaultTableModel(3, 4);
        shiftTable = new JTable(shiftTableModel);
        shiftTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        main.add(new JScrollPane(shiftTable), BorderLayout.CENTER);

        // Низ: управление сдвигом + файловые кнопки
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JPanel shiftCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shiftCtrl.add(new JLabel("Направление:"));
        directionCombo = new JComboBox<>(new String[]{"ROWS (строки)", "COLS (столбцы)"});
        shiftCtrl.add(directionCombo);
        shiftCtrl.add(new JLabel("  Сдвиг n:"));
        shiftSpinner = new JSpinner(new SpinnerNumberModel(1, -1000, 1000, 1));
        shiftCtrl.add(shiftSpinner);
        JButton shiftBtn = new JButton("Выполнить сдвиг");
        shiftBtn.addActionListener(e -> performShift());
        shiftCtrl.add(shiftBtn);
        controlPanel.add(shiftCtrl);

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("Загрузить из файла");
        loadBtn.addActionListener(e -> loadShiftFromFile());
        filePanel.add(loadBtn);
        JButton saveBtn = new JButton("Сохранить в файл");
        saveBtn.addActionListener(e -> saveShiftToFile());
        filePanel.add(saveBtn);
        controlPanel.add(filePanel);

        main.add(controlPanel, BorderLayout.SOUTH);
        return main;
    }

    private void resizeShiftTable() {
        stopEditing(shiftTable);
        int rows = (int) shiftRowsSpinner.getValue();
        int cols = (int) shiftColsSpinner.getValue();
        shiftTableModel.setRowCount(rows);
        shiftTableModel.setColumnCount(cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (shiftTableModel.getValueAt(i, j) == null)
                    shiftTableModel.setValueAt(0, i, j);
    }

    private int[][] readShiftArray() {
        stopEditing(shiftTable);
        int rows = shiftTableModel.getRowCount();
        int cols = shiftTableModel.getColumnCount();
        int[][] array = new int[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                Object v = shiftTableModel.getValueAt(i, j);
                array[i][j] = (v == null || v.toString().trim().isEmpty())
                        ? 0 : Integer.parseInt(v.toString().trim());
            }
        return array;
    }

    private void displayShiftArray(int[][] array) {
        int rows = array.length;
        int cols = rows > 0 ? array[0].length : 0;
        shiftTableModel.setRowCount(rows);
        shiftTableModel.setColumnCount(cols);
        shiftRowsSpinner.setValue(rows);
        shiftColsSpinner.setValue(cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                shiftTableModel.setValueAt(array[i][j], i, j);
    }

    private void performShift() {
        try {
            int[][] array = readShiftArray();
            String direction = directionCombo.getSelectedIndex() == 0 ? "ROWS" : "COLS";
            int shift = (int) shiftSpinner.getValue();
            int[][] result = ArrayShiftUtils.cyclicShift(array, direction, shift);
            displayShiftArray(result);
            JOptionPane.showMessageDialog(this,
                    "Сдвиг выполнен: " + direction + " на " + shift,
                    "Готово", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка: некорректные числа в таблице.\n" + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadShiftFromFile() {
        JFileChooser fc = new JFileChooser(".");
        fc.setDialogTitle("Открыть входной файл");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            ArrayShiftUtils.ShiftTask task = ArrayShiftUtils.readFromFile(fc.getSelectedFile().getAbsolutePath());
            displayShiftArray(task.array);
            directionCombo.setSelectedIndex(task.direction.equals("ROWS") ? 0 : 1);
            shiftSpinner.setValue(task.shift);
            JOptionPane.showMessageDialog(this,
                    "Загружено из: " + fc.getSelectedFile().getName() +
                    "\nНаправление: " + task.direction + ", сдвиг: " + task.shift,
                    "Загружено", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка чтения файла:\n" + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveShiftToFile() {
        JFileChooser fc = new JFileChooser(".");
        fc.setDialogTitle("Сохранить в файл");
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            ArrayShiftUtils.writeToFile(fc.getSelectedFile().getAbsolutePath(), readShiftArray());
            JOptionPane.showMessageDialog(this,
                    "Сохранено в: " + fc.getSelectedFile().getName(),
                    "Сохранено", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка сохранения:\n" + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================================================================
    //  ВКЛАДКА 2 — Поиск наибольшего окружённого прямоугольника
    // ================================================================

    private JPanel buildRectPanel() {
        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верх: пояснение + размер
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel hint = new JLabel("<html><b>Клетки:</b> 0 = ложь (белая), 1 = истина (серая)." +
                "  Двойной клик или ввод 0/1 переключает значение.<br>" +
                "Алгоритм ищет наибольший прямоугольник из 1, окружённый 0 или границей.</html>");
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        topPanel.add(hint);

        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.add(new JLabel("Строки:"));
        rectRowsSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 100, 1));
        sizePanel.add(rectRowsSpinner);
        sizePanel.add(new JLabel("  Столбцы:"));
        rectColsSpinner = new JSpinner(new SpinnerNumberModel(9, 1, 100, 1));
        sizePanel.add(rectColsSpinner);
        JButton resizeBtn = new JButton("Создать таблицу");
        resizeBtn.addActionListener(e -> resizeRectTable());
        sizePanel.add(resizeBtn);
        JButton clearBtn = new JButton("Очистить (всё в 0)");
        clearBtn.addActionListener(e -> clearRectTable());
        sizePanel.add(clearBtn);
        topPanel.add(sizePanel);

        main.add(topPanel, BorderLayout.NORTH);

        // Центр: таблица с подсветкой
        rectTableModel = new DefaultTableModel(7, 9) {
            @Override public Class<?> getColumnClass(int col) { return String.class; }
        };
        rectTable = new JTable(rectTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                Object v = getModel().getValueAt(row, col);
                boolean isOne = "1".equals(v != null ? v.toString().trim() : "");
                boolean inRect = foundRectRow >= 0
                        && row >= foundRectRow && row < foundRectRow + foundRectH
                        && col >= foundRectCol && col < foundRectCol + foundRectW;

                if (inRect) {
                    c.setBackground(new Color(255, 140, 0));   // оранжевый — найденный прямоугольник
                    c.setForeground(Color.WHITE);
                } else if (isOne) {
                    c.setBackground(new Color(120, 120, 120)); // зелёный — истина
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.DARK_GRAY);
                }
                return c;
            }
        };
        rectTable.setRowHeight(28);
        rectTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        rectTable.setGridColor(Color.LIGHT_GRAY);

        // Переключение по двойному клику мышью
        rectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = rectTable.rowAtPoint(e.getPoint());
                    int col = rectTable.columnAtPoint(e.getPoint());
                    if (row >= 0 && col >= 0) toggleCell(row, col);
                }
            }
        });

        // Выравнивание по центру
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        rectTable.setDefaultRenderer(Object.class, center);

        fillRectTableWithZeros();
        main.add(new JScrollPane(rectTable), BorderLayout.CENTER);

        // Низ: кнопки + результат
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton findBtn = new JButton("Найти прямоугольник");
        findBtn.setFont(findBtn.getFont().deriveFont(Font.BOLD));
        findBtn.addActionListener(e -> findRectangle());
        btnPanel.add(findBtn);

        JButton resetBtn = new JButton("Сбросить подсветку");
        resetBtn.addActionListener(e -> {
            foundRectRow = foundRectCol = foundRectH = foundRectW = -1;
            rectResultLabel.setText("Подсветка сброшена.");
            rectTable.repaint();
        });
        btnPanel.add(resetBtn);

        JButton loadBtn2 = new JButton("Загрузить из файла (0/1)");
        loadBtn2.addActionListener(e -> loadRectFromFile());
        btnPanel.add(loadBtn2);

        south.add(btnPanel);

        rectResultLabel = new JLabel("  Нажмите «Найти прямоугольник» для запуска.");
        rectResultLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));
        rectResultLabel.setFont(rectResultLabel.getFont().deriveFont(13f));
        south.add(rectResultLabel);

        main.add(south, BorderLayout.SOUTH);
        return main;
    }

    /** Переключает ячейку между 0 и 1. */
    private void toggleCell(int row, int col) {
        Object v = rectTableModel.getValueAt(row, col);
        String cur = (v == null) ? "0" : v.toString().trim();
        rectTableModel.setValueAt("1".equals(cur) ? "0" : "1", row, col);
        // Сбрасываем подсветку при изменении массива
        foundRectRow = foundRectCol = foundRectH = foundRectW = -1;
        rectResultLabel.setText("Массив изменён. Нажмите «Найти прямоугольник».");
        rectTable.repaint();
    }

    private void resizeRectTable() {
        stopEditing(rectTable);
        int rows = (int) rectRowsSpinner.getValue();
        int cols = (int) rectColsSpinner.getValue();
        rectTableModel.setRowCount(rows);
        rectTableModel.setColumnCount(cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (rectTableModel.getValueAt(i, j) == null)
                    rectTableModel.setValueAt("0", i, j);
        foundRectRow = foundRectCol = foundRectH = foundRectW = -1;
        rectResultLabel.setText("Таблица пересоздана. Введите данные и нажмите «Найти прямоугольник».");
        rectTable.repaint();
    }

    private void clearRectTable() {
        stopEditing(rectTable);
        int rows = rectTableModel.getRowCount();
        int cols = rectTableModel.getColumnCount();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                rectTableModel.setValueAt("0", i, j);
        foundRectRow = foundRectCol = foundRectH = foundRectW = -1;
        rectResultLabel.setText("Таблица очищена.");
        rectTable.repaint();
    }

    private void fillRectTableWithZeros() {
        int rows = rectTableModel.getRowCount();
        int cols = rectTableModel.getColumnCount();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                rectTableModel.setValueAt("0", i, j);
    }

    /** Читает булев массив из таблицы. */
    private boolean[][] readRectGrid() {
        stopEditing(rectTable);
        int rows = rectTableModel.getRowCount();
        int cols = rectTableModel.getColumnCount();
        boolean[][] grid = new boolean[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                Object v = rectTableModel.getValueAt(i, j);
                grid[i][j] = "1".equals(v != null ? v.toString().trim() : "0");
            }
        return grid;
    }

    private void findRectangle() {
        try {
            boolean[][] grid = readRectGrid();
            int[] result = RectangleFinder.findLargestSurroundedRectangle(grid);

            if (result[0] == -1) {
                foundRectRow = foundRectCol = foundRectH = foundRectW = -1;
                rectResultLabel.setText("Результат: прямоугольник не найден  →  (-1, -1, -1, -1)");
            } else {
                foundRectRow = result[0];
                foundRectCol = result[1];
                foundRectH   = result[2];
                foundRectW   = result[3];
                int area = result[2] * result[3];
                rectResultLabel.setText(String.format(
                        "<html>Результат: строка = <b>%d</b>, столбец = <b>%d</b>," +
                        " высота = <b>%d</b>, ширина = <b>%d</b>," +
                        " площадь = <b>%d</b>   (прямоугольник выделен оранжевым)</html>",
                        result[0], result[1], result[2], result[3], area));
            }
            rectTable.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка: в таблице допустимы только 0 и 1.\n" + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Загрузка булевой матрицы из текстового файла.
     * Формат: строки из 0 и 1 через пробел.
     */
    private void loadRectFromFile() {
        JFileChooser fc = new JFileChooser(".");
        fc.setDialogTitle("Открыть файл с булевой матрицей");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            java.util.List<String[]> lines = new java.util.ArrayList<>();
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.FileReader(fc.getSelectedFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) lines.add(line.split("\\s+"));
                }
            }
            if (lines.isEmpty()) throw new Exception("Файл пуст");
            int rows = lines.size();
            int cols = lines.get(0).length;

            rectTableModel.setRowCount(rows);
            rectTableModel.setColumnCount(cols);
            rectRowsSpinner.setValue(rows);
            rectColsSpinner.setValue(cols);

            for (int i = 0; i < rows; i++) {
                String[] parts = lines.get(i);
                for (int j = 0; j < cols; j++) {
                    String val = (j < parts.length) ? parts[j].trim() : "0";
                    if (!val.equals("0") && !val.equals("1"))
                        throw new Exception("Допустимы только 0 и 1, найдено: " + val);
                    rectTableModel.setValueAt(val, i, j);
                }
            }

            foundRectRow = foundRectCol = foundRectH = foundRectW = -1;
            rectResultLabel.setText("Загружено из: " + fc.getSelectedFile().getName() +
                    ". Нажмите «Найти прямоугольник».");
            rectTable.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка чтения файла:\n" + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ======================== Утилиты ========================

    private void stopEditing(JTable table) {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
    }

    // ======================== Точка входа ========================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new Task8GUI().setVisible(true);
        });
    }
}
