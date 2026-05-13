package geometri.shapes3d;

import geometri.interfaces.Geometri;
import geometri.shapes2d.SegiEmpatSembarang;

/**
 * Limas dengan alas berbentuk segi empat sembarang.
 * <p>
 * <b>Kalau ditanya “segi empat sembarangnya di mana?”</b> — parameter {@code a,b,c,d} dan
 * {@code sudutA–D} <em>adalah</em> alas segi empat sembarang; luas alas dihitung lewat
 * {@link #hitungLuasAlas()} yang membuat {@link SegiEmpatSembarang} dengan data yang sama.
 */
public class LimasSegiEmpatSembarang implements Geometri {

    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final double sudutA;
    private final double sudutB;
    private final double sudutC;
    private final double sudutD;
    private final double tinggiLimas;
    private final double tinggiSisiA;
    private final double tinggiSisiB;
    private final double tinggiSisiC;
    private final double tinggiSisiD;

    public LimasSegiEmpatSembarang(double a, double b, double c, double d,
            double sudutA, double sudutB, double sudutC, double sudutD,
            double tinggiLimas,
            double tinggiSisiA, double tinggiSisiB, double tinggiSisiC, double tinggiSisiD) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.sudutA = sudutA;
        this.sudutB = sudutB;
        this.sudutC = sudutC;
        this.sudutD = sudutD;
        this.tinggiLimas = tinggiLimas;
        this.tinggiSisiA = tinggiSisiA;
        this.tinggiSisiB = tinggiSisiB;
        this.tinggiSisiC = tinggiSisiC;
        this.tinggiSisiD = tinggiSisiD;
    }

    /** Luas alas = luas segi empat sembarang dengan sisi & sudut yang sama. */
    private double hitungLuasAlas() {
        SegiEmpatSembarang alas = new SegiEmpatSembarang(a, b, c, d, sudutA, sudutB, sudutC, sudutD);
        return alas.hitungLuas();
    }

    @Override
    public double hitungKeliling() {
        return a + b + c + d;
    }

    @Override
    public double hitungLuas() {
        return hitungLuasAlas();
    }

    @Override
    public double hitungVolume() {
        return (1.0 / 3.0) * hitungLuasAlas() * tinggiLimas;
    }

    @Override
    public double hitungLuasPermukaan() {
        double alas = hitungLuasAlas();
        double sisiMiring = 0.5 * (a * tinggiSisiA + b * tinggiSisiB + c * tinggiSisiC + d * tinggiSisiD);
        return alas + sisiMiring;
    }

    @Override
    public String getInfo() {
        return String.format(
                "Limas alas segi empat sembarang | alas a=%.2f,b=%.2f,c=%.2f,d=%.2f (°) %.1f,%.1f,%.1f,%.1f | tinggi limas=%.2f | tinggi sisi tegak a–d=%.2f,%.2f,%.2f,%.2f",
                a, b, c, d, sudutA, sudutB, sudutC, sudutD, tinggiLimas,
                tinggiSisiA, tinggiSisiB, tinggiSisiC, tinggiSisiD);
    }
}
