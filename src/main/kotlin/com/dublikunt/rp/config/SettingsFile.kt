package com.dublikunt.rp.config

data object SettingsFile {
    var sayDistance: Int = 15
    var update: Boolean = true

    var successChange: Int = 50

    var maxDices: Int = 50
    var maxSides: Int = 50

    var maxLeashDistance: Int = 15

    var lockDistance: Int = 5
    var leashSyncRate: Int = 2
}