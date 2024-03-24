package com.dublikunt.rp

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

private lateinit var configFile: File
private lateinit var languageFile: File

private lateinit var pluginConfiguration: FileConfiguration
private lateinit var languageConfiguration: FileConfiguration

var sayDistance: Int = 0
    private set
var usePlaceholders: Boolean = false
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
            configFile.createNewFile()
            DMRP.getInstance().saveResource("config.yaml", true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    if (!languageFile.exists()) {
        try {
            languageFile.createNewFile()
            DMRP.getInstance().saveResource("language.yaml", true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    reload()

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
        usePlaceholders = true
    }
}

fun reload() {
    pluginConfiguration = YamlConfiguration.loadConfiguration(configFile)
    languageConfiguration = YamlConfiguration.loadConfiguration(languageFile)

    sayDistance = pluginConfiguration.getInt("distance")
}

fun getLanguageConfiguration(): FileConfiguration {
    return languageConfiguration
}
