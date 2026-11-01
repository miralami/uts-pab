package com.example.questlog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.questlog.data.AppDatabase
import com.example.questlog.data.Difficulty
import com.example.questlog.data.Hero
import com.example.questlog.data.Quest
import com.example.questlog.data.Repository
import com.example.questlog.data.ShopItem
import com.example.questlog.logic.GameRules
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuestVM(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(AppDatabase.getInstance(app))

    val activeQuests = repo.activeQuests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    suspend fun getQuest(id: Int): Quest? = repo.getQuest(id)

    fun saveQuest(quest: Quest, onSaved: () -> Unit) {
        viewModelScope.launch {
            repo.saveQuest(quest)
            onSaved()
        }
    }

    fun deleteQuest(quest: Quest) {
        viewModelScope.launch { repo.deleteQuest(quest) }
    }

    fun completeQuest(quest: Quest) {
        viewModelScope.launch {
            if (repo.completeQuest(quest)) {
                val difficulty = Difficulty.valueOf(quest.difficulty)
                _messages.emit(
                    "+${GameRules.xpFor(difficulty)} XP, +${GameRules.goldFor(difficulty)} gold"
                )
            }
        }
    }
}

class HeroVM(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(AppDatabase.getInstance(app))

    val hero = repo.hero()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Hero())

    val doneCount = repo.doneCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}

class ShopVM(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(AppDatabase.getInstance(app))

    val items = repo.shopItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val hero = repo.hero()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Hero())

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    fun buy(item: ShopItem) {
        viewModelScope.launch {
            if (!repo.buyItem(item)) {
                _messages.emit("Gold tidak cukup")
            }
        }
    }
}
