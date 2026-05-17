package geometri;

public class PrismaSegiEmpatSembarang
        extends SegiEmpatSembarang
        implements Geometri {

    private final double tinggiPrisma;

    // Constructor utama
    public PrismaSegiEmpatSembarang(
            double a, double b, double c, double d,
            double sudutA, double sudutB,
            double sudutC, double sudutD,
            double tinggiPrisma) {

        // Memanggil constructor parent
        super(a, b, c, d,
              sudutA, sudutB,
              sudutC, sudutD);

        this.tinggiPrisma = tinggiPrisma;
    }

    // hitung luas alas
    public double hitungLuasAlas() {
        return super.hitungLuas();
    }

    // hitung keliling alas
    @Override
    public double hitungKeliling() {
        return super.hitungKeliling();
    }

    // hitung luas alas prisma
    @Override
    public double hitungLuas() {
        return hitungLuasAlas();
    }

    // hitung volume prisma
    @Override
    public double hitungVolume() {
        return super.hitungLuas() * tinggiPrisma;
    }

    // hitung luas permukaan prisma
    @Override
    public double hitungLuasPermukaan() {
        return 2.0 * super.hitungLuas()
                + super.hitungKeliling() * tinggiPrisma;
    }

    // tmpilkan informasi prisma
    @Override
    public String getInfo() {
        return String.format(
                "Prisma Segi Empat Sembarang | tinggi prisma = %.2f",
                tinggiPrisma);
    }
}