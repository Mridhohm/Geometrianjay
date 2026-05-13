import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import geometri.ShapeGenerator;
import geometri.interfaces.Geometri;
import geometri.processors.GeoProcessor;

public class Main {

    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    /**
     * Satu bentuk geometri = satu thread. Ubah dua angka ini untuk memperbanyak / mengurangi.
     */
    private static final int JUMLAH_BENTUK_MIN = 3;
    private static final int JUMLAH_BENTUK_MAX = 5; // inklusif

    public static void main(String[] args) {
        System.out.println("=== SISTEM PERHITUNGAN GEOMETRI MULTITHREADING ===");

        System.out.println("\n[INFO] Menggenerate bentuk geometri acak...");
        List<Geometri> shapes = ShapeGenerator.generateRandomShapes(JUMLAH_BENTUK_MIN, JUMLAH_BENTUK_MAX);
        System.out.println("[OK] Berhasil membuat " + shapes.size() + " bentuk geometri");
        System.out.println("[INFO] Memulai pemrosesan dengan multithreading...");
        System.out.println("[INFO] Memulai " + shapes.size() + " threads...\n");

        Thread[] threads = new Thread[shapes.size()];
        for (int i = 0; i < shapes.size(); i++) {
            int delayMs = 800 + RND.nextInt(1200);
            GeoProcessor processor = new GeoProcessor(shapes.get(i), "Thread-" + (i + 1), delayMs);
            threads[i] = new Thread(processor, "GeoWorker-" + (i + 1));
        }

        for (Thread t : threads) {
            t.start();
        }

        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[ERROR] Main interrupted while waiting for threads.");
        }

        printSummary(shapes);
        System.out.println("\n[OK] Semua perhitungan selesai!");
    }

    private static void printSummary(List<Geometri> shapes) {
        synchronized (System.out) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("RINGKASAN AKHIR (main thread)");
            System.out.println("=".repeat(60));
            int i = 1;
            for (Geometri g : shapes) {
                System.out.println(i + ". " + g.getClass().getSimpleName()
                        + " | luas=" + String.format("%.2f", g.hitungLuas())
                        + ", keliling=" + String.format("%.2f", g.hitungKeliling())
                        + ", volume=" + String.format("%.2f", g.hitungVolume())
                        + ", luas permukaan=" + String.format("%.2f", g.hitungLuasPermukaan()));
                i++;
            }
        }
    }
}
