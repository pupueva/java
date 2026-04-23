public class Triangle {
    public double x1, y1;
    public double x2, y2;
    public double x3, y3;

    public Triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        this.x1 = x1; this.y1 = y1;
        this.x2 = x2; this.y2 = y2;
        this.x3 = x3; this.y3 = y3;
    }

    public double sideA() {
        return dist(x2, y2, x3, y3);
    }

    public double sideB() {
        return dist(x1, y1, x3, y3);
    }

    public double sideC() {
        return dist(x1, y1, x2, y2);
    }

    private static double dist(double ax, double ay, double bx, double by) {
        double dx = ax - bx;
        double dy = ay - by;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double[] sortedSides() {
        double a = sideA();
        double b = sideB();
        double c = sideC();
        if (a > b) { double t = a; a = b; b = t; }
        if (b > c) { double t = b; b = c; c = t; }
        if (a > b) { double t = a; a = b; b = t; }
        return new double[]{a, b, c};
    }

    public boolean isValid() {
        double[] s = sortedSides();
        return s[0] > 1e-10 && s[1] > 1e-10 && s[2] > 1e-10
            && s[0] + s[1] > s[2];
    }

    public static boolean areSimilar(Triangle t1, Triangle t2) {
        double[] s1 = t1.sortedSides();
        double[] s2 = t2.sortedSides();
        if (s1[2] < 1e-10 || s2[2] < 1e-10) return false;
        double ratio = s1[0] / s2[0];
        if (ratio < 1e-10) return false;
        double eps = 1e-9;
        return Math.abs(s1[0] / s2[0] - ratio) < eps
            && Math.abs(s1[1] / s2[1] - ratio) < eps
            && Math.abs(s1[2] / s2[2] - ratio) < eps;
    }

    @Override
    public String toString() {
        return String.format("Triangle((%.2f,%.2f),(%.2f,%.2f),(%.2f,%.2f))",
            x1, y1, x2, y2, x3, y3);
    }

    public String toFileLine() {
        return x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3;
    }

    public static Triangle fromFileLine(String line) {
        String[] p = line.trim().split("\\s+");
        if (p.length < 6) throw new IllegalArgumentException("Нужно 6 чисел: x1 y1 x2 y2 x3 y3, строка: " + line);
        return new Triangle(
            Double.parseDouble(p[0]), Double.parseDouble(p[1]),
            Double.parseDouble(p[2]), Double.parseDouble(p[3]),
            Double.parseDouble(p[4]), Double.parseDouble(p[5])
        );
    }
}
