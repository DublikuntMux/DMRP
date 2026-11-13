package com.dublikunt.rp.config

import org.bukkit.Material
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files

internal data class ConfigurationSnapshot(
    val settings: Settings,
    val language: LanguageSettings,
)

internal class ConfigurationLoader(
    private val dataFolder: File,
    private val resource: (String) -> InputStream?,
    private val isItem: (Material) -> Boolean = { it.isItem },
) {
    fun load(): ConfigurationSnapshot {
        try {
            Files.createDirectories(dataFolder.toPath())
        } catch (exception: IOException) {
            throw ConfigurationException(
                "Could not create plugin directory '$dataFolder': ${exception.message}",
                exception
            )
        }

        val configDefaults = readDefaults(CONFIG_FILE)
        val languageDefaults = readDefaults(LANGUAGE_FILE)
        val raw = readFile(CONFIG_FILE)
        val language = readFile(LANGUAGE_FILE)
        val values = ConfigValues(raw, configDefaults, isItem)
        values.integer("config-version", 1..1)

        val settings = Settings(
            sayDistance = values.integer("hear_distance", 0..Int.MAX_VALUE),
            update = values.boolean("update"),
            successChance = values.integer("success_change", 0..100),
            maxDice = values.integer("max_dices", 1..10_000),
            maxSides = values.integer("max_sides", 1..100_000),
            lockDistance = values.integer("lock_distance", 0..Int.MAX_VALUE),
            lockItem = values.material("lock_item"),
            maxLeashDistance = values.integer("leash_distance", 0..Int.MAX_VALUE),
            leashSyncRate = values.integer("leash_sync_rate", 1..200),
            leashItem = values.material("leash_item"),
        )
        return ConfigurationSnapshot(settings, LanguageSettings.from(language, languageDefaults))
    }

    private fun readDefaults(name: String): YamlConfiguration {
        return parse("Bundled $name") { yaml ->
            openResource(name).bufferedReader(UTF_8).use { yaml.load(it) }
        }
    }

    private fun readFile(name: String): YamlConfiguration {
        val file = File(dataFolder, name)
        return parse(name) { yaml ->
            if (!file.exists()) {
                openResource(name).use { Files.copy(it, file.toPath()) }
            }
            file.reader(UTF_8).use { yaml.load(it) }
        }
    }

    private fun openResource(name: String): InputStream {
        return resource(name) ?: throw ConfigurationException("Missing bundled resource '$name'.")
    }

    private fun parse(name: String, read: (YamlConfiguration) -> Unit): YamlConfiguration {
        try {
            return YamlConfiguration().also(read)
        } catch (exception: InvalidConfigurationException) {
            throw ConfigurationException("$name: invalid YAML: ${exception.message}", exception)
        } catch (exception: IOException) {
            throw ConfigurationException("$name: could not read or create file: ${exception.message}", exception)
        }
    }

    private class ConfigValues(
        private val raw: YamlConfiguration,
        private val defaults: YamlConfiguration,
        private val isItem: (Material) -> Boolean,
    ) {
        fun integer(path: String, range: IntRange): Int {
            val value = value(path)
            if (value !is Int || value !in range) {
                throw ConfigurationException("config.yaml: '$path' must be an integer in ${range.first}..${range.last}; got '$value'.")
            }
            return value
        }

        fun boolean(path: String): Boolean {
            val value = value(path)
            return value as? Boolean
                ?: throw ConfigurationException("config.yaml: '$path' must be true or false; got '$value'.")
        }

        fun material(path: String): Material {
            val value = value(path)
            val material = (value as? String)?.trim()?.takeIf { it.isNotEmpty() }?.let(Material::matchMaterial)
            if (material == null || material in AIR_MATERIALS || !isItem(material)) {
                throw ConfigurationException("config.yaml: '$path' must name a non-air item material; got '$value'.")
            }
            return material
        }

        private fun value(path: String): Any {
            return raw.get(path) ?: defaults.get(path)
            ?: throw ConfigurationException("Bundled config.yaml is missing '$path'.")
        }
    }

    private companion object {
        const val CONFIG_FILE = "config.yaml"
        const val LANGUAGE_FILE = "language.yaml"
        val AIR_MATERIALS = setOf(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR)
    }
}
