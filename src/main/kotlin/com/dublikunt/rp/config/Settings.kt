package com.dublikunt.rp.config

data class Settings(
    val sayDistance: Int = 30,
    val update: Boolean = true,

    val successChange: Int = 50,

    val maxDices: Int = 100,
    val maxSides: Int = 255,

    val maxLeashDistance: Int = 15,

    val lockDistance: Int = 5,
    val leashSyncRate: Int = 2
)
