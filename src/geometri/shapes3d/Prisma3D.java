package geometri.shapes3d;

import geometri.interfaces.Geometri;
import geometri.shapes2d.SegiEmpat2D;

/**
 * Prisma tegak beralas segi empat.
 */
public class Prisma3D implements Geometri {

    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final double sudutA;
    private final double sudutB;
    private final double sudutC;
    private final double sudutD;
    private final double tinggiPrisma;

    public Prisma3D(double a, double b, double c, double d,
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
        return hitungLuasAlas() * tinggiPrisma;
    }

    @Override
    public double hitungLuasPermukaan() {
        return 2.0 * hitungLuasAlas() + hitungKeliling() * tinggiPrisma;
    }

    @Override
    public String getInfo() {
        return String.format(
                "Prisma 3D | Alas a=%.2f,b=%.2f,c=%.2f,d=%.2f | tinggi prisma=%.2f",
                a, b, c, d, tinggiPrisma);
    }
}
