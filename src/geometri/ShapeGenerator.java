package geometri;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import geometri.interfaces.Geometri;
import geometri.shapes2d.SegiEmpatSembarang;
import geometri.shapes3d.LimasSegiEmpatSembarang;
import geometri.shapes3d.PrismaSegiEmpatSembarang;

/**
 * Membangkitkan bentuk geometri acak untuk demo multithreading.
 */
public final class ShapeGenerator {

    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    private ShapeGenerator() {
    }

    public static List<Geometri> generateRandomShapes(int minCount, int maxCountInclusive) {
        if (minCount < 1) {
            throw new IllegalArgumentException("minCount harus >= 1");
        }
        if (maxCountInclusive < minCount) {
            throw new IllegalArgumentException("max harus >= min");
        }
        List<Geometri> shapes = new ArrayList<>();
        int count = minCount + RND.nextInt(maxCountInclusive - minCount + 1);

        for (int i = 0; i < count; i++) {
            int kind = RND.nextInt(3);
            if (kind == 0) {
                shapes.add(randomSegiEmpatSembarang());
            } else if (kind == 1) {
                shapes.add(randomLimasSegiEmpatSembarang());
            } else {
                shapes.add(randomPrismaSegiEmpatSembarang());
            }
        }
        return shapes;
    }

    private static SegiEmpatSembarang randomSegiEmpatSembarang() {
        if (RND.nextBoolean()) {
            double s = 3 + RND.nextDouble() * 5;
            return new SegiEmpatSembarang(s, s, s, s, 90, 90, 90, 90);
        }
        double w = 3 + RND.nextDouble() * 6;
        double h = 2 + RND.nextDouble() * 5;
        return new SegiEmpatSembarang(w, h, w, h, 90, 90, 90, 90);
    }

    private static LimasSegiEmpatSembarang randomLimasSegiEmpatSembarang() {
        double w = 3 + RND.nextDouble() * 4;
        double h = 2 + RND.nextDouble() * 4;
        SegiEmpatSembarang alas = new SegiEmpatSembarang(w, h, w, h, 90, 90, 90, 90);
        double tinggi = 4 + RND.nextDouble() * 6;
        double hs = 3 + RND.nextDouble() * 4;
        return new LimasSegiEmpatSembarang(alas, tinggi, hs, hs, hs, hs);
    }

    private static PrismaSegiEmpatSembarang randomPrismaSegiEmpatSembarang() {
        double w = 3 + RND.nextDouble() * 5;
        double h = 2 + RND.nextDouble() * 5;
        SegiEmpatSembarang alas = new SegiEmpatSembarang(w, h, w, h, 90, 90, 90, 90);
        double t = 5 + RND.nextDouble() * 10;
        return new PrismaSegiEmpatSembarang(alas, t);
    }
}
