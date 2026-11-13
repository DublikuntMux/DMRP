package com.dublikunt.rp.config

import com.dublikunt.rp.DMRP

class PluginConfig internal constructor(
    private val loader: ConfigurationLoader,
    val usePlaceholders: Boolean = false,
) {
    constructor(plugin: DMRP) : this(
        ConfigurationLoader(plugin.dataFolder, plugin::getResource),
        plugin.server.pluginManager.getPlugin("PlaceholderAPI") != null,
    )

    @Volatile
    private lateinit var snapshot: ConfigurationSnapshot

    val settings: Settings
        get() = snapshot.settings

    val language: LanguageSettings
        get() = snapshot.language

    fun load(beforeApply: (Settings) -> Unit = {}) {
        val candidate = loader.load()
        // Update dependent tasks before publishing; a failure keeps the previous snapshot intact.
        beforeApply(candidate.settings)
        snapshot = candidate
    }
}
