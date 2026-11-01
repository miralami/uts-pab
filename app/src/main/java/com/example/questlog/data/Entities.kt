package com.example.questlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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
