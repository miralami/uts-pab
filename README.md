# Task Manager (UTS Pengembangan Aplikasi Bergerak - Kelompok 6)

Aplikasi Android manajemen tugas dengan Kotlin, XML + ViewBinding, RecyclerView, dan Room (SQLite). Tanpa internet/API.

## Fitur
- Register (username unik, password minimal 6 karakter) dan Login dari database Room
- Sesi login memakai SharedPreferences (`user_id`), plus Logout
- Data tiap user terpisah lewat foreign key `user_id`
- CRUD tugas: nama, prioritas, deadline, status selesai
- Checklist tugas selesai dan sortir berdasarkan prioritas
- Dashboard berisi 3 tugas terdekat deadline dan navbar bawah

## Akun demo (seed)
Saat database pertama dibuat, otomatis terisi akun `demo` / `demo123` beserta 4 tugas contoh.
Kalau sudah pernah install, uninstall dulu agar seed masuk.

## Alur
Login → Dashboard → Daftar Tugas (RecyclerView) → Form Tambah/Edit/Hapus

## Menjalankan
1. Buka folder ini di Android Studio, tunggu Gradle sync.
2. Pilih emulator/HP, lalu klik Run.

Atau lewat terminal: `./gradlew assembleDebug`

Semua nama tabel dan kelas modifikasi memakai suffix NIM `189`.
