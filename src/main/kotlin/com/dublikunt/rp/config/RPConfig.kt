package com.dublikunt.rp.config

import com.dublikunt.rp.DMPlaceholders
import com.dublikunt.rp.DMRP
import com.dublikunt.rp.leash.PlayerLeash
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

object RPConfig {
    private lateinit var configFile: File
    private lateinit var languageFile: File

    private lateinit var pluginConfiguration: FileConfiguration
    lateinit var languageConfiguration: FileConfiguration
        private set

    var usePlaceholders: Boolean = false
        private set

    @Volatile
    var settings: Settings = Settings()
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

        val newSettings = Settings(
            sayDistance = pluginConfiguration.getInt("hear_distance", 30),
            update = pluginConfiguration.getBoolean("update", true),

            successChange = pluginConfiguration.getInt("success_change", 50),

            maxDices = pluginConfiguration.getInt("max_dices", 100),
            maxSides = pluginConfiguration.getInt("max_sides", 255),

            maxLeashDistance = pluginConfiguration.getInt("leash_distance", 15),

            lockDistance = pluginConfiguration.getInt("lock_distance", 5),
            leashSyncRate = pluginConfiguration.getInt("leash_sync_rate", 2)
        )

        settings = newSettings
        PlayerLeash.reload()
    }
}
