package com.example.taskmanager

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "user_189", indices = [Index(value = ["username"], unique = true)])
data class User189(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String
)

@Entity(
    tableName = "task_189",
    foreignKeys = [ForeignKey(User189::class, ["id"], ["user_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("user_id")]
)
data class Task189(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "nama_tugas") val namaTugas: String,
    val prioritas: Int, // 1 = Rendah, 2 = Sedang, 3 = Tinggi
    val deadline: String,
    @ColumnInfo(name = "is_done") val isDone: Boolean = false
)

@Dao
interface UserDao189 {
    @Insert
    fun insert(user: User189)

    @Query("SELECT * FROM user_189 WHERE username = :username LIMIT 1")
    fun findByUsername(username: String): User189?

    @Query("SELECT * FROM user_189 WHERE username = :username AND password = :password LIMIT 1")
    fun login(username: String, password: String): User189?
}

@Dao
interface TaskDao189 {
    @Insert
    fun insert(task: Task189)

    @Update
    fun update(task: Task189)

    @Delete
    fun delete(task: Task189)

    @Query("SELECT * FROM task_189 WHERE id = :id AND user_id = :userId")
    fun getById(id: Int, userId: Int): Task189?

    @Query("SELECT * FROM task_189 WHERE user_id = :userId AND nama_tugas LIKE '%' || :search || '%' ORDER BY id DESC")
    fun getAll(userId: Int, search: String): List<Task189>

    // deadline disimpan "yyyy-MM-dd" sehingga urutan teks = urutan tanggal.
    @Query("SELECT * FROM task_189 WHERE user_id = :userId AND is_done = 0 ORDER BY deadline ASC LIMIT 3")
    fun getUpcoming(userId: Int): List<Task189>

    @Query("SELECT * FROM task_189 WHERE user_id = :userId AND nama_tugas LIKE '%' || :search || '%' ORDER BY prioritas DESC, id DESC")
    fun getAllByPriority(userId: Int, search: String): List<Task189>
}

@Database(entities = [User189::class, Task189::class], version = 1, exportSchema = false)
abstract class AppDatabase189 : RoomDatabase() {
    abstract fun userDao(): UserDao189
    abstract fun taskDao(): TaskDao189

    companion object {
        @Volatile private var instance: AppDatabase189? = null

        fun get(context: Context): AppDatabase189 = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase189::class.java, "task_189.db"
            ).allowMainThreadQueries()
                .addCallback(object : Callback() {
                    // Seed data demo: login dengan username "demo", password "demo123".
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("INSERT INTO user_189 (id, username, password) VALUES (1, 'demo', 'demo123')")
                        db.execSQL(
                            "INSERT INTO task_189 (user_id, nama_tugas, prioritas, deadline, is_done) VALUES " +
                                "(1, 'Kerjakan laporan UTS', 3, '2026-11-12', 0), " +
                                "(1, 'Belajar Room Database', 2, '2026-11-15', 0), " +
                                "(1, 'Beli buku catatan', 1, '2026-11-20', 0), " +
                                "(1, 'Install Android Studio', 2, '2026-11-05', 1)"
                        )
                    }
                })
                .build().also { instance = it }
        }
    }
}
