package geometri;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

            return new SegiEmpatSembarang(
                    s, s, s, s,
                    90, 90, 90, 90
            );
        }

        double w = 3 + RND.nextDouble() * 6;
        double h = 2 + RND.nextDouble() * 5;

        return new SegiEmpatSembarang(
                w, h, w, h,
                90, 90, 90, 90
        );
    }

    private static LimasSegiEmpatSembarang randomLimasSegiEmpatSembarang() {

        double w = 3 + RND.nextDouble() * 4;
        double h = 2 + RND.nextDouble() * 4;

        double tinggiLimas = 4 + RND.nextDouble() * 6;

        double tinggiSisi = 3 + RND.nextDouble() * 4;

        return new LimasSegiEmpatSembarang(
                w, h, w, h,
                90, 90, 90, 90,
                tinggiLimas,
                tinggiSisi,
                tinggiSisi,
                tinggiSisi,
                tinggiSisi
        );
    }

    private static PrismaSegiEmpatSembarang randomPrismaSegiEmpatSembarang() {

        double w = 3 + RND.nextDouble() * 5;

        double h = 2 + RND.nextDouble() * 5;

        double tinggiPrisma = 5 + RND.nextDouble() * 10;

        return new PrismaSegiEmpatSembarang(
                w, h, w, h,
                90, 90, 90, 90,
                tinggiPrisma
        );
    }
}