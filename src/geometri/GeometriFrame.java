package geometri;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class GeometriFrame extends JFrame {

    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    private static final String[] TABLE_HEADERS = {
            "#", "Bentuk", "Luas (alas / 2D)", "Keliling alas", "Volume", "Luas permukaan"
    };

    private static final String LOG_SEPARATOR = "------------------------------------------------------------";

    private final JSpinner spinnerMin = new JSpinner(new SpinnerNumberModel(3, 1, 500, 1));
    private final JSpinner spinnerMax = new JSpinner(new SpinnerNumberModel(8, 1, 500, 1));
    private final JSpinner spinnerDelayMin = new JSpinner(new SpinnerNumberModel(400, 0, 30_000, 100));
    private final JSpinner spinnerDelayMax = new JSpinner(new SpinnerNumberModel(2000, 0, 30_000, 100));

    private final JTextArea logArea = new JTextArea();
    private final DefaultTableModel summaryModel = new DefaultTableModel(TABLE_HEADERS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable summaryTable = new JTable(summaryModel);

    private final JButton btnRun = new JButton("Jalankan");
    private final JButton btnStop = new JButton("Hentikan");
    private final JButton btnClear = new JButton("Bersihkan log");
    private final JButton btnCopy = new JButton("Salin log + tabel");
    private final JProgressBar progress = new JProgressBar();

    private volatile Thread[] activeThreads = new Thread[0];

    public GeometriFrame() {
        super("Geometri — multithreading");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 580));

        initLogArea();
        initSummaryTable();

        Dimension spin = new Dimension(88, 28);
        spinnerMin.setPreferredSize(spin);
        spinnerMax.setPreferredSize(spin);
        spinnerDelayMin.setPreferredSize(spin);
        spinnerDelayMax.setPreferredSize(spin);

        spinnerMin.setToolTipText("Minimal jumlah bentuk (satu bentuk = satu thread)");
        spinnerMax.setToolTipText("Maksimal jumlah bentuk (inklusif)");
        spinnerDelayMin.setToolTipText("Delay minimum per thread sebelum hitung (ms)");
        spinnerDelayMax.setToolTipText("Delay maksimum per thread (ms)");

        JPanel params = buildParameterPanel();
        params.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xd1d5db)),
                        "Parameter simulasi"),
                BorderFactory.createEmptyBorder(4, 10, 10, 10)));

        JLabel hint = new JLabel(
                "<html><div style='color:#6b7280;font-size:12px'>"
                        + "Atas: <b>log</b> tiap thread (rata kiri). Bawah: <b>tabel ringkasan</b> setelah join. "
                        + "Volume bentuk 2D = \"—\"."
                        + "</div></html>");
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setOpaque(false);
        north.add(params);
        north.add(Box.createVerticalStrut(6));
        north.add(hint);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xd1d5db)),
                "Log thread (urutan eksekusi)"));

        JScrollPane tableScroll = new JScrollPane(summaryTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xd1d5db)),
                "Ringkasan angka (setelah join)"));
        tableScroll.setToolTipText(
                "Satu baris per bentuk. Volume bentuk 2D tidak didefinisikan (ditampilkan —).");

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, logScroll, tableScroll);
        split.setResizeWeight(0.55);
        split.setDividerLocation(280);
        split.setContinuousLayout(true);

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        btnRun.setMnemonic('J');
        btnStop.setEnabled(false);
        progress.setStringPainted(true);
        progress.setString("Siap");
        progress.setPreferredSize(new Dimension(200, 22));
        progress.setMaximumSize(new Dimension(Short.MAX_VALUE, 22));

        actions.add(btnRun);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(btnStop);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(btnClear);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(btnCopy);
        actions.add(Box.createHorizontalGlue());
        actions.add(progress);

        btnRun.addActionListener(e -> startRun());
        btnClear.addActionListener(e -> clearAllOutput());
        btnCopy.addActionListener(e -> copyLogAndTable());
        btnStop.addActionListener(e -> interruptWorkers());

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBorder(BorderFactory.createEmptyBorder(10, 14, 0, 14));
        root.add(north, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void initLogArea() {
        logArea.setEditable(false);
        logArea.setLineWrap(false);
        logArea.setWrapStyleWord(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logArea.setMargin(new Insets(8, 10, 8, 10));
        logArea.setTabSize(4);
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(new Color(0x111827));
    }

    private void initSummaryTable() {
        summaryTable.setRowHeight(22);
        summaryTable.setFillsViewportHeight(true);
        summaryTable.setShowGrid(true);
        summaryTable.setGridColor(new Color(0xe5e7eb));
        summaryTable.getTableHeader().setReorderingAllowed(false);
        summaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column >= 2 ? JLabel.RIGHT : JLabel.LEFT);
                return c;
            }
        };
        for (int col = 0; col < summaryTable.getColumnCount(); col++) {
            summaryTable.getColumnModel().getColumn(col).setCellRenderer(right);
        }
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(36);
        summaryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
    }

    private void clearAllOutput() {
        logArea.setText("");
        summaryModel.setRowCount(0);
    }

    private void appendLogLine(String line) {
        if (line == null) {
            return;
        }
        if (line.isEmpty()) {
            logArea.append("\n");
            scrollLogToEnd();
            return;
        }

        String trimmed = line.trim();
        if (!trimmed.isEmpty() && trimmed.chars().allMatch(ch -> ch == '=') && trimmed.length() >= 12) {
            logArea.append(LOG_SEPARATOR + "\n");
            scrollLogToEnd();
            return;
        }

        if (trimmed.startsWith("=".repeat(52)) || trimmed.startsWith("RINGKASAN AKHIR")
                || trimmed.startsWith("[RINGKASAN]")) {
            return;
        }

        logArea.append(line.stripLeading() + "\n");
        scrollLogToEnd();
    }

    private void scrollLogToEnd() {
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private JPanel buildParameterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Jumlah bentuk (min)"), c);
        c.gridx = 1;
        p.add(spinnerMin, c);
        c.gridx = 2;
        p.add(new JLabel("sampai (max)"), c);
        c.gridx = 3;
        p.add(spinnerMax, c);

        c.gridx = 0;
        c.gridy = 1;
        p.add(new JLabel("Delay thread (ms, min)"), c);
        c.gridx = 1;
        p.add(spinnerDelayMin, c);
        c.gridx = 2;
        p.add(new JLabel("sampai (max)"), c);
        c.gridx = 3;
        p.add(spinnerDelayMax, c);

        return p;
    }

    private static String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private static String volumeCell(Geometri g) {
        // Hanya bentuk 2D murni — limas/prisma juga extends SegiEmpatSembarang
        if (g.getClass() == SegiEmpatSembarang.class) {
            return "—";
        }
        return fmt(g.hitungVolume());
    }

    private void fillSummaryTable(List<Geometri> shapes) {
        summaryModel.setRowCount(0);
        int i = 1;
        for (Geometri g : shapes) {
            summaryModel.addRow(new Object[] {
                    i,
                    g.getClass().getSimpleName(),
                    fmt(g.hitungLuas()),
                    fmt(g.hitungKeliling()),
                    volumeCell(g),
                    fmt(g.hitungLuasPermukaan()),
            });
            i++;
        }
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
            JOptionPane.showMessageDialog(this, "Max harus lebih besar atau sama dengan min.", "Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int dmin = (Integer) spinnerDelayMin.getValue();
        int dmax = (Integer) spinnerDelayMax.getValue();
        if (dmax < dmin) {
            JOptionPane.showMessageDialog(this, "Delay max harus >= delay min.", "Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        clearAllOutput();

        btnRun.setEnabled(false);
        btnStop.setEnabled(true);
        progress.setIndeterminate(true);
        progress.setString("Berjalan…");

        Consumer<String> toEdt = line -> SwingUtilities.invokeLater(() -> appendLogLine(line));

        Thread worker = new Thread(() -> runBatch(min, max, dmin, dmax, toEdt), "GeoGui-Coordinator");
        worker.start();
    }

    private void runBatch(int min, int max, int delayMin, int delayMax, Consumer<String> toEdt) {
        List<Geometri> shapes = null;
        try {
            toEdt.accept("=== Geometri multithreading ===");
            toEdt.accept("");
            toEdt.accept("[INFO] Membangkitkan bentuk acak…");
            shapes = ShapeGenerator.generateRandomShapes(min, max);
            toEdt.accept("[OK] " + shapes.size() + " bentuk siap — tiap bentuk = satu thread.");
            toEdt.accept("[INFO] Worker dimulai…");
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

            if (shapes != null) {
                final List<Geometri> finalShapes = shapes;
                SwingUtilities.invokeLater(() -> fillSummaryTable(finalShapes));
            }

            toEdt.accept("");
            toEdt.accept("[INFO] Ringkasan angka ada di tabel di bawah.");
            toEdt.accept("[OK] Semua thread selesai.");

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

    private void copyLogAndTable() {
        StringBuilder sb = new StringBuilder();
        String logText = logArea.getText();
        if (logText != null && !logText.isBlank()) {
            sb.append("--- LOG ---\n").append(logText).append("\n\n");
        }
        sb.append("--- TABEL RINGKASAN ---\n");
        for (int c = 0; c < summaryModel.getColumnCount(); c++) {
            if (c > 0) {
                sb.append('\t');
            }
            sb.append(summaryModel.getColumnName(c));
        }
        sb.append('\n');
        for (int r = 0; r < summaryModel.getRowCount(); r++) {
            for (int c = 0; c < summaryModel.getColumnCount(); c++) {
                if (c > 0) {
                    sb.append('\t');
                }
                Object v = summaryModel.getValueAt(r, c);
                sb.append(v != null ? v.toString() : "");
            }
            sb.append('\n');
        }
        String all = sb.toString();
        if (!all.isBlank()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(all), null);
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
}
