package com.example.questlog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Quest::class, Hero::class, ShopItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun heroDao(): HeroDao
    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "questlog.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed sinkron saat database pertama kali dibuat.
                            db.execSQL(
                                "INSERT INTO hero (id, name, level, xp, gold) " +
                                    "VALUES (1, 'Hero', 1, 0, 0)"
                            )
                            db.execSQL(
                                "INSERT INTO shop_item (name, price, isOwned) VALUES " +
                                    "('Nonton 1 episode', 20, 0), " +
                                    "('Jajan kopi', 40, 0), " +
                                    "('Main game 1 jam', 60, 0), " +
                                    "('Rebahan tanpa rasa bersalah', 80, 0), " +
                                    "('Makan enak', 150, 0)"
                            )
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
