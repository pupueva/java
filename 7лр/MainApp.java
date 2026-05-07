import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;

/**
 * GUI-приложение (Java 8+):
 *   Вкладка 1 — Римские <-> Арабские  (класс RomanConverter)
 *   Вкладка 2 — Слова заданной длины   (класс WordExtractor)
 *   Вкладка 3 — Тесты (44 теста, результат -> test_data/output.txt)
 *
 * Каждая рабочая вкладка: ручной ввод / загрузка из файла / сохранение в файл.
 * 5 тестовых входных файлов + 1 выходной создаются автоматически в test_data/.
 *
 * Запуск:   java -jar MainApp.jar   (или двойной клик по .jar)
 * Из исходников (JDK 8+): javac -encoding UTF-8 MainApp.java
 *                          jar cfe MainApp.jar MainApp *.class
 */
public class MainApp {



    static class RomanConverter {

        private static final int[]    ARAB_VALUES  = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        private static final String[] ROMAN_GLYPHS = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};

        private static final Map<Character, Integer> ROMAN_MAP = new HashMap<Character, Integer>();
        static {
            ROMAN_MAP.put('I', 1);   ROMAN_MAP.put('V', 5);
            ROMAN_MAP.put('X', 10);  ROMAN_MAP.put('L', 50);
            ROMAN_MAP.put('C', 100); ROMAN_MAP.put('D', 500);
            ROMAN_MAP.put('M', 1000);
        }


        private static final Pattern VALID_ROMAN =
                Pattern.compile("^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$");


        public static String toRoman(int number) {
            if (number < 1 || number > 3999)
                throw new IllegalArgumentException(
                        "\u0427\u0438\u0441\u043B\u043E \u0434\u043E\u043B\u0436\u043D\u043E \u0431\u044B\u0442\u044C \u0432 \u0434\u0438\u0430\u043F\u0430\u0437\u043E\u043D\u0435 1-3999, \u043F\u043E\u043B\u0443\u0447\u0435\u043D\u043E: " + number);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ARAB_VALUES.length; i++) {
                while (number >= ARAB_VALUES[i]) {
                    sb.append(ROMAN_GLYPHS[i]);
                    number -= ARAB_VALUES[i];
                }
            }
            return sb.toString();
        }


        public static int toArabic(String roman) {
            if (roman == null || roman.trim().isEmpty())
                throw new IllegalArgumentException("\u0421\u0442\u0440\u043E\u043A\u0430 \u043D\u0435 \u043C\u043E\u0436\u0435\u0442 \u0431\u044B\u0442\u044C \u043F\u0443\u0441\u0442\u043E\u0439");
            String upper = roman.trim().toUpperCase();
            if (!VALID_ROMAN.matcher(upper).matches())
                throw new IllegalArgumentException("\u041D\u0435\u043A\u043E\u0440\u0440\u0435\u043A\u0442\u043D\u043E\u0435 \u0440\u0438\u043C\u0441\u043A\u043E\u0435 \u0447\u0438\u0441\u043B\u043E: " + roman.trim());
            int result = 0, prev = 0;
            for (int i = upper.length() - 1; i >= 0; i--) {
                int curr = ROMAN_MAP.get(upper.charAt(i));
                result += (curr < prev) ? -curr : curr;
                prev = curr;
            }
            return result;
        }
    }



    static class WordExtractor {

        private static final Pattern WORD_PATTERN =
                Pattern.compile("[A-Za-z\u0410-\u042F\u0430-\u044F\u0401\u04510-9]+");

        public static List<String> extract(String text, int length) {
            if (text == null || length < 1)
                return Collections.emptyList();
            Set<String> seen = new LinkedHashSet<String>();
            Matcher m = WORD_PATTERN.matcher(text);
            while (m.find()) {
                String word = m.group();
                if (word.length() == length)
                    seen.add(word);
            }
            return new ArrayList<String>(seen);
        }
    }



    private static final String TEST_DIR = "test_data";

    private static void writeFile(String dir, String name, String content) throws IOException {
        File d = new File(dir);
        if (!d.exists()) d.mkdirs();
        try (Writer w = new OutputStreamWriter(
                new FileOutputStream(new File(d, name)), StandardCharsets.UTF_8)) {
            w.write(content);
        }
    }

    private static String readFile(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int off = 0;
            while (off < bytes.length) {
                int n = fis.read(bytes, off, bytes.length - off);
                if (n < 0) break;
                off += n;
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null)
                lines.add(line);
        }
        return lines;
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... items) {
        return Collections.unmodifiableList(Arrays.asList(items));
    }


    private static void createTestFiles() {
        try {
            writeFile(TEST_DIR, "test1_roman.txt",
                    "1\n4\n9\n42\n99\n444\n1994\n2026\n3999\n");

            writeFile(TEST_DIR, "test2_arabic.txt",
                    "I\nIV\nIX\nXLII\nXCIX\nCDXLIV\nMCMXCIV\nMMXXVI\nMMMCMXCIX\n");

            writeFile(TEST_DIR, "test3_words.txt",
                    "3\nThe cat sat on the mat and the cat was fat.\n\u041A\u043E\u0442 \u0441\u0435\u043B \u043F\u043E\u0434 \u0441\u0442\u043E\u043B, \u0430 \u041A\u043E\u0442 \u0443\u0448\u0451\u043B \u0434\u043E\u043C\u043E\u0439.\n");

            writeFile(TEST_DIR, "test4_edge.txt",
                    "1\n3999\nI\nMMMCMXCIX\nMCMLXXVII\nCDXLIV\n");

            writeFile(TEST_DIR, "test5_mixed.txt",
                    "4\n\u0401\u043B\u043A\u0430 \u0441\u0442\u043E\u0438\u0442, \u0451\u0436\u0438\u043A \u0431\u0435\u0436\u0438\u0442! Hello world \u0442\u0435\u0441\u0442123 \u0401\u043B\u043A\u0430.\n\u0420\u0430\u0437,\u0434\u0432\u0430\t\u0442\u0440\u0438\n\u0447\u0435\u0442\u044B\u0440\u0435...\u043F\u044F\u0442\u044C 12345 a1b2\n");

            writeFile(TEST_DIR, "output.txt", "");
        } catch (IOException e) {
            System.err.println("Error creating test files: " + e.getMessage());
        }
    }

    private static int passed, failed;

    private static void check(Object exp, Object act, String name, StringBuilder log) {
        if (Objects.equals(exp, act)) {
            passed++;
            log.append("  \u2713 ").append(name).append("\n");
        } else {
            failed++;
            log.append("  \u2717 ").append(name)
               .append("  \u043E\u0436\u0438\u0434\u0430\u043B\u043E\u0441\u044C: ").append(exp)
               .append(", \u043F\u043E\u043B\u0443\u0447\u0435\u043D\u043E: ").append(act).append("\n");
        }
    }

    private static void checkThrows(Runnable r, String name, StringBuilder log) {
        try { r.run(); failed++;
            log.append("  \u2717 ").append(name).append(" \u2014 \u0438\u0441\u043A\u043B\u044E\u0447\u0435\u043D\u0438\u0435 \u043D\u0435 \u0432\u044B\u0431\u0440\u043E\u0448\u0435\u043D\u043E\n");
        } catch (Exception e) { passed++;
            log.append("  \u2713 ").append(name).append("  (").append(e.getMessage()).append(")\n");
        }
    }

    public static String runAllTests() {
        passed = 0; failed = 0;
        StringBuilder log = new StringBuilder();

        log.append("\n RomanConverter.toRoman \n");
        check("I",         RomanConverter.toRoman(1),    "1 -> I", log);
        check("IV",        RomanConverter.toRoman(4),    "4 -> IV", log);
        check("IX",        RomanConverter.toRoman(9),    "9 -> IX", log);
        check("XLII",      RomanConverter.toRoman(42),   "42 -> XLII", log);
        check("XCIX",      RomanConverter.toRoman(99),   "99 -> XCIX", log);
        check("CDXLIV",    RomanConverter.toRoman(444),  "444 -> CDXLIV", log);
        check("MCMXCIV",   RomanConverter.toRoman(1994), "1994 -> MCMXCIV", log);
        check("MMXXVI",    RomanConverter.toRoman(2026), "2026 -> MMXXVI", log);
        check("MMMCMXCIX", RomanConverter.toRoman(3999), "3999 -> MMMCMXCIX", log);

        log.append("\n \u0413\u0440\u0430\u043D\u0438\u0447\u043D\u044B\u0435 / \u043E\u0448\u0438\u0431\u043E\u0447\u043D\u044B\u0435 \n");
        checkThrows(() -> RomanConverter.toRoman(0),    "0 -> exception", log);
        checkThrows(() -> RomanConverter.toRoman(-1),   "-1 -> exception", log);
        checkThrows(() -> RomanConverter.toRoman(4000), "4000 -> exception", log);

        log.append("\n RomanConverter.toArabic \n");
        check(1,    RomanConverter.toArabic("I"),         "I -> 1", log);
        check(4,    RomanConverter.toArabic("IV"),        "IV -> 4", log);
        check(9,    RomanConverter.toArabic("IX"),        "IX -> 9", log);
        check(58,   RomanConverter.toArabic("LVIII"),     "LVIII -> 58", log);
        check(1994, RomanConverter.toArabic("MCMXCIV"),   "MCMXCIV -> 1994", log);
        check(3999, RomanConverter.toArabic("MMMCMXCIX"), "MMMCMXCIX -> 3999", log);
        check(14,   RomanConverter.toArabic("xiv"),       "xiv -> 14 (lowercase)", log);
        check(2888, RomanConverter.toArabic("MMDCCCLXXXVIII"), "MMDCCCLXXXVIII -> 2888", log);

        log.append("\n \u041D\u0435\u043A\u043E\u0440\u0440\u0435\u043A\u0442\u043D\u044B\u0435 \n");
        checkThrows(() -> RomanConverter.toArabic(""),     "empty", log);
        checkThrows(() -> RomanConverter.toArabic(null),   "null", log);
        checkThrows(() -> RomanConverter.toArabic("IIII"), "IIII", log);
        checkThrows(() -> RomanConverter.toArabic("VV"),   "VV", log);
        checkThrows(() -> RomanConverter.toArabic("IC"),   "IC", log);
        checkThrows(() -> RomanConverter.toArabic("ABC"),  "ABC", log);
        checkThrows(() -> RomanConverter.toArabic("MMMM"), "MMMM", log);

        log.append("\n Round-trip 1..3999 \n");
        boolean ok = true;
        for (int i = 1; i <= 3999; i++) {
            if (RomanConverter.toArabic(RomanConverter.toRoman(i)) != i) {
                ok = false; failed++;
                log.append("  \u2717 round-trip fail at ").append(i).append("\n");
                break;
            }
        }
        if (ok) { passed++; log.append("  \u2713 All 3999 numbers OK\n"); }

        log.append("\n WordExtractor.extract \n");

        check(listOf("is", "of"),
              WordExtractor.extract("This is a test of the system!", 2),
              "eng words len=2", log);

        check(listOf("\u041A\u043E\u0442", "\u0441\u0435\u043B", "\u043F\u043E\u0434"),
              WordExtractor.extract("\u041A\u043E\u0442 \u0441\u0435\u043B \u043F\u043E\u0434 \u0441\u0442\u043E\u043B, \u0430 \u041A\u043E\u0442 \u0443\u0448\u0451\u043B.", 3),
              "rus words len=3 no dupes", log);

        check(listOf("ab", "12"),
              WordExtractor.extract("ab 12 ab", 2),
              "latin+digits len=2 no dupes", log);

        check(Collections.emptyList(),
              WordExtractor.extract("Hi! Go? OK.", 5),
              "no words len=5", log);

        check(Collections.emptyList(), WordExtractor.extract("", 3),   "empty string", log);
        check(Collections.emptyList(), WordExtractor.extract(null, 3), "null", log);

        check(listOf("7", "3"),
              WordExtractor.extract("7 \u043F\u043B\u044E\u0441 3 \u0440\u0430\u0432\u043D\u043E 10", 1),
              "digits len=1", log);

        check(listOf("\u0420\u0430\u0437", "\u0434\u0432\u0430", "\u0442\u0440\u0438"),
              WordExtractor.extract("\u0420\u0430\u0437,\u0434\u0432\u0430\t\u0442\u0440\u0438\n\u0420\u0430\u0437!", 3),
              "separators: comma tab newline", log);

        check(listOf("\u0401\u043B\u043A\u0430", "\u0451\u0436\u0438\u043A", "\u0451\u043B\u043A\u0438"),
              WordExtractor.extract("\u0401\u043B\u043A\u0430 \u0438 \u0451\u0436\u0438\u043A \u0443 \u0451\u043B\u043A\u0438", 4),
              "words with YO len=4", log);

        log.append("\n File-based tests \n");
        try {
            List<String> l1 = readLines(new File(TEST_DIR, "test1_roman.txt"));
            StringBuilder r1 = new StringBuilder();
            for (String line : l1) { String s=line.trim(); if(!s.isEmpty()) { int n=Integer.parseInt(s); r1.append(n).append(" -> ").append(RomanConverter.toRoman(n)).append("\n"); }}
            check(true, r1.toString().contains("1994 -> MCMXCIV"), "test1: 1994->MCMXCIV", log);

            List<String> l2 = readLines(new File(TEST_DIR, "test2_arabic.txt"));
            StringBuilder r2 = new StringBuilder();
            for (String line : l2) { String s=line.trim(); if(!s.isEmpty()) r2.append(s).append(" -> ").append(RomanConverter.toArabic(s)).append("\n"); }
            check(true, r2.toString().contains("MCMXCIV -> 1994"), "test2: MCMXCIV->1994", log);

            List<String> l3 = readLines(new File(TEST_DIR, "test3_words.txt"));
            int len3 = Integer.parseInt(l3.get(0).trim());
            StringBuilder t3 = new StringBuilder();
            for (int i=1; i<l3.size(); i++) { if(i>1) t3.append("\n"); t3.append(l3.get(i)); }
            List<String> w3 = WordExtractor.extract(t3.toString(), len3);
            check(true, w3.contains("cat"), "test3: 'cat' len=3", log);
            check(true, w3.contains("\u041A\u043E\u0442"), "test3: '\u041A\u043E\u0442' len=3", log);

            List<String> l5 = readLines(new File(TEST_DIR, "test5_mixed.txt"));
            int len5 = Integer.parseInt(l5.get(0).trim());
            StringBuilder t5 = new StringBuilder();
            for (int i=1; i<l5.size(); i++) { if(i>1) t5.append("\n"); t5.append(l5.get(i)); }
            List<String> w5 = WordExtractor.extract(t5.toString(), len5);
            check(true, w5.contains("\u0401\u043B\u043A\u0430"), "test5: '\u0401\u043B\u043A\u0430' len=4", log);
            check(true, w5.contains("\u0451\u0436\u0438\u043A"), "test5: '\u0451\u0436\u0438\u043A' len=4", log);
            check(true, w5.contains("a1b2"), "test5: 'a1b2' len=4", log);
        } catch (Exception e) {
            failed++;
            log.append("  \u2717 File test error: ").append(e.getMessage()).append("\n");
        }

        log.append("\n\n");
        log.append("  TOTAL passed: ").append(passed).append(", failed: ").append(failed).append("\n");
        log.append("\n");

        try { writeFile(TEST_DIR, "output.txt", log.toString()); }
        catch (IOException e) { log.append("\nCannot write output.txt: ").append(e.getMessage()).append("\n"); }

        return log.toString();
    }


    public static void main(String[] args) {
        createTestFiles();
        System.out.println(runAllTests());
        SwingUtilities.invokeLater(MainApp::buildGUI);
    }

    private static void buildGUI() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        JFrame frame = new JFrame("\u0420\u0438\u043C\u0441\u043A\u0438\u0435 \u0447\u0438\u0441\u043B\u0430 & \u0418\u0437\u0432\u043B\u0435\u0447\u0435\u043D\u0438\u0435 \u0441\u043B\u043E\u0432");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(820, 640));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.addTab("\u0420\u0438\u043C\u0441\u043A\u0438\u0435 \u2194 \u0410\u0440\u0430\u0431\u0441\u043A\u0438\u0435", buildRomanTab());
        tabs.addTab("\u0421\u043B\u043E\u0432\u0430 \u043F\u043E \u0434\u043B\u0438\u043D\u0435", buildWordsTab());
        tabs.addTab("\u0422\u0435\u0441\u0442\u044B", buildTestsTab());

        frame.add(tabs);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private static JPanel buildRomanTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12,12,12,12));

        JPanel fileBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnOpen = new JButton("\u041E\u0442\u043A\u0440\u044B\u0442\u044C \u0444\u0430\u0439\u043B");
        JButton btnSave = new JButton("\u0421\u043E\u0445\u0440\u0430\u043D\u0438\u0442\u044C \u0440\u0435\u0437\u0443\u043B\u044C\u0442\u0430\u0442");
        fileBar.add(btnOpen); fileBar.add(btnSave);
        fileBar.add(new JLabel("   \u0424\u043E\u0440\u043C\u0430\u0442: \u043F\u043E \u043E\u0434\u043D\u043E\u043C\u0443 \u0447\u0438\u0441\u043B\u0443 \u043D\u0430 \u0441\u0442\u0440\u043E\u043A\u0443"));
        root.add(fileBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel left = new JPanel(new BorderLayout(5,5));
        left.setBorder(BorderFactory.createTitledBorder("\u0412\u0445\u043E\u0434\u043D\u044B\u0435 \u0434\u0430\u043D\u043D\u044B\u0435"));
        JTextArea inArea = new JTextArea(15, 28);
        inArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        inArea.setText("1\n4\n9\n42\nXCIX\nMCMXCIV\n3999\nMMMCMXCIX");
        left.add(new JScrollPane(inArea), BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton bToR = new JButton("\u0410\u0440\u0430\u0431\u0441\u043A\u0438\u0435 -> \u0420\u0438\u043C\u0441\u043A\u0438\u0435");
        JButton bToA = new JButton("\u0420\u0438\u043C\u0441\u043A\u0438\u0435 -> \u0410\u0440\u0430\u0431\u0441\u043A\u0438\u0435");
        bToR.setFont(new Font("SansSerif", Font.BOLD, 13));
        bToA.setFont(new Font("SansSerif", Font.BOLD, 13));
        bp.add(bToR); bp.add(bToA);
        left.add(bp, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(5,5));
        right.setBorder(BorderFactory.createTitledBorder("\u0420\u0435\u0437\u0443\u043B\u044C\u0442\u0430\u0442"));
        JTextArea outArea = new JTextArea(15, 28);
        outArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        outArea.setEditable(false);
        outArea.setBackground(new Color(245,248,245));
        right.add(new JScrollPane(outArea), BorderLayout.CENTER);

        center.add(left); center.add(right);
        root.add(center, BorderLayout.CENTER);

        bToR.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (String line : inArea.getText().split("\\r?\\n")) {
                String s = line.trim(); if (s.isEmpty()) continue;
                try { int n = Integer.parseInt(s); sb.append(n).append(" -> ").append(RomanConverter.toRoman(n)).append("\n");
                } catch (NumberFormatException ex) { sb.append(s).append(" \u2014 not a number\n");
                } catch (IllegalArgumentException ex) { sb.append(s).append(" \u2014 ").append(ex.getMessage()).append("\n"); }
            } outArea.setText(sb.toString());
        });

        bToA.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (String line : inArea.getText().split("\\r?\\n")) {
                String s = line.trim(); if (s.isEmpty()) continue;
                try { sb.append(s).append(" -> ").append(RomanConverter.toArabic(s)).append("\n");
                } catch (IllegalArgumentException ex) { sb.append(s).append(" \u2014 ").append(ex.getMessage()).append("\n"); }
            } outArea.setText(sb.toString());
        });

        btnOpen.addActionListener(e -> {
            JFileChooser fc = makeChooser();
            if (fc.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
                try { inArea.setText(readFile(fc.getSelectedFile())); }
                catch (IOException ex) { showError(root, ex.getMessage()); }
            }
        });

        btnSave.addActionListener(e -> {
            JFileChooser fc = makeChooser(); fc.setSelectedFile(new File("roman_result.txt"));
            if (fc.showSaveDialog(root) == JFileChooser.APPROVE_OPTION) {
                try { writeFile(fc.getSelectedFile().getParent(), fc.getSelectedFile().getName(), outArea.getText());
                    showInfo(root, "\u0421\u043E\u0445\u0440\u0430\u043D\u0435\u043D\u043E: " + fc.getSelectedFile().getAbsolutePath());
                } catch (IOException ex) { showError(root, ex.getMessage()); }
            }
        });

        return root;
    }


    private static JPanel buildWordsTab() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(12,12,12,12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnOpen = new JButton("\u041E\u0442\u043A\u0440\u044B\u0442\u044C \u0444\u0430\u0439\u043B");
        JButton btnSave = new JButton("\u0421\u043E\u0445\u0440\u0430\u043D\u0438\u0442\u044C \u0440\u0435\u0437\u0443\u043B\u044C\u0442\u0430\u0442");
        top.add(btnOpen); top.add(btnSave);
        top.add(Box.createHorizontalStrut(20));
        top.add(new JLabel("\u0414\u043B\u0438\u043D\u0430 \u0441\u043B\u043E\u0432\u0430:"));
        JSpinner sp = new JSpinner(new SpinnerNumberModel(3,1,100,1));
        sp.setFont(new Font("Monospaced", Font.PLAIN, 14));
        top.add(sp);
        JButton btnFind = new JButton("\u041D\u0430\u0439\u0442\u0438 \u0441\u043B\u043E\u0432\u0430");
        btnFind.setFont(new Font("SansSerif", Font.BOLD, 13));
        top.add(btnFind);
        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,2,10,0));

        JPanel lp = new JPanel(new BorderLayout(5,5));
        lp.setBorder(BorderFactory.createTitledBorder("\u0412\u0445\u043E\u0434\u043D\u043E\u0439 \u0442\u0435\u043A\u0441\u0442 (\u0444\u0430\u0439\u043B: 1-\u044F \u0441\u0442\u0440\u043E\u043A\u0430 = \u0434\u043B\u0438\u043D\u0430)"));
        JTextArea inArea = new JTextArea(15, 28);
        inArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inArea.setLineWrap(true); inArea.setWrapStyleWord(true);
        inArea.setText("\u0401\u043B\u043A\u0430 \u0441\u0442\u043E\u0438\u0442 \u0443 \u0434\u043E\u043C\u0430. The cat sat on the mat.\n\u041A\u043E\u04422 \u0438 \u0401\u0436 \u2014 \u0434\u0440\u0443\u0437\u044C\u044F!");
        lp.add(new JScrollPane(inArea), BorderLayout.CENTER);

        JPanel rp = new JPanel(new BorderLayout(5,5));
        rp.setBorder(BorderFactory.createTitledBorder("\u041D\u0430\u0439\u0434\u0435\u043D\u043D\u044B\u0435 \u0441\u043B\u043E\u0432\u0430 (\u0431\u0435\u0437 \u043F\u043E\u0432\u0442\u043E\u0440\u0435\u043D\u0438\u0439)"));
        JTextArea outArea = new JTextArea(15, 28);
        outArea.setEditable(false);
        outArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outArea.setBackground(new Color(245,245,250));
        rp.add(new JScrollPane(outArea), BorderLayout.CENTER);

        center.add(lp); center.add(rp);
        root.add(center, BorderLayout.CENTER);

        btnFind.addActionListener(e -> {
            int len = (int)sp.getValue();
            List<String> words = WordExtractor.extract(inArea.getText(), len);
            StringBuilder sb = new StringBuilder();
            sb.append("\u0414\u043B\u0438\u043D\u0430: ").append(len).append("   \u041D\u0430\u0439\u0434\u0435\u043D\u043E: ").append(words.size()).append("\n");
            sb.append("-\n");
            for (int i=0; i<words.size(); i++) sb.append(String.format("%3d. %s%n", i+1, words.get(i)));
            if (words.isEmpty()) sb.append("(\u043D\u0435\u0442 \u0441\u043B\u043E\u0432 \u0442\u0430\u043A\u043E\u0439 \u0434\u043B\u0438\u043D\u044B)\n");
            outArea.setText(sb.toString());
        });

        btnOpen.addActionListener(e -> {
            JFileChooser fc = makeChooser();
            if (fc.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
                try {
                    String content = readFile(fc.getSelectedFile());
                    String[] parts = content.split("\\r?\\n", 2);
                    try { int fl = Integer.parseInt(parts[0].trim()); sp.setValue(fl);
                        inArea.setText(parts.length > 1 ? parts[1] : "");
                    } catch (NumberFormatException ex) { inArea.setText(content); }
                } catch (IOException ex) { showError(root, ex.getMessage()); }
            }
        });

        btnSave.addActionListener(e -> {
            JFileChooser fc = makeChooser(); fc.setSelectedFile(new File("words_result.txt"));
            if (fc.showSaveDialog(root) == JFileChooser.APPROVE_OPTION) {
                try { writeFile(fc.getSelectedFile().getParent(), fc.getSelectedFile().getName(), outArea.getText());
                    showInfo(root, "\u0421\u043E\u0445\u0440\u0430\u043D\u0435\u043D\u043E: " + fc.getSelectedFile().getAbsolutePath());
                } catch (IOException ex) { showError(root, ex.getMessage()); }
            }
        });

        return root;
    }



    private static JPanel buildTestsTab() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(12,12,12,12));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnRun = new JButton("\u0417\u0430\u043F\u0443\u0441\u0442\u0438\u0442\u044C \u0432\u0441\u0435 \u0442\u0435\u0441\u0442\u044B");
        btnRun.setFont(new Font("SansSerif", Font.BOLD, 14));
        JButton btnDir = new JButton("\u041E\u0442\u043A\u0440\u044B\u0442\u044C test_data");
        JButton btnOut = new JButton("\u041E\u0442\u043A\u0440\u044B\u0442\u044C output.txt");
        topBar.add(btnRun); topBar.add(btnDir); topBar.add(btnOut);
        topBar.add(new JLabel("   \u0420\u0435\u0437\u0443\u043B\u044C\u0442\u0430\u0442 \u2192 test_data/output.txt"));
        root.add(topBar, BorderLayout.NORTH);

        JTextArea area = new JTextArea(18, 65);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        root.add(new JScrollPane(area), BorderLayout.CENTER);

        JTextArea filesArea = new JTextArea(8, 65);
        filesArea.setEditable(false);
        filesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        filesArea.setBackground(new Color(250,250,240));
        JScrollPane fScroll = new JScrollPane(filesArea);
        fScroll.setBorder(BorderFactory.createTitledBorder("5 \u0442\u0435\u0441\u0442\u043E\u0432\u044B\u0445 \u0444\u0430\u0439\u043B\u043E\u0432"));
        root.add(fScroll, BorderLayout.SOUTH);

        Runnable loadFiles = () -> {
            StringBuilder sb = new StringBuilder();
            for (String n : new String[]{"test1_roman.txt","test2_arabic.txt","test3_words.txt","test4_edge.txt","test5_mixed.txt"}) {
                sb.append("").append(n).append(" \n");
                try { sb.append(readFile(new File(TEST_DIR, n))); }
                catch (IOException e) { sb.append("(error)\n"); }
                sb.append("\n");
            }
            filesArea.setText(sb.toString()); filesArea.setCaretPosition(0);
        };
        loadFiles.run();

        btnRun.addActionListener(e -> { createTestFiles(); area.setText(runAllTests()); area.setCaretPosition(0); loadFiles.run(); });
        btnDir.addActionListener(e -> { try { Desktop.getDesktop().open(new File(TEST_DIR).getAbsoluteFile()); } catch (Exception ex) { showInfo(root, new File(TEST_DIR).getAbsolutePath()); }});
        btnOut.addActionListener(e -> { try { Desktop.getDesktop().open(new File(TEST_DIR,"output.txt").getAbsoluteFile()); } catch (Exception ex) { showInfo(root, new File(TEST_DIR,"output.txt").getAbsolutePath()); }});

        return root;
    }



    private static JFileChooser makeChooser() {
        JFileChooser fc = new JFileChooser(new File(".").getAbsoluteFile());
        fc.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        fc.setAcceptAllFileFilterUsed(true);
        return fc;
    }

    private static void showError(Component p, String m) { JOptionPane.showMessageDialog(p, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private static void showInfo(Component p, String m)  { JOptionPane.showMessageDialog(p, m, "Info",  JOptionPane.INFORMATION_MESSAGE); }
}
