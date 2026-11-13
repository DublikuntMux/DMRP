package com.dublikunt.rp.config

import org.bukkit.Material

data class Settings(
    val sayDistance: Int,
    val update: Boolean,

    val successChance: Int,

    val maxDice: Int,
    val maxSides: Int,

    val lockDistance: Int,
    val lockItem: Material,

    val maxLeashDistance: Int,
    val leashSyncRate: Int,
    val leashItem: Material,
)
