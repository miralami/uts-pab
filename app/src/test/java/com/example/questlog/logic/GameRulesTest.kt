package com.example.questlog.logic

import com.example.questlog.data.Difficulty
import com.example.questlog.data.Hero
import org.junit.Assert.assertEquals
import org.junit.Test

class GameRulesTest {

    @Test
    fun easyFromLevel1With95Xp_levelsUpAndCarriesRemainder() {
        val result = GameRules.applyReward(Hero(level = 1, xp = 95), Difficulty.EASY)

        assertEquals(2, result.level)
        assertEquals(5, result.xp)
        assertEquals(5, result.gold)
    }

    @Test
    fun hardFromLevel1With90Xp_levelsUpAndCarriesRemainder() {
        val result = GameRules.applyReward(Hero(level = 1, xp = 90), Difficulty.HARD)

        assertEquals(2, result.level)
        assertEquals(40, result.xp)
        assertEquals(30, result.gold)
    }
}
