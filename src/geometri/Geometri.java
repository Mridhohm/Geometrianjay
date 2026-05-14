package geometri;

/**
 * Kontrak perhitungan geometri untuk bentuk 2D dan 3D.
 */
public interface Geometri {

    double hitungLuas();

    double hitungKeliling();

    double hitungVolume();

    double hitungLuasPermukaan();

    String getInfo();
}
