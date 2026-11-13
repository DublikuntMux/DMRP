package com.dublikunt.rp.config

import com.dublikunt.rp.DMRP
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class PluginConfig(private val plugin: DMRP) {
    @Volatile
    var settings: Settings = Settings()
        private set

    @Volatile
    var language: YamlConfiguration = YamlConfiguration()
        private set

    @Volatile
    var usePlaceholders: Boolean = false
        private set

    fun load() {
        val dataFolder = plugin.dataFolder
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.logger.warning("Could not create the plugin data folder at ${dataFolder.absolutePath}")
        }

        val configFile = File(dataFolder, CONFIG_FILE)
        val languageFile = File(dataFolder, LANGUAGE_FILE)
        if (!configFile.exists()) {
            plugin.saveResource(CONFIG_FILE, false)
        }
        if (!languageFile.exists()) {
            plugin.saveResource(LANGUAGE_FILE, false)
        }

        val raw = YamlConfiguration.loadConfiguration(configFile)
        settings = Settings(
            sayDistance = raw.getInt("hear_distance", 30).coerceAtLeast(0),
            update = raw.getBoolean("update", true),
            successChance = raw.getInt("success_change", 50).coerceIn(0, 100),
            maxDice = raw.getInt("max_dices", 100).coerceIn(1, MAX_DICE),
            maxSides = raw.getInt("max_sides", 255).coerceIn(1, MAX_SIDES),
            lockDistance = raw.getInt("lock_distance", 5).coerceAtLeast(0),
            lockItem = raw.material("lock_item", Material.IRON_CHAIN),
            maxLeashDistance = raw.getInt("leash_distance", 15).coerceAtLeast(0),
            leashSyncRate = raw.getInt("leash_sync_rate", 2).coerceIn(1, 200),
            leashItem = raw.material("leash_item", Material.LEAD),
        )
        language = YamlConfiguration.loadConfiguration(languageFile)
        usePlaceholders = plugin.server.pluginManager.getPlugin(PLACEHOLDER_API) != null
    }

    private fun YamlConfiguration.material(path: String, default: Material): Material {
        val configured = getString(path)?.trim().orEmpty()
        if (configured.isEmpty()) {
            return default
        }

        val material = Material.matchMaterial(configured)
        if (material == null || material.isAir) {
            plugin.logger.warning(
                "Invalid item material '$configured' at '$path' in config.yaml; using ${default.name}."
            )
            return default
        }
        return material
    }

    private companion object {
        const val CONFIG_FILE = "config.yaml"
        const val LANGUAGE_FILE = "language.yaml"
        const val PLACEHOLDER_API = "PlaceholderAPI"
        const val MAX_DICE = 10_000
        const val MAX_SIDES = 100_000
    }
}
