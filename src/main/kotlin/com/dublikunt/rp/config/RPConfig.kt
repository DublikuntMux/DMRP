package com.dublikunt.rp.config

import com.dublikunt.rp.DMPlaceholders
import com.dublikunt.rp.DMRP
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

private lateinit var configFile: File
private lateinit var languageFile: File

private lateinit var pluginConfiguration: FileConfiguration
lateinit var languageConfiguration: FileConfiguration
    private set

var usePlaceholders: Boolean = false
    private set

var settings: SettingsConfig = SettingsConfig
    private set

fun setup() {
    val dataFolder: File = DMRP.getInstance().dataFolder
    if (!dataFolder.exists()) {
        try {
            dataFolder.mkdir()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    configFile = File(dataFolder, "config.yaml")
    languageFile = File(dataFolder, "language.yaml")

    if (!configFile.exists()) {
        try {
            DMRP.getInstance().saveResource("config.yaml", false)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    if (!languageFile.exists()) {
        try {
            DMRP.getInstance().saveResource("language.yaml", false)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
        usePlaceholders = true
        DMPlaceholders().register()
    }

    reload()
}

fun reload() {
    pluginConfiguration = YamlConfiguration.loadConfiguration(configFile)
    languageConfiguration = YamlConfiguration.loadConfiguration(languageFile)

    settings.sayDistance = pluginConfiguration.getInt("hear_distance", 30)
    settings.update = pluginConfiguration.getBoolean("update", true)

    settings.successChange = pluginConfiguration.getInt("success_change", 50)

    settings.maxDices = pluginConfiguration.getInt("max_dices", 100)
    settings.maxSides = pluginConfiguration.getInt("max_sides", 255)

    settings.maxLeashDistance = pluginConfiguration.getInt("leash_distance", 15)

    settings.lockDistance = pluginConfiguration.getInt("lock_distance", 5)
}
