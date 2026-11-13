package com.dublikunt.rp.config

import org.bukkit.Material

data class Settings(
    val sayDistance: Int = 30,
    val update: Boolean = true,

    val successChance: Int = 50,

    val maxDice: Int = 100,
    val maxSides: Int = 255,

    val lockDistance: Int = 5,
    val lockItem: Material = Material.IRON_CHAIN,

    val maxLeashDistance: Int = 15,
    val leashSyncRate: Int = 2,
    val leashItem: Material = Material.LEAD,
)
