import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class WindowApp extends JFrame {

    private static final String[] CIRCLE_COLS = {"X (центр)", "Y (центр)", "R (радиус)"};
    private static final String[] TRIANGLE_COLS = {"X1", "Y1", "X2", "Y2", "X3", "Y3"};

    private DefaultTableModel circleModel;
    private JTable circleTable;
    private JTextArea circleResultArea;
    private JTextField circleInputField;
    private JTextField circleOutputField;
    private JLabel circleStatus;

    private DefaultTableModel triangleModel;
    private JTable triangleTable;
    private JTextArea triangleResultArea;
    private JTextField triangleInputField;
    private JTextField triangleOutputField;
    private JLabel triangleStatus;

    public WindowApp() {
        super("Геометрические задачи");
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Окружности", buildCirclePanel());
        tabs.addTab("Треугольники (подобие)", buildTrianglePanel());
        setContentPane(tabs);
    }

    private JPanel buildCirclePanel() {
        JPanel main = new JPanel(new BorderLayout(6, 6));
        main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new GridLayout(3, 1, 3, 3));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r1.add(new JLabel("Входной файл:"));
        circleInputField = new JTextField("input01.txt", 20);
        r1.add(circleInputField);
        JButton loadBtn = new JButton("Загрузить");
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadCircles(); }
        });
        r1.add(loadBtn);
        JButton addRow = new JButton("+ Строка");
        addRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                circleModel.addRow(new Object[]{"", "", ""});
            }
        });
        r1.add(addRow);
        JButton delRow = new JButton("- Строка");
        delRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelectedRow(circleTable, circleModel); }
        });
        r1.add(delRow);
        top.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r2.add(new JLabel("Выходной файл:"));
        circleOutputField = new JTextField("output.txt", 20);
        r2.add(circleOutputField);
        JButton saveBtn = new JButton("Сохранить результат");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { saveCircleResult(); }
        });
        r2.add(saveBtn);
        top.add(r2);

        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton calcBtn = new JButton("Найти тройку с мин. периметром");
        calcBtn.setFont(calcBtn.getFont().deriveFont(Font.BOLD));
        calcBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { calculateCircles(); }
        });
        r3.add(calcBtn);
        JButton clearBtn = new JButton("Очистить");
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                circleModel.setRowCount(0);
                circleResultArea.setText("");
                setStatus(circleStatus, "Таблица очищена.", false);
            }
        });
        r3.add(clearBtn);
        top.add(r3);

        main.add(top, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.55);

        circleModel = new DefaultTableModel(CIRCLE_COLS, 5);
        circleTable = new JTable(circleModel);
        styleTable(circleTable);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Окружности (X, Y, R)"));
        leftPanel.add(new JScrollPane(circleTable));
        split.setLeftComponent(leftPanel);

        circleResultArea = new JTextArea();
        circleResultArea.setEditable(false);
        circleResultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        circleResultArea.setMargin(new Insets(6, 6, 6, 6));
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Результат"));
        rightPanel.add(new JScrollPane(circleResultArea));
        split.setRightComponent(rightPanel);

        main.add(split, BorderLayout.CENTER);

        circleStatus = new JLabel("Введите данные и нажмите кнопку.");
        circleStatus.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
        main.add(circleStatus, BorderLayout.SOUTH);

        return main;
    }

    private JPanel buildTrianglePanel() {
        JPanel main = new JPanel(new BorderLayout(6, 6));
        main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new GridLayout(3, 1, 3, 3));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r1.add(new JLabel("Входной файл:"));
        triangleInputField = new JTextField("input03.txt", 20);
        r1.add(triangleInputField);
        JButton loadBtn = new JButton("Загрузить");
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadTriangles(); }
        });
        r1.add(loadBtn);
        JButton addRow = new JButton("+ Строка");
        addRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                triangleModel.addRow(new Object[]{"", "", "", "", "", ""});
            }
        });
        r1.add(addRow);
        JButton delRow = new JButton("- Строка");
        delRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelectedRow(triangleTable, triangleModel); }
        });
        r1.add(delRow);
        top.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r2.add(new JLabel("Выходной файл:"));
        triangleOutputField = new JTextField("output.txt", 20);
        r2.add(triangleOutputField);
        JButton saveBtn = new JButton("Сохранить результат");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { saveTriangleResult(); }
        });
        r2.add(saveBtn);
        top.add(r2);

        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton calcBtn = new JButton("Разбить на подмножества подобных");
        calcBtn.setFont(calcBtn.getFont().deriveFont(Font.BOLD));
        calcBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { calculateTriangles(); }
        });
        r3.add(calcBtn);
        JButton clearBtn = new JButton("Очистить");
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                triangleModel.setRowCount(0);
                triangleResultArea.setText("");
                setStatus(triangleStatus, "Таблица очищена.", false);
            }
        });
        r3.add(clearBtn);
        top.add(r3);

        main.add(top, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.6);

        triangleModel = new DefaultTableModel(TRIANGLE_COLS, 5);
        triangleTable = new JTable(triangleModel);
        styleTable(triangleTable);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Треугольники (X1 Y1 X2 Y2 X3 Y3)"));
        leftPanel.add(new JScrollPane(triangleTable));
        split.setLeftComponent(leftPanel);

        triangleResultArea = new JTextArea();
        triangleResultArea.setEditable(false);
        triangleResultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        triangleResultArea.setMargin(new Insets(6, 6, 6, 6));
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Группы подобных треугольников"));
        rightPanel.add(new JScrollPane(triangleResultArea));
        split.setRightComponent(rightPanel);

        main.add(split, BorderLayout.CENTER);

        triangleStatus = new JLabel("Введите треугольники и нажмите кнопку.");
        triangleStatus.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
        main.add(triangleStatus, BorderLayout.SOUTH);

        return main;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void stopEditing(JTable table) {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
    }

    private void deleteSelectedRow(JTable table, DefaultTableModel model) {
        stopEditing(table);
        int row = table.getSelectedRow();
        if (row >= 0) model.removeRow(row);
    }

    private String[][] readCircleTable() {
        stopEditing(circleTable);
        int rows = circleModel.getRowCount();
        String[][] data = new String[rows][3];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 3; c++) {
                Object v = circleModel.getValueAt(r, c);
                data[r][c] = v == null ? "" : v.toString().trim();
            }
        }
        return data;
    }

    private String[][] readTriangleTable() {
        stopEditing(triangleTable);
        int rows = triangleModel.getRowCount();
        String[][] data = new String[rows][6];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 6; c++) {
                Object v = triangleModel.getValueAt(r, c);
                data[r][c] = v == null ? "" : v.toString().trim();
            }
        }
        return data;
    }

    private void loadCircles() {
        String filename = circleInputField.getText().trim();
        if (filename.isEmpty()) { setStatus(circleStatus, "Укажите имя файла.", true); return; }
        try {
            List<Circle> circles = TaskLogic.readCirclesFromFile(filename);
            String[][] data = TaskLogic.circlesToTable(circles);
            circleModel.setRowCount(0);
            for (int i = 0; i < data.length; i++) circleModel.addRow(data[i]);
            circleResultArea.setText("");
            setStatus(circleStatus, "Загружено " + circles.size() + " окружностей из: " + filename, false);
        } catch (Exception ex) {
            setStatus(circleStatus, "Ошибка загрузки: " + ex.getMessage(), true);
        }
    }

    private void calculateCircles() {
        String[][] data = readCircleTable();
        List<Circle> circles = TaskLogic.tableToCircles(data);
        if (circles.size() < 3) {
            setStatus(circleStatus, "Нужно минимум 3 корректные окружности, найдено: " + circles.size(), true);
            return;
        }
        CircleTriple result = TaskLogic.findMinPerimeterTriple(circles);
        circleResultArea.setText(result == null ? "Результат не найден." : result.toString());
        setStatus(circleStatus, "Обработано " + circles.size() + " окружностей.", false);
    }

    private void saveCircleResult() {
        String[][] data = readCircleTable();
        List<Circle> circles = TaskLogic.tableToCircles(data);
        CircleTriple result = circles.size() >= 3 ? TaskLogic.findMinPerimeterTriple(circles) : null;
        String filename = circleOutputField.getText().trim();
        if (filename.isEmpty()) { setStatus(circleStatus, "Укажите имя выходного файла.", true); return; }
        try {
            TaskLogic.writeResultToFile(filename, result);
            setStatus(circleStatus, "Результат сохранён в: " + filename, false);
        } catch (Exception ex) {
            setStatus(circleStatus, "Ошибка сохранения: " + ex.getMessage(), true);
        }
    }

    private void loadTriangles() {
        String filename = triangleInputField.getText().trim();
        if (filename.isEmpty()) { setStatus(triangleStatus, "Укажите имя файла.", true); return; }
        try {
            List<Triangle> triangles = TaskLogic.readTrianglesFromFile(filename);
            String[][] data = TaskLogic.trianglesToTable(triangles);
            triangleModel.setRowCount(0);
            for (int i = 0; i < data.length; i++) triangleModel.addRow(data[i]);
            triangleResultArea.setText("");
            setStatus(triangleStatus, "Загружено " + triangles.size() + " треугольников из: " + filename, false);
        } catch (Exception ex) {
            setStatus(triangleStatus, "Ошибка загрузки: " + ex.getMessage(), true);
        }
    }

    private void calculateTriangles() {
        String[][] data = readTriangleTable();
        List<Triangle> triangles = TaskLogic.tableToTriangles(data);
        if (triangles.isEmpty()) {
            setStatus(triangleStatus, "Нет корректных треугольников в таблице.", true);
            return;
        }
        List<List<Triangle>> groups = TaskLogic.groupBySimilarity(triangles);
        StringBuilder sb = new StringBuilder();
        sb.append("Треугольников: ").append(triangles.size())
          .append("   Групп подобных: ").append(groups.size()).append("\n\n");
        for (int g = 0; g < groups.size(); g++) {
            List<Triangle> group = groups.get(g);
            sb.append("Группа ").append(g + 1)
              .append(" (").append(group.size()).append(" треугольн.):\n");
            for (int i = 0; i < group.size(); i++) {
                Triangle t = group.get(i);
                sb.append(String.format("  %d. (%.2f,%.2f)-(%.2f,%.2f)-(%.2f,%.2f)  стороны: %.4f  %.4f  %.4f%n",
                    i + 1, t.x1, t.y1, t.x2, t.y2, t.x3, t.y3,
                    t.sideA(), t.sideB(), t.sideC()));
            }
            sb.append("\n");
        }
        triangleResultArea.setText(sb.toString());
        triangleResultArea.setCaretPosition(0);
        setStatus(triangleStatus, "Обработано " + triangles.size() + " треугольников, найдено " + groups.size() + " групп.", false);
    }

    private void saveTriangleResult() {
        String[][] data = readTriangleTable();
        List<Triangle> triangles = TaskLogic.tableToTriangles(data);
        String filename = triangleOutputField.getText().trim();
        if (filename.isEmpty()) { setStatus(triangleStatus, "Укажите имя выходного файла.", true); return; }
        if (triangles.isEmpty()) { setStatus(triangleStatus, "Нет данных для сохранения.", true); return; }
        try {
            List<List<Triangle>> groups = TaskLogic.groupBySimilarity(triangles);
            TaskLogic.writeGroupsToFile(filename, groups);
            setStatus(triangleStatus, "Результат сохранён в: " + filename, false);
        } catch (Exception ex) {
            setStatus(triangleStatus, "Ошибка сохранения: " + ex.getMessage(), true);
        }
    }

    private void setStatus(JLabel label, String msg, boolean isError) {
        label.setText(msg);
        label.setForeground(isError ? Color.RED : new Color(0, 120, 0));
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
