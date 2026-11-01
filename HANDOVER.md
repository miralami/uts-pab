# HANDOVER – QuestLog (Android, Kotlin)

Dokumen ini untuk AI coding. Baca seluruhnya sebelum menulis kode. Dokumen ini berdiri sendiri (tidak perlu PRD terpisah).

## 0. Aturan Kerja (WAJIB)

1. Kerjakan **satu task per giliran**, sesuai urutan di Bagian 8. Jangan lompat.
2. Setelah tiap task: pastikan project **compile**. Jika error, perbaiki dulu sebelum lanjut.
3. **Jangan tambah fitur** di luar dokumen ini (tanpa login, notifikasi, internet, animasi rumit, library tambahan).
4. Jangan ubah nama class, field, atau package yang sudah ditentukan di sini.
5. Kode singkat, tanpa over-engineering. Tidak perlu dependency injection framework (tanpa Hilt/Dagger/Koin).
6. Jika ada yang ambigu, pilih opsi **paling sederhana** dan tulis asumsinya sebagai komentar singkat.
7. Versi library: pakai versi stabil terbaru yang kompatibel dengan versi Android Studio dan Kotlin di project. Jangan menebak nomor versi dari ingatan, cek project yang ada.

## 1. Ringkasan Produk

QuestLog adalah aplikasi to-do bergaya RPG. Tugas = **quest**. Menyelesaikan quest memberi **XP** dan **gold**. XP menaikkan **level**. Gold dipakai membeli reward di **shop**. Semua data tersimpan lokal dengan Room. Tanpa internet.

Pengguna: mahasiswa. Satu pengguna, satu hero, tanpa akun.

## 2. Tech Stack (tetap)

| Hal | Pilihan |
|-----|---------|
| Bahasa | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigasi | Navigation Compose |
| State | ViewModel + StateFlow |
| Database | Room (pakai KSP, bukan kapt) |
| minSdk | 24 |
| Package | `com.example.questlog` |
| Arsitektur | MVVM: UI → ViewModel → Repository → DAO |

## 3. Struktur Folder

```
com.example.questlog
├── MainActivity.kt
├── data
│   ├── Entities.kt        // Quest, Hero, ShopItem, Difficulty
│   ├── Daos.kt            // QuestDao, HeroDao, ShopDao
│   ├── AppDatabase.kt     // + seed data
│   └── Repository.kt
├── logic
│   └── GameRules.kt       // fungsi murni XP/level
├── ui
│   ├── AppNav.kt          // NavHost + bottom bar
│   ├── QuestBoardScreen.kt
│   ├── QuestFormScreen.kt // add & edit
│   ├── HeroScreen.kt
│   ├── ShopScreen.kt
│   ├── ViewModels.kt      // QuestVM, HeroVM, ShopVM
│   └── theme/             // Color, Theme (default Material3 cukup)
```

## 4. Data Model

```kotlin
enum class Difficulty { EASY, MEDIUM, HARD }

@Entity(tableName = "quest")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val difficulty: String,          // simpan Difficulty.name
    val deadline: Long,              // epoch millis
    val isDone: Boolean = false
)

@Entity(tableName = "hero")
data class Hero(
    @PrimaryKey val id: Int = 1,     // selalu 1
    val name: String = "Hero",
    val level: Int = 1,
    val xp: Int = 0,
    val gold: Int = 0
)

@Entity(tableName = "shop_item")
data class ShopItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Int,
    val isOwned: Boolean = false
)
```

### Seed data (saat database pertama dibuat)

- Hero: `Hero()` (level 1, xp 0, gold 0).
- ShopItem:
  - Nonton 1 episode, 20
  - Jajan kopi, 40
  - Main game 1 jam, 60
  - Rebahan tanpa rasa bersalah, 80
  - Makan enak, 150

Gunakan `RoomDatabase.Callback.onCreate` atau cek kosong saat startup.

## 5. Aturan Game (logic/GameRules.kt)

| Difficulty | XP | Gold |
|------------|----|------|
| EASY | 10 | 5 |
| MEDIUM | 25 | 15 |
| HARD | 50 | 30 |

- XP untuk naik dari level `L` ke `L+1` = `L * 100`.
- Sisa XP dibawa ke level berikutnya (bisa naik lebih dari satu level sekaligus).

```kotlin
object GameRules {
    fun xpFor(d: Difficulty) = when (d) {
        Difficulty.EASY -> 10; Difficulty.MEDIUM -> 25; Difficulty.HARD -> 50
    }
    fun goldFor(d: Difficulty) = when (d) {
        Difficulty.EASY -> 5; Difficulty.MEDIUM -> 15; Difficulty.HARD -> 30
    }
    fun xpToNext(level: Int) = level * 100

    fun applyReward(hero: Hero, d: Difficulty): Hero {
        var xp = hero.xp + xpFor(d)
        var level = hero.level
        while (xp >= xpToNext(level)) {
            xp -= xpToNext(level)
            level++
        }
        return hero.copy(level = level, xp = xp, gold = hero.gold + goldFor(d))
    }
}
```

### Aturan lain

- Quest yang sudah `isDone = true` **tidak bisa** diselesaikan lagi dan **tidak bisa** di-undo (mencegah farming XP).
- Beli item: hanya jika `gold >= price` dan `isOwned == false`. Jika berhasil: `gold -= price`, `isOwned = true`. Jika gagal: tampilkan Snackbar "Gold tidak cukup".
- Selesaikan quest dan update hero harus dalam **satu transaksi** database.

## 6. DAO (kontrak)

```kotlin
@Dao interface QuestDao {
    @Query("SELECT * FROM quest WHERE isDone = 0 ORDER BY deadline ASC")
    fun activeQuests(): Flow<List<Quest>>
    @Query("SELECT * FROM quest WHERE id = :id") suspend fun getById(id: Int): Quest?
    @Insert suspend fun insert(q: Quest)
    @Update suspend fun update(q: Quest)
    @Delete suspend fun delete(q: Quest)
}

@Dao interface HeroDao {
    @Query("SELECT * FROM hero WHERE id = 1") fun hero(): Flow<Hero>
    @Query("SELECT * FROM hero WHERE id = 1") suspend fun getHero(): Hero
    @Update suspend fun update(h: Hero)
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(h: Hero)
    @Query("SELECT COUNT(*) FROM quest WHERE isDone = 1") fun doneCount(): Flow<Int>   // taruh di QuestDao jika lebih rapi
}

@Dao interface ShopDao {
    @Query("SELECT * FROM shop_item ORDER BY price ASC") fun items(): Flow<List<ShopItem>>
    @Update suspend fun update(i: ShopItem)
    @Insert suspend fun insertAll(items: List<ShopItem>)
    @Query("SELECT COUNT(*) FROM shop_item") suspend fun count(): Int
}
```

Repository membungkus DAO dan memuat dua fungsi utama:

- `completeQuest(quest: Quest)`: dalam `withTransaction`, tandai `isDone = true`, hitung `GameRules.applyReward`, simpan hero.
- `buyItem(item: ShopItem): Boolean`: dalam `withTransaction`, validasi gold, update hero dan item. Return `false` jika gagal.

## 7. Layar dan UI

**Gaya:** minimal dan bersih. Material 3 bawaan, tema terang, satu warna aksen (ungu atau teal), banyak whitespace, tanpa gambar, tanpa font custom. Ikon pakai `Icons.Default`.

**Navigasi:** Bottom navigation 3 tab: **Quest**, **Hero**, **Shop**. Form quest adalah layar terpisah (tanpa bottom bar), dibuka dari Quest Board.

### 7.1 Quest Board (route `quests`)

- List `LazyColumn` quest aktif, urut deadline.
- Tiap item (Card): judul, chip difficulty (EASY/MEDIUM/HARD), deadline (format `dd MMM`), checkbox untuk selesai.
- Tap card → buka form edit.
- FAB `+` → buka form tambah.
- Swipe atau tombol hapus: cukup satu cara, pilih yang paling mudah (tombol ikon hapus di card boleh).
- Jika kosong: teks "Belum ada quest. Tambah satu!".
- Saat checkbox ditekan: panggil `completeQuest`, tampilkan Snackbar `+{xp} XP, +{gold} gold`.

### 7.2 Quest Form (route `quest_form?id={id}`)

- Field: judul (wajib, tidak boleh kosong), difficulty (3 pilihan, pakai `SegmentedButton` atau `FilterChip`), deadline (DatePicker Material 3).
- Tombol **Simpan**. Judul kosong → tampilkan error di field, jangan simpan.
- Mode edit: isi field dari data quest, tombol Simpan melakukan update.

### 7.3 Hero (route `hero`)

- Nama hero (teks, boleh tanpa edit di MVP).
- `Level {n}` besar di tengah.
- Progress bar XP: `xp / xpToNext(level)` dengan label `120 / 300 XP`.
- Baris statistik: Gold, Quest selesai.

### 7.4 Shop (route `shop`)

- Header: sisa gold.
- List item: nama, harga, tombol **Beli**. Jika `isOwned`: tombol nonaktif bertuliskan "Dimiliki".
- Gold kurang: Snackbar "Gold tidak cukup".

## 8. Daftar Task (kerjakan berurutan)

Centang setelah selesai dan compile berhasil.

- [x] **T1 – Setup:** buat project Compose, tambahkan dependency Room (KSP), Navigation Compose, ViewModel Compose. Pastikan app kosong bisa jalan.
- [x] **T2 – Data layer:** `Entities.kt`, `Daos.kt`, `AppDatabase.kt` dengan seed data, `Repository.kt` (belum perlu `completeQuest`/`buyItem`).
- [x] **T3 – GameRules:** buat `GameRules.kt` persis seperti Bagian 5. Tambahkan unit test kecil: EASY dari level 1 xp 95 → level 2 xp 5; HARD dari level 1 xp 90 → level 2 xp 40.
- [x] **T4 – Navigasi:** `AppNav.kt` dengan bottom bar 3 tab dan placeholder layar kosong.
- [x] **T5 – Quest Board + Quest Form:** CRUD jalan (tambah, edit, hapus, tampil). Belum ada logika selesai.
- [x] **T6 – Complete quest:** implementasi `completeQuest` di Repository (transaksi), hubungkan ke checkbox, Snackbar reward.
- [x] **T7 – Hero screen:** tampilkan level, progress XP, gold, jumlah quest selesai dari Flow.
- [x] **T8 – Shop:** implementasi `buyItem`, UI, Snackbar gagal.
- [x] **T9 – Polish:** empty state, validasi form, urutan dan format tanggal, cek rotasi layar tidak menghilangkan data, hapus kode mati.

## 9. Kriteria Selesai (uji manual)

1. Tambah quest → muncul di Quest Board. Tutup dan buka lagi app → masih ada.
2. Selesaikan quest EASY → hero +10 XP, +5 gold; quest hilang dari board.
3. Selesaikan quest sampai XP ≥ `level*100` → level naik, sisa XP benar.
4. Hero screen menampilkan angka yang sama dengan hasil di atas.
5. Beli item dengan gold cukup → gold berkurang, item "Dimiliki". Gold kurang → ditolak.
6. Judul kosong tidak bisa disimpan.
7. Tidak ada crash saat rotasi layar atau saat list kosong.

## 10. Di Luar Scope (jangan dikerjakan)

Login, internet/API, notifikasi, widget, animasi level-up, penalti HP, quest berulang, dark mode khusus, achievement, multi-bahasa.

## 11. Cara Melapor Setelah Tiap Task

Balas singkat dengan format:

```
Task: T#
File diubah: ...
Status build: OK / ERROR (jika error, sertakan pesan)
Catatan/asumsi: ...
```
