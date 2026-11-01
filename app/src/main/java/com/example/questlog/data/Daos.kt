package com.example.questlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM quest WHERE isDone = 0 ORDER BY deadline ASC")
    fun activeQuests(): Flow<List<Quest>>

    @Query("SELECT * FROM quest WHERE id = :id")
    suspend fun getById(id: Int): Quest?

    @Insert
    suspend fun insert(q: Quest)

    @Update
    suspend fun update(q: Quest)

    @Delete
    suspend fun delete(q: Quest)

    @Query("SELECT COUNT(*) FROM quest WHERE isDone = 1")
    fun doneCount(): Flow<Int>
}

@Dao
interface HeroDao {
    @Query("SELECT * FROM hero WHERE id = 1")
    fun hero(): Flow<Hero>

    @Query("SELECT * FROM hero WHERE id = 1")
    suspend fun getHero(): Hero

    @Update
    suspend fun update(h: Hero)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(h: Hero)
}

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop_item ORDER BY price ASC")
    fun items(): Flow<List<ShopItem>>

    @Query("SELECT * FROM shop_item WHERE id = :id")
    suspend fun getById(id: Int): ShopItem?

    @Update
    suspend fun update(i: ShopItem)

    @Insert
    suspend fun insertAll(items: List<ShopItem>)

    @Query("SELECT COUNT(*) FROM shop_item")
    suspend fun count(): Int
}
