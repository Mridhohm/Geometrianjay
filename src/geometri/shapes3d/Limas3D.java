package geometri.shapes3d;

import geometri.interfaces.Geometri;
import geometri.shapes2d.SegiEmpat2D;

/**
 * Limas beralas segi empat (sisi dan sudut alas sama seperti SegiEmpat2D).
 */
public class Limas3D implements Geometri {

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

    public Limas3D(double a, double b, double c, double d,
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

    private double hitungLuasAlas() {
        SegiEmpat2D alas = new SegiEmpat2D(a, b, c, d, sudutA, sudutB, sudutC, sudutD);
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
                "Limas 3D | Alas a=%.2f,b=%.2f,c=%.2f,d=%.2f | tinggi=%.2f | tinggi sisi a-d: %.2f,%.2f,%.2f,%.2f",
                a, b, c, d, tinggiLimas, tinggiSisiA, tinggiSisiB, tinggiSisiC, tinggiSisiD);
    }
}
