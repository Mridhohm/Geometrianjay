package geometri.shapes3d;

import java.util.Objects;

import geometri.interfaces.Geometri;
import geometri.shapes2d.SegiEmpatSembarang;

/**
 * Prisma tegak dengan alas segi empat sembarang (komposisi: {@link SegiEmpatSembarang}).
 */
public class PrismaSegiEmpatSembarang implements Geometri {

    private final SegiEmpatSembarang alas;
    private final double tinggiPrisma;

    public PrismaSegiEmpatSembarang(SegiEmpatSembarang alas, double tinggiPrisma) {
        this.alas = Objects.requireNonNull(alas, "alas");
        this.tinggiPrisma = tinggiPrisma;
    }

    /**
     * Delegasi ke {@link #PrismaSegiEmpatSembarang(SegiEmpatSembarang, double)}.
     */
    public PrismaSegiEmpatSembarang(double a, double b, double c, double d,
            double sudutA, double sudutB, double sudutC, double sudutD,
            double tinggiPrisma) {
        this(new SegiEmpatSembarang(a, b, c, d, sudutA, sudutB, sudutC, sudutD), tinggiPrisma);
    }

    public SegiEmpatSembarang getAlas() {
        return alas;
    }

    private double hitungLuasAlas() {
        return alas.hitungLuas();
    }

    @Override
    public double hitungKeliling() {
        return alas.hitungKeliling();
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
        return String.format("Prisma alas segi empat sembarang | %s | tinggi prisma=%.2f",
                alas.getInfo(), tinggiPrisma);
    }
}
