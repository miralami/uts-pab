package com.example.questlog.logic

import com.example.questlog.data.Difficulty
import com.example.questlog.data.Hero

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
