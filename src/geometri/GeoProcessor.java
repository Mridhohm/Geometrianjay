package geometri;

import java.util.function.Consumer;

public class GeoProcessor implements Runnable {

    private static final Object LOCK = new Object();
    private final Geometri shape;
    private final String threadID;
    private final int delayMs;
    private final Consumer<String> logSink;

    public GeoProcessor(Geometri shape, String threadID, int delayMs) {
        this(shape, threadID, delayMs, null);
    }
    public GeoProcessor(Geometri shape, String threadID, int delayMs, Consumer<String> logSink) {
        this.shape = shape;
        this.threadID = threadID;
        this.delayMs = delayMs;
        this.logSink = logSink;
    }

    @Override
    public void run() {
        try {
            displayProcessingStatus();
            Thread.sleep(delayMs);

            double luas = shape.hitungLuas();
            double keliling = shape.hitungKeliling();
            double volume = shape.hitungVolume();
            double luasPermukaan = shape.hitungLuasPermukaan();

            displayResult(luas, keliling, volume, luasPermukaan);

        } catch (InterruptedException e) {
            logLine("[ERROR] " + threadID + " interrupted!");
            Thread.currentThread().interrupt();
        }
    }

    private void logLine(String line) {
        synchronized (LOCK) {
            if (logSink != null) {
                logSink.accept(line);
            } else {
                System.out.println(line);
            }
        }
    }

    private void displayProcessingStatus() {
        logLine("");
        logLine("=".repeat(60));
        logLine("MEMPROSES: " + shape.getClass().getSimpleName());
        logLine("Thread ID: " + threadID);
        logLine("Info: " + shape.getInfo());
        logLine("=".repeat(60));
    }

    private void displayResult(double luas, double keliling, double volume, double luasPermukaan) {
        logLine("HASIL PERHITUNGAN (" + threadID + ")");
        logLine("  Keliling: " + String.format("%.2f", keliling));
        logLine("  Luas: " + String.format("%.2f", luas));
        logLine("  Volume: " + String.format("%.2f", volume));
        logLine("  Luas Permukaan: " + String.format("%.2f", luasPermukaan));
        logLine("=".repeat(60));
    }
}
