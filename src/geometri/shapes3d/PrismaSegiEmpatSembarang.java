package geometri.shapes3d;

import geometri.interfaces.Geometri;
import geometri.shapes2d.SegiEmpatSembarang;

/**
 * Prisma tegak dengan alas berbentuk segi empat sembarang.
 * <p>
 * <b>Kalau ditanya “segi empat sembarangnya di mana?”</b> — sama seperti limas: field
 * {@code a,b,c,d} dan {@code sudutA–D} mendeskripsikan alas; {@link #hitungLuasAlas()}
 * mendelegasikan ke {@link SegiEmpatSembarang}.
 */
public class PrismaSegiEmpatSembarang implements Geometri {

    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final double sudutA;
    private final double sudutB;
    private final double sudutC;
    private final double sudutD;
    private final double tinggiPrisma;

    public PrismaSegiEmpatSembarang(double a, double b, double c, double d,
            double sudutA, double sudutB, double sudutC, double sudutD,
            double tinggiPrisma) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.sudutA = sudutA;
        this.sudutB = sudutB;
        this.sudutC = sudutC;
        this.sudutD = sudutD;
        this.tinggiPrisma = tinggiPrisma;
    }

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
        return hitungLuasAlas() * tinggiPrisma;
    }

    @Override
    public double hitungLuasPermukaan() {
        return 2.0 * hitungLuasAlas() + hitungKeliling() * tinggiPrisma;
    }

    @Override
    public String getInfo() {
        return String.format(
                "Prisma alas segi empat sembarang | alas a=%.2f,b=%.2f,c=%.2f,d=%.2f (°) %.1f,%.1f,%.1f,%.1f | tinggi prisma=%.2f",
                a, b, c, d, sudutA, sudutB, sudutC, sudutD, tinggiPrisma);
    }
}
