public class CircleTriple {
    public Circle a;
    public Circle b;
    public Circle c;
    public double perimeter;

    public CircleTriple(Circle a, Circle b, Circle c, double perimeter) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.perimeter = perimeter;
    }

    @Override
    public String toString() {
        return String.format(
                "Три точки с минимальным периметром треугольника:\n" +
                        "  1: (x=%.4f, y=%.4f)\n" +
                        "  2: (x=%.4f, y=%.4f)\n" +
                        "  3: (x=%.4f, y=%.4f)\n" +
                        "  Периметр = %.6f",
                a.x, a.y,
                b.x, b.y,
                c.x, c.y,
                perimeter
        );
    }
}