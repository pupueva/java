public class Circle {
    public double x;
    public double y;
    public double r;

    public Circle(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    /** Конструктор для задачи 7: точка без радиуса (r = 0). */
    public Circle(double x, double y) {
        this(x, y, 0.0);
    }

    @Override
    public String toString() {
        if (r == 0.0) {
            return String.format("Point(x=%.2f, y=%.2f)", x, y);
        }
        return String.format("Circle(x=%.2f, y=%.2f, r=%.2f)", x, y, r);
    }

    public String toFileLine() {
        if (r == 0.0) return x + " " + y;
        return x + " " + y + " " + r;
    }

    /**
     * Парсит строку формата:
     *   "x y"       — точка (для задачи 7)
     *   "x y r"     — окружность
     */
    public static Circle fromFileLine(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 2)
            throw new IllegalArgumentException("Нужно минимум 2 числа (x y): " + line);
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double r = 0.0;
        if (parts.length >= 3) {
            r = Double.parseDouble(parts[2]);
            if (r < 0) throw new IllegalArgumentException("Радиус не может быть отрицательным: " + r);
        }
        return new Circle(x, y, r);
    }
}