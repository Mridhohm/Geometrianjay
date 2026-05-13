package geometri.shapes2d;

import geometri.interfaces.Geometri;

/**
 * Segi empat sembarang di bidang: empat sisi {@code a–b–c–d} berurutan dan empat sudut dalam
 * {@code sudutA…sudutD} (derajat) di setiap vertex.
 * <p>
 * <b>Kalau ditanya “segi empat sembarangnya di mana?”</b> — class ini <em>adalah</em> bentuknya:
 * field sisi + sudut, plus {@link #hitungLuas()} / {@link #hitungKeliling()}.
 * Luas memakai diagonal (cosine rule dari sudut antara {@code a} dan {@code b}) + Heron dua segitiga.
 */
public class SegiEmpatSembarang implements Geometri {

    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final double sudutA;
    private final double sudutB;
    private final double sudutC;
    private final double sudutD;

    public SegiEmpatSembarang(double a, double b, double c, double d,
            double sudutA, double sudutB, double sudutC, double sudutD) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.sudutA = sudutA;
        this.sudutB = sudutB;
        this.sudutC = sudutC;
        this.sudutD = sudutD;
    }

    @Override
    public double hitungKeliling() {
        return a + b + c + d;
    }

    @Override
    public double hitungLuas() {
        double radB = Math.toRadians(sudutB);
        double diagSq = a * a + b * b - 2 * a * b * Math.cos(radB);
        if (diagSq <= 0) {
            return 0.0;
        }
        double diagonal = Math.sqrt(diagSq);
        double area1 = heron(a, b, diagonal);
        double area2 = heron(c, d, diagonal);
        return area1 + area2;
    }

    private static double heron(double x, double y, double z) {
        double s = (x + y + z) / 2.0;
        double inner = s * (s - x) * (s - y) * (s - z);
        if (inner <= 0) {
            return 0.0;
        }
        return Math.sqrt(inner);
    }

    @Override
    public double hitungVolume() {
        return 0.0;
    }

    @Override
    public double hitungLuasPermukaan() {
        return hitungLuas();
    }

    @Override
    public String getInfo() {
        return String.format(
                "Segi empat sembarang (2D) | sisi a=%.2f,b=%.2f,c=%.2f,d=%.2f | sudut ° A–D: %.1f, %.1f, %.1f, %.1f",
                a, b, c, d, sudutA, sudutB, sudutC, sudutD);
    }
}
