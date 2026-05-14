package geometri;

import java.util.Objects;

/**
 * Limas dengan alas berbentuk segi empat sembarang (komposisi: memegang {@link SegiEmpatSembarang}).
 * <p>
 * <b>Kalau ditanya “segi empat sembarangnya di mana?”</b> — field {@link #alas}; volume & luas alas memakai objek itu.
 */
public class LimasSegiEmpatSembarang implements Geometri {

    private final SegiEmpatSembarang alas;
    private final double tinggiLimas;
    private final double tinggiSisiA;
    private final double tinggiSisiB;
    private final double tinggiSisiC;
    private final double tinggiSisiD;

    /**
     * Limas dengan alas sebagai objek (disarankan).
     */
    public LimasSegiEmpatSembarang(SegiEmpatSembarang alas, double tinggiLimas,
            double tinggiSisiA, double tinggiSisiB, double tinggiSisiC, double tinggiSisiD) {
        this.alas = Objects.requireNonNull(alas, "alas");
        this.tinggiLimas = tinggiLimas;
        this.tinggiSisiA = tinggiSisiA;
        this.tinggiSisiB = tinggiSisiB;
        this.tinggiSisiC = tinggiSisiC;
        this.tinggiSisiD = tinggiSisiD;
    }

    /**
     * Delegasi ke {@link #LimasSegiEmpatSembarang(SegiEmpatSembarang, double, double, double, double, double)}.
     */
    public LimasSegiEmpatSembarang(double a, double b, double c, double d,
            double sudutA, double sudutB, double sudutC, double sudutD,
            double tinggiLimas,
            double tinggiSisiA, double tinggiSisiB, double tinggiSisiC, double tinggiSisiD) {
        this(new SegiEmpatSembarang(a, b, c, d, sudutA, sudutB, sudutC, sudutD),
                tinggiLimas, tinggiSisiA, tinggiSisiB, tinggiSisiC, tinggiSisiD);
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
        return (1.0 / 3.0) * hitungLuasAlas() * tinggiLimas;
    }

    @Override
    public double hitungLuasPermukaan() {
        double luasAlas = hitungLuasAlas();
        double sisiMiring = 0.5 * (alas.getSisiA() * tinggiSisiA + alas.getSisiB() * tinggiSisiB
                + alas.getSisiC() * tinggiSisiC + alas.getSisiD() * tinggiSisiD);
        return luasAlas + sisiMiring;
    }

    @Override
    public String getInfo() {
        return String.format(
                "Limas alas segi empat sembarang | %s | tinggi limas=%.2f | tinggi sisi tegak a–d=%.2f,%.2f,%.2f,%.2f",
                alas.getInfo(), tinggiLimas, tinggiSisiA, tinggiSisiB, tinggiSisiC, tinggiSisiD);
    }
}
