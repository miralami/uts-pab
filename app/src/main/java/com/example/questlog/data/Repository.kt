package com.example.questlog.data

import androidx.room.withTransaction
import com.example.questlog.logic.GameRules
import kotlinx.coroutines.flow.Flow

class Repository(private val db: AppDatabase) {
    private val questDao = db.questDao()
    private val heroDao = db.heroDao()
    private val shopDao = db.shopDao()

    fun activeQuests(): Flow<List<Quest>> = questDao.activeQuests()
    fun doneCount(): Flow<Int> = questDao.doneCount()
    fun hero(): Flow<Hero> = heroDao.hero()
    fun shopItems(): Flow<List<ShopItem>> = shopDao.items()

    suspend fun getQuest(id: Int): Quest? = questDao.getById(id)

    suspend fun saveQuest(quest: Quest) {
        if (quest.id == 0) questDao.insert(quest) else questDao.update(quest)
    }

    suspend fun deleteQuest(quest: Quest) = questDao.delete(quest)

    /** @return false jika quest sudah selesai atau tidak ditemukan. */
    suspend fun completeQuest(quest: Quest): Boolean = db.withTransaction {
        val current = questDao.getById(quest.id) ?: return@withTransaction false
        if (current.isDone) return@withTransaction false

        questDao.update(current.copy(isDone = true))
        val hero = heroDao.getHero()
        val difficulty = Difficulty.valueOf(current.difficulty)
        heroDao.update(GameRules.applyReward(hero, difficulty))
        true
    }

    /** @return false jika gold tidak cukup atau item sudah dimiliki. */
    suspend fun buyItem(item: ShopItem): Boolean = db.withTransaction {
        val current = shopDao.getById(item.id) ?: return@withTransaction false
        val hero = heroDao.getHero()

        if (current.isOwned || hero.gold < current.price) {
            return@withTransaction false
        }

        heroDao.update(hero.copy(gold = hero.gold - current.price))
        shopDao.update(current.copy(isOwned = true))
        true
    }
}
