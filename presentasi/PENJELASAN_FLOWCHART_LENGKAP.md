# Panduan membaca flowchart — Proyek Geometri (lengkap)

Dokumen ini menjelaskan **simbol flowchart standar** (sesuai materi umum / diagram referensi kuliah), **cara membaca panah dan keputusan**, serta **alur tiap diagram 01–06** yang selaras dengan kode terbaru di `src/geometri/`.

File diagram sumber: folder `presentasi/mermaid/` (bisa di-import ke draw.io lewat **Insert → Mermaid**).

---

## 1. Legenda simbol (yang dipakai di diagram project ini)

| Simbol (bentuk) | Nama (Indonesia) | Arti | Di Mermaid (file .mmd) |
|-----------------|------------------|------|-------------------------|
| Oval / stadium | **Terminator** | Mulai atau selesai suatu alur | `([teks])` |
| Persegi panjang | **Proses** | Langkah kerja di komputer (hitung, loop, assign) | `[teks]` |
| Belah ketupat | **Keputusan** | Pertanyaan ya/tidak; cabang berdasarkan kondisi | `{teks}` |
| Jajar genjang | **Input / Output** | Data masuk atau keluar (layar, parameter, return) | `[/teks/]` |
| Persegi + garis ganda | **Proses terdefinisi / subprogram** | Memanggil method atau modul lain | `[[teks]]` |
| Panah | **Alur / flow direction** | Urutan eksekusi; ikuti arah panah | `-->` |

**Simbol lain di referensi kuliah** (tidak dipakai di diagram project ini, tapi good to know): manual operation (trapesium), preparation (heksagon), dokumen, disk/tape, display khusus, connector halaman lain.

### Pemetaan bentuk Mermaid → draw.io manual

Saat menggambar di draw.io **tanpa** Mermaid, pilih bentuk yang sama dengan tabel di atas. Contoh: `([Mulai])` digambar sebagai **Terminator**, `[hitung luas]` sebagai **Process**.

---

## 2. Cara membaca flowchart (untuk belajar)

### 2.1 Aturan dasar

1. **Mulai** dari simbol **Terminator** bertuliskan “Mulai” / “Start”.
2. **Ikuti panah** ke bawah atau ke samping — jangan melawan arah panah.
3. Di **Keputusan** (belah ketupat), baca **label di cabang** (“Ya”, “Tidak”, “Konsol”, “GUI”, dll.) — itu kondisi yang harus benar agar alur masuk cabang itu.
4. **Proses** = satu langkah yang “dilakukan” program; biasanya cocok dengan satu atau beberapa baris kode.
5. **Input/Output** = data yang masuk (parameter, klik user) atau keluar (cetak ke konsol, isi tabel).
6. **Subprogram** `[[...]]` = “loncat ke detail method lain”; di project ini sering `ShapeGenerator` atau helper `random*`.
7. **Selesai** di Terminator “Selesai” / “End” — alur itu berhenti (atau kembali ke tunggu user di GUI).

### 2.2 Cabang dan loop

- **Cabang:** hanya **satu** jalur yang dijalankan per keputusan (kecuali diagram paralel — di master diagram 06, kotak “Paralel” berarti banyak thread jalan bersamaan; itu konsep khusus multithreading).
- **Loop:** panah yang **kembali** ke keputusan `i < N?` artinya ulangi sampai kondisi “Tidak”.

### 2.3 Hubungkan dengan kode Java

Setiap kotak proses sebaiknya bisa kamu tunjukkan **file + method**:
- `Main.java` → `main`, `printSummary`
- `ShapeGenerator.java` → `generateRandomShapes`
- `GeoProcessor.java` → `run`, `logLine`
- `GeometriFrame.java` → `startRun`, `runBatch`, `fillSummaryTable`

---

## 3. Urutan belajar diagram (01 → 06)

| No | File | Pelajari apa dulu | Kode utama |
|----|------|-------------------|------------|
| **01** | `01-shapegenerator.mmd` | Dari mana `List<Geometri>` dan validasi | `ShapeGenerator.java` |
| **02** | `02-sinkronisasi-log.mmd` | Kenapa log tidak campur | `GeoProcessor.logLine` |
| **03** | `03-geoprocessor.mmd` | Satu thread = satu bentuk | `GeoProcessor.run` |
| **04** | `04-polimorfisme.mmd` | Satu panggilan, banyak rumus | class bentuk + `Geometri` |
| **05** | `05-gui-edt.mmd` | GUI tidak freeze saat join | `GeometriFrame` |
| **06** | `06-alur-master-keseluruhan.mmd` | **Ringkasan penuh** — baca terakhir | `Main` + `GeometriFrame` |

---

## 4. Diagram 01 — `ShapeGenerator.generateRandomShapes`

### Alur singkat (baca sambil tunjuk diagram)

1. **Mulai** method dengan **input** `minCount`, `maxCountInclusive`.
2. **Keputusan:** `minCount >= 1?` — jika tidak → **subprogram error** (exception).
3. **Keputusan:** `max >= min?` — jika tidak → exception.
4. **Proses:** hitung `N` acak di rentang [min, max].
5. **Proses:** buat `ArrayList` kosong, `i = 0`.
6. **Loop** selama `i < N`:
   - Acak `kind` 0, 1, atau 2 → panggil helper **subprogram** bentuk 2D / limas / prisma.
   - `add` ke list, `i++`.
7. **Output:** `return` list.
8. **Selesai.**

### Kode yang cocok

```java
// ShapeGenerator.java — generateRandomShapes
int count = minCount + RND.nextInt(maxCountInclusive - minCount + 1);
for (int i = 0; i < count; i++) {
    int kind = RND.nextInt(3);
    // kind 0, 1, 2 → add bentuk berbeda
}
return shapes;
```

---

## 5. Diagram 02 — Sinkronisasi `logLine`

### Alur

1. Worker thread memanggil **logLine**.
2. **Proses:** masuk `synchronized (LOCK)` — hanya satu thread dalam blok ini.
3. **Keputusan:** `logSink == null?`
   - **Ya** → **output** ke `System.out` (mode konsol).
   - **Tidak** → **output** ke `Consumer` (mode GUI).
4. Keluar blok → thread lain boleh masuk.
5. **Selesai.**

### Mengapa penting

Tanpa `synchronized`, dua thread bisa mencetak ke `System.out` bersamaan sehingga **satu baris tercampur huruf**. Ini contoh **sinkronisasi** pada sumber daya bersama (saluran log).

---

## 6. Diagram 03 — `GeoProcessor.run` (satu worker)

### Alur

1. **Mulai** saat `Thread.start()` memanggil `run()`.
2. **Proses + output:** `displayProcessingStatus` → cetak MEMPROSES, ID thread, `getInfo()` (semua lewat `logLine` + sync).
3. **Proses:** `Thread.sleep(delayMs)`.
4. **Keputusan:** ada `InterruptedException`?
   - **Ya** → cetak ERROR, set interrupt flag, **selesai**.
   - **Tidak** → hitung empat besaran lewat `shape.hitung…()` (polimorfisme → diagram 04).
5. **Proses + output:** `displayResult` → empat angka.
6. **Selesai.**

### Delay di kode

- **Konsol (`Main`):** `800 + random(0..1199)` ms per thread.
- **GUI:** `delayMin + random(0..delayMax-delayMin)` dari spinner.

---

## 7. Diagram 04 — Polimorfisme

### Alur

1. Di `GeoProcessor`, variabel bertipe **`Geometri shape`** (bukan nama class konkret).
2. **Proses:** panggil `shape.hitungLuas()` dll.
3. **Keputusan (runtime):** objek di heap sebenarnya apa?
   - `SegiEmpatSembarang` → rumus 2D (Heron, volume 0).
   - `LimasSegiEmpatSembarang` → volume ⅓ alas×tinggi, LP limas.
   - `PrismaSegiEmpatSembarang` → volume alas×tinggi, LP prisma.
4. **Output:** nilai `double` kembali ke `GeoProcessor` → `displayResult`.

### Kalimat untuk presentasi

“Pemanggil tidak pakai `if` panjang per jenis; JVM memilih method yang sesuai objek nyata — itu **dynamic dispatch** / polimorfisme.”

---

## 8. Diagram 05 — GUI (`GeometriFrame`)

### Alur

1. **Mulai** program GUI → frame tampil (`invokeLater`).
2. **Tunggu** user.
3. **Input:** spinner + klik **Jalankan**.
4. **Keputusan:** validasi min≤max dan delay min≤max?
   - **Tidak** → **output** JOptionPane, kembali tunggu.
   - **Ya** → bersihkan log/tabel, matikan tombol jalankan, mulai **thread koordinator** `runBatch` (bukan EDT — supaya UI tidak freeze).
5. Di koordinator: log judul → **subprogram** `ShapeGenerator` → buat thread worker → `start` → **`join`** (tunggu semua).
6. **Output:** `invokeLater` isi **JTable**; pesan selesai di log.
7. **finally:** pulihkan tombol dan progress.
8. **Selesai** batch.

### EDT vs koordinator

| Thread | Boleh apa |
|--------|---------|
| **EDT** | Ubah `JTextArea`, `JTable`, tombol |
| **Koordinator** | `join`, generate list, start worker |
| **Worker** | `GeoProcessor.run` — log lewat `invokeLater` ke EDT |

---

## 9. Diagram 06 — Master (baca terakhir)

### Cara baca satu kali dari atas ke bawah

1. **Mulai program** → **keputusan mode:** `Main` (konsol) atau `GeometriFrame` (GUI).
2. **Cabang konsol:** input dari konstanta `JUMLAH_BENTUK_MIN/MAX` → langsung ke generator.
3. **Cabang GUI:** input spinner → validasi → (jika gagal, tunggu user) → bersihkan layar → generator.
4. **Subprogram generator** (detail di diagram 01) → dapat `List<Geometri>`.
5. **Loop** buat `GeoProcessor` + `Thread` untuk tiap elemen list.
6. **`start` semua** → fase **paralel** (banyak `run()` — detail diagram 03, log 02, hitung 04).
7. **`join` semua** — program tidak lanjut sebelum semua worker selesai.
8. **Keputusan keluaran:**
   - Konsol → `printSummary` ke `System.out`.
   - GUI → `fillSummaryTable` + pesan di log; `finally` pulihkan UI.
9. **Selesai** satu batch.

### Yang tidak digambar penuh di master (sengaja)

Master **merujuk** diagram 01–05 agar tidak terlalu padat. Saat presentasi: klik kotak “Paralel” → buka diagram 03; kotak “generator” → buka diagram 01.

---

## 10. Contoh membaca satu jalur (konsol)

**Skenario:** user menjalankan `java geometri.Main`.

1. Terminator Mulai → Proses cetak judul → Output info generate.
2. Subprogram `generateRandomShapes(3, 5)` → misalnya dapat N=4 bentuk.
3. Loop i=0..3: buat `GeoProcessor` + `Thread`, delay acak.
4. `start` keempat thread → **empat jalur paralel** diagram 03 (urutan log bisa campur antar thread, baris tidak rusak berkat diagram 02).
5. `join` → main thread menunggu.
6. Output `printSummary` empat baris ringkas.
7. Terminator Selesai.

---

## 11. Contoh membaca satu jalur (GUI)

1. User set spinner → Jalankan → validasi OK.
2. Koordinator: generate → 4 thread start → join.
3. Saat worker jalan, log muncul di **JTextArea** (bukan langsung dari worker — lewat `invokeLater`).
4. Setelah join, **tabel** terisi; volume 2D = `—`.
5. Tombol Jalankan aktif lagi.

---

## 12. Checklist latihan mandiri

- [ ] Tanpa melihat kode, jelaskan diagram 01 dengan 5 kalimat.
- [ ] Tunjuk di `GeoProcessor.java` baris yang cocok dengan diagram 03.
- [ ] Jelaskan beda `join` (diagram 06) dan `synchronized` (diagram 02) dalam satu paragraf.
- [ ] Gambar ulang diagram 04 di kertas dengan tiga cabang rumus.
- [ ] Jalankan program dan cocokkan **output** dengan `PENJELASAN_OUTPUT_LENGKAP.md`.

---

*Diagram di `presentasi/index.html` §8 dan file `.mmd` menggunakan notasi Mermaid di atas. Untuk laporan, bisa export dari draw.io dengan bentuk simbol resmi (Terminator, Process, Decision, I/O).*
