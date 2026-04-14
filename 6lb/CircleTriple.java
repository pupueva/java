/**
 * Составной тип данных: результат поиска —
 * тройка окружностей с минимальным периметром треугольника центров,
 * а также сам периметр.
 */
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
            "Тройка окружностей с минимальным периметром треугольника центров:\n" +
            "  1: %s\n" +
            "  2: %s\n" +
            "  3: %s\n" +
            "  Периметр треугольника центров = %.6f",
            a, b, c, perimeter
        );
    }
}
