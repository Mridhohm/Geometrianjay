import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import geometri.ShapeGenerator;
import geometri.interfaces.Geometri;
import geometri.processors.GeoProcessor;

/**
 * GUI Swing: parameter jumlah bentuk & delay, log multithreading, ringkasan setelah join.
 */
public class GeometriFrame extends JFrame {

    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    private final JSpinner spinnerMin = new JSpinner(new SpinnerNumberModel(3, 1, 500, 1));
    private final JSpinner spinnerMax = new JSpinner(new SpinnerNumberModel(8, 1, 500, 1));
    private final JSpinner spinnerDelayMin = new JSpinner(new SpinnerNumberModel(400, 0, 30_000, 100));
    private final JSpinner spinnerDelayMax = new JSpinner(new SpinnerNumberModel(2000, 0, 30_000, 100));

    private final JTextArea logArea = new JTextArea();
    private final JButton btnRun = new JButton("Jalankan perhitungan");
    private final JButton btnClear = new JButton("Bersihkan log");
    private final JButton btnStop = new JButton("Hentikan");
    private final JProgressBar progress = new JProgressBar();

    private volatile Thread[] activeThreads = new Thread[0];

    public GeometriFrame() {
        super("Geometri multithreading");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(720, 520));

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Log (thread-safe ke layar)"));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Jumlah bentuk — min:"));
        top.add(spinnerMin);
        top.add(new JLabel("max:"));
        top.add(spinnerMax);
        top.add(new JLabel("| Delay (ms) min:"));
        top.add(spinnerDelayMin);
        top.add(new JLabel("max:"));
        top.add(spinnerDelayMax);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(btnRun);
        bottom.add(btnStop);
        bottom.add(btnClear);
        progress.setIndeterminate(false);
        progress.setStringPainted(true);
        progress.setString("Siap");
        bottom.add(progress);

        btnStop.setEnabled(false);

        btnRun.addActionListener(e -> startRun());
        btnClear.addActionListener(e -> logArea.setText(""));
        btnStop.addActionListener(e -> interruptWorkers());

        setLayout(new BorderLayout(8, 8));
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // default LAF
        }
        SwingUtilities.invokeLater(() -> {
            GeometriFrame f = new GeometriFrame();
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

    private void startRun() {
        int min = (Integer) spinnerMin.getValue();
        int max = (Integer) spinnerMax.getValue();
        if (max < min) {
            JOptionPane.showMessageDialog(this, "Max harus >= min.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int dmin = (Integer) spinnerDelayMin.getValue();
        int dmax = (Integer) spinnerDelayMax.getValue();
        if (dmax < dmin) {
            JOptionPane.showMessageDialog(this, "Delay max harus >= delay min.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnRun.setEnabled(false);
        btnStop.setEnabled(true);
        progress.setIndeterminate(true);
        progress.setString("Berjalan…");

        Consumer<String> toEdt = line -> SwingUtilities.invokeLater(() -> {
            logArea.append(line + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });

        Thread worker = new Thread(() -> runBatch(min, max, dmin, dmax, toEdt), "GeoGui-Coordinator");
        worker.start();
    }

    private void runBatch(int min, int max, int delayMin, int delayMax, Consumer<String> toEdt) {
        try {
            toEdt.accept("=== SISTEM PERHITUNGAN GEOMETRI MULTITHREADING (GUI) ===");
            toEdt.accept("");
            toEdt.accept("[INFO] Menggenerate bentuk geometri acak…");
            List<Geometri> shapes = ShapeGenerator.generateRandomShapes(min, max);
            toEdt.accept("[OK] Berhasil membuat " + shapes.size() + " bentuk geometri");
            toEdt.accept("[INFO] Memulai " + shapes.size() + " thread…");
            toEdt.accept("");

            Thread[] threads = new Thread[shapes.size()];
            this.activeThreads = threads;

            for (int i = 0; i < shapes.size(); i++) {
                int span = Math.max(0, delayMax - delayMin);
                int delayMs = delayMin + (span > 0 ? RND.nextInt(span + 1) : 0);
                GeoProcessor processor = new GeoProcessor(
                        shapes.get(i), "Thread-" + (i + 1), delayMs, toEdt);
                threads[i] = new Thread(processor, "GeoWorker-" + (i + 1));
            }

            for (Thread t : threads) {
                t.start();
            }

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    toEdt.accept("[ERROR] Koordinator di-interrupt.");
                    break;
                }
            }

            appendSummary(shapes, toEdt);
            toEdt.accept("");
            toEdt.accept("[OK] Semua perhitungan selesai!");

        } finally {
            SwingUtilities.invokeLater(() -> {
                btnRun.setEnabled(true);
                btnStop.setEnabled(false);
                progress.setIndeterminate(false);
                progress.setString("Siap");
                activeThreads = new Thread[0];
            });
        }
    }

    private void interruptWorkers() {
        Thread[] threads = activeThreads;
        for (Thread t : threads) {
            if (t != null && t.isAlive()) {
                t.interrupt();
            }
        }
    }

    private static void appendSummary(List<Geometri> shapes, Consumer<String> out) {
        out.accept("");
        out.accept("=".repeat(60));
        out.accept("RINGKASAN AKHIR (setelah join)");
        out.accept("=".repeat(60));
        int i = 1;
        for (Geometri g : shapes) {
            out.accept(i + ". " + g.getClass().getSimpleName()
                    + " | luas=" + String.format("%.2f", g.hitungLuas())
                    + ", keliling=" + String.format("%.2f", g.hitungKeliling())
                    + ", volume=" + String.format("%.2f", g.hitungVolume())
                    + ", luas permukaan=" + String.format("%.2f", g.hitungLuasPermukaan()));
            i++;
        }
    }
}
