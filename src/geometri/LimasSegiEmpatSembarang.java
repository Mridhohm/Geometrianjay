package geometri;


public class LimasSegiEmpatSembarang
        extends SegiEmpatSembarang
        implements Geometri {

    private final double tinggiLimas;

    // Ini tuh Tinggi masing-masing sisi tegak
    private final double tinggiSisiA;
    private final double tinggiSisiB;
    private final double tinggiSisiC;
    private final double tinggiSisiD;

 
    public LimasSegiEmpatSembarang(
            double a, double b, double c, double d,
            double sudutA, double sudutB,
            double sudutC, double sudutD,
            double tinggiLimas,
            double tinggiSisiA,
            double tinggiSisiB,
            double tinggiSisiC,
            double tinggiSisiD) {

        // Ini Memanggil constructor parent
        super(a, b, c, d,
              sudutA, sudutB,
              sudutC, sudutD);

        this.tinggiLimas = tinggiLimas;

        this.tinggiSisiA = tinggiSisiA;
        this.tinggiSisiB = tinggiSisiB;
        this.tinggiSisiC = tinggiSisiC;
        this.tinggiSisiD = tinggiSisiD;
    }

    /**
     * Buat Menghitung luas alas limas.
     */
    private double hitungLuasAlas() {
        return super.hitungLuas();
    }

    /**
     * Buat Menghitung keliling alas.
     */
    @Override
    public double hitungKeliling() {
        return super.hitungKeliling();
    }

    /**
     * Buat Mengembalikan luas alas.
     */
    @Override
    public double hitungLuas() {
        return hitungLuasAlas();
    }

    /**
     * Buat Menghitung volume limas.
     */
    @Override
    public double hitungVolume() {
        return (1.0 / 3.0)
                * hitungLuasAlas()
                * tinggiLimas;
    }

    /**
     * Buat Menghitung luas permukaan limas.
     */
    @Override
    public double hitungLuasPermukaan() {

        double luasAlas = hitungLuasAlas();

        double luasSisiTegak =
                0.5 * (
                        getSisiA() * tinggiSisiA
                      + getSisiB() * tinggiSisiB
                      + getSisiC() * tinggiSisiC
                      + getSisiD() * tinggiSisiD
                );

        return luasAlas + luasSisiTegak;
    }

    /**
     * Buat Menampilkan informasi limas.
     */
    @Override
    public String getInfo() {

        return String.format(
                "Limas Segi Empat Sembarang | "
              + "tinggi limas = %.2f | "
              + "tinggi sisi tegak A-D = %.2f, %.2f, %.2f, %.2f",
                tinggiLimas,
                tinggiSisiA,
                tinggiSisiB,
                tinggiSisiC,
                tinggiSisiD
        );
    }
}