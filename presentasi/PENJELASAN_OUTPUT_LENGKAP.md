# Penjelasan output program — lengkap

Dokumen ini menjelaskan **teks yang muncul di layar** saat menjalankan project Geometri (mode **konsol** `Main` dan mode **GUI** `GeometriFrame`): arti tiap baris, format angka, dan beda antara bentuk 2D dan 3D. Cocok untuk laporan, presentasi, atau membaca log hasil uji.

---

## 1. Prinsip umum

- **Satu bentuk geometri = satu thread** (`GeoProcessor`). Tiap thread mencetak blok **MEMPROSES** lalu **HASIL PERHITUNGAN** (kecuali di-interrupt).
- **Angka** ditampilkan dengan **dua desimal** (`%.2f`, locale US di tabel GUI): misalnya `12,34` di layar konsol Windows bisa tampil dengan koma tergantung regional; di kode pemformatan memakai titik.
- **Urutan blok antar thread** di konsol **tidak dijamin** sama tiap run: thread mana selesai duluan bergantung pada OS dan `delay` acak. Yang dijamin: setelah semua `join`, barulah **ringkasan akhir** (konsol) atau **tabel** (GUI) muncul.
- **Sinkronisasi** pada `logLine` menjaga satu baris log tidak tercampur huruf antar-thread; baris tetap bisa **bergantian** antar-thread (blok A lalu blok B, dst.).

---

## 2. Mode konsol (`Main.java`)

### 2.1 Baris pembuka (sebelum thread)

| Output | Arti |
|--------|------|
| `=== SISTEM PERHITUNGAN GEOMETRI MULTITHREADING ===` | Judul program. |
| `[INFO] Menggenerate bentuk geometri acak...` | Akan memanggil `ShapeGenerator.generateRandomShapes`. |
| `[OK] Berhasil membuat N bentuk geometri` | **N** = jumlah objek di list (acak antara `JUMLAH_BENTUK_MIN` dan `JUMLAH_BENTUK_MAX` di kode). |
| `[INFO] Memulai pemrosesan dengan multithreading...` | Penjelasan bahwa pemrosesan paralel dimulai. |
| `[INFO] Memulai N threads...` | **N** worker thread (`GeoWorker-1` …) akan di-`start`. |

### 2.2 Blok log per thread (`GeoProcessor`)

Setiap worker mencetak pola berikut (lewat `System.out.println`).

**Baris kosong** — pemisah visual antar blok.

**Garis pemisah** — enam puluh karakter `=` (bukan garis `------------------------------------------------------------` seperti di GUI; itu khusus tampilan log di Swing).

**Bagian “MEMPROSES”**

| Baris | Arti |
|--------|------|
| `MEMPROSES: <NamaClass>` | Nama class Java bentuk yang diproses, mis. `SegiEmpatSembarang`, `LimasSegiEmpatSembarang`, `PrismaSegiEmpatSembarang`. |
| `Thread ID: Thread-k` | **k** = indeks 1-based (`Thread-1` … `Thread-N`), sama dengan label yang dipakai di hasil. |
| `Info: ...` | Teks dari `shape.getInfo()`: ringkasan parameter bentuk (sisi, sudut, tinggi, dll.) — format berbeda per jenis bentuk (lihat §5). |

**Jeda** — `Thread.sleep(delayMs)` dengan `delayMs` acak per thread (800–1999 ms di `Main`); selama ini belum ada baris “HASIL”.

**Bagian “HASIL PERHITUNGAN (Thread-k)”**

| Baris | Arti |
|--------|------|
| `Keliling: x.xx` | Keliling alas / keliling bentuk 2D (jumlah sisi). Untuk limas/prisma, sama dengan keliling segi empat alas. |
| `Luas: x.xx` | Untuk **2D**: luas bidang. Untuk **limas/prisma**: di program ini sama dengan **luas alas** (bukan luas seluruh permukaan 3D). |
| `Volume: x.xx` | Untuk **2D**: selalu **0,00** (bangun planar). Untuk **limas**: volume limas. Untuk **prisma**: volume prisma. |
| `Luas Permukaan: x.xx` | Untuk **2D**: sama dengan luas bidang. Untuk **limas/prisma**: luas permukaan total (alas + sisi tegak/selimut). |

Lalu lagi **60 tanda `=`** menutup blok.

### 2.3 Jika thread di-interrupt

```
[ERROR] Thread-k interrupted!
```

Muncul jika `Thread.sleep` terganggu `InterruptedException` (misalnya tombol hentikan di GUI, atau pemanggilan interrupt lain). Thread menandai interrupt dan **tidak** mencetak blok “HASIL PERHITUNGAN” lengkap untuk batch itu.

### 2.4 Ringkasan akhir (`printSummary`)

Setelah **semua** thread selesai `join`:

```
============================================================
RINGKASAN AKHIR (main thread)
============================================================
1. <NamaClass> | luas=a,bb, keliling=c,dd, volume=e,ee, luas permukaan=f,ff
2. ...
```

- Dicetak di **thread utama**, dengan `synchronized (System.out)` agar tidak bentrok dengan sisa output.
- Satu baris per bentuk **sesuai urutan di list** (bukan urutan thread selesai).
- **Volume** untuk 2D tetap ditampilkan angka **0,00** di ringkasan konsol (beda dengan GUI yang memakai `—` di tabel).

### 2.5 Penutup

```
[OK] Semua perhitungan selesai!
```

---

## 3. Mode GUI (`GeometriFrame`)

### 3.1 Area atas: log thread

**Baris dari koordinator** (sebelum worker sama seperti konsol secara logika):

| Contoh | Arti |
|--------|------|
| `=== Geometri multithreading ===` | Judul batch (bukan sama persis dengan judul konsol). |
| `[INFO] Membangkitkan bentuk acak…` | Generator dipanggil. |
| `[OK] N bentuk siap — tiap bentuk = satu thread.` | **N** objek siap. |
| `[INFO] Worker dimulai…` | Worker akan `start`. |

**Isi per worker** sama strukturnya dengan §2.2 (`GeoProcessor`), tetapi:

- Teks dikirim lewat `Consumer` → `appendLogLine` di EDT.
- **Garis panjang hanya `=`** (≥12 karakter) diganti tampilan menjadi garis tetap `------------------------------------------------------------` agar lebar konsisten.
- Baris yang diawali pola ringkasan konsol tertentu **disembunyikan** dari area log (agar tidak dobel dengan tabel).

**Setelah join:**

| Baris | Arti |
|--------|------|
| `[INFO] Ringkasan angka ada di tabel di bawah.` | Angka rapi ada di `JTable`. |
| `[OK] Semua thread selesai.` | Batch selesai; tombol **Jalankan** aktif lagi (via `finally`). |

### 3.2 Area bawah: tabel ringkasan

Header kolom:

| Kolom | Isi |
|--------|-----|
| `#` | Nomor urut 1 … N sesuai list. |
| `Bentuk` | `getClass().getSimpleName()` — nama class singkat. |
| `Luas (alas / 2D)` | `hitungLuas()`: untuk 2D = luas bidang; untuk 3D = luas alas. |
| `Keliling alas` | `hitungKeliling()`: keliling alas / keliling 2D. |
| `Volume` | Untuk **`SegiEmpatSembarang`**: tampilan **`—`** (bukan 0,00). Untuk limas/prisma: nilai volume dua desimal. |
| `Luas permukaan` | `hitungLuasPermukaan()`. |

### 3.3 Tombol **Salin log + tabel**

Menyalin ke clipboard: bagian `--- LOG ---` (isi `JTextArea`), lalu `--- TABEL RINGKASAN ---` dengan header dan baris dipisah **tab** (`\t`) agar mudah tempel ke spreadsheet.

### 3.4 Tombol **Hentikan**

Memanggil `interrupt()` pada worker yang masih hidup → worker yang sedang `sleep` dapat mencetak `[ERROR] Thread-k interrupted!` seperti di §2.3.

---

## 4. Makna angka per jenis bentuk (ringkas)

| Besaran | Segi empat 2D | Limas | Prisma tegak |
|---------|----------------|-------|----------------|
| Luas (kolom / baris “Luas”) | Luas segi empat | Luas alas | Luas alas |
| Keliling | a+b+c+d | Keliling alas | Keliling alas |
| Volume | 0 (konsol) / — (GUI tabel) | (1/3)×luas alas×tinggi limas | luas alas×tinggi prisma |
| Luas permukaan | = luas bidang | alas + Σ luas sisi miring | 2×alas + keliling×tinggi |

---

## 5. Contoh format baris `Info:` (`getInfo()`)

- **Segi empat sembarang:** `Segi empat sembarang (2D) | sisi a=…,b=…,c=…,d=… | sudut ° A–D: …`
- **Limas:** menyertakan teks alas + `tinggi limas=` + `tinggi sisi tegak a–d=…`
- **Prisma:** alas + `tinggi prisma=…`

Detail angka bergantung pada nilai yang dibuat `ShapeGenerator` atau input konstruktor.

---

## 6. File terkait di kode

| Perilaku output | File / method |
|-----------------|---------------|
| Judul & ringkasan konsol | `Main.main`, `Main.printSummary` |
| Isi log per thread | `GeoProcessor.run`, `displayProcessingStatus`, `displayResult`, `logLine` |
| Pesan batch GUI | `GeometriFrame.runBatch` |
| Penyesuaian tampilan log | `GeometriFrame.appendLogLine` |
| Isi tabel | `GeometriFrame.fillSummaryTable`, `volumeCell` |
| Teks `getInfo` per bentuk | `SegiEmpatSembarang`, `LimasSegiEmpatSembarang`, `PrismaSegiEmpatSembarang` |

---

*Dokumen ini selaras dengan kode di `src/geometri/` pada saat penulisan. Jika format string diubah di `GeoProcessor` atau `Main`, sesuaikan penjelasan di atas.*
