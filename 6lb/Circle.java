public class Circle {
    public double x;
    public double y;
    public double r;

    public Circle(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    public String toString() {
        return String.format("Circle(x=%.2f, y=%.2f, r=%.2f)", x, y, r);
    }

    public String toFileLine() {
        return x + " " + y + " " + r;
    }

    public static Circle fromFileLine(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 3) throw new IllegalArgumentException("Неверный формат строки: " + line);
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double r = Double.parseDouble(parts[2]);
        if (r < 0) throw new IllegalArgumentException("Радиус не может быть отрицательным: " + r);
        return new Circle(x, y, r);
    }
}
