package com.dublikunt.rp.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.util.Collections
import java.util.IllegalFormatException
import java.util.Locale

class LanguageSettings private constructor(
    val prefix: String,
    val trySuggestions: List<String>,
    private val messages: Map<String, String>,
) {
    fun message(key: String): String = messages[key] ?: key

    companion object {
        private const val SUGGESTIONS = "message.try.suggestion"
        private val FORMATTED_MESSAGES = setOf(
            "message.dice.throw_one",
            "message.dice.throw_many",
            "message.coinflip.heads",
            "message.coinflip.tails",
            "message.leash.unleash_owner",
            "message.leash.unleash_target",
            "message.leash.leash_owner",
            "message.leash.leash_target",
            "message.inventory_lock.unlock_owner",
            "message.inventory_lock.unlock_target",
            "message.inventory_lock.lock_owner",
            "message.inventory_lock.lock_target",
        )

        fun from(raw: YamlConfiguration, defaults: YamlConfiguration): LanguageSettings {
            val messages = LinkedHashMap<String, String>()
            var suggestions: List<String> = emptyList()

            fun readSection(section: ConfigurationSection?, fallback: ConfigurationSection, parent: String) {
                val values = section?.getValues(false).orEmpty()
                val defaultValues = fallback.getValues(false)
                for ((name, defaultValue) in defaultValues) {
                    val path = if (parent.isEmpty()) name else "$parent.$name"
                    // get()/contains() can hide nulls or consult Bukkit defaults. Inspect direct values instead.
                    val value = if (values.containsKey(name)) values[name] else defaultValue
                    when {
                        path == SUGGESTIONS -> suggestions = readSuggestions(values[name])
                        defaultValue is ConfigurationSection -> {
                            val configured = values[name]
                            if (configured != null && configured !is ConfigurationSection) {
                                throw ConfigurationException("language.yaml: '$path' must be a section.")
                            }
                            readSection(configured, defaultValue, path)
                        }

                        defaultValue is String -> {
                            if (value !is String) {
                                throw ConfigurationException("language.yaml: '$path' must be a string.")
                            }
                            validateFormat(path, value)
                            messages[path] = value
                        }

                        else -> throw ConfigurationException(
                            "Bundled language.yaml: '$path' must be a string or section."
                        )
                    }
                }

                for ((name, value) in values) {
                    if (!defaultValues.containsKey(name)) {
                        val path = if (parent.isEmpty()) name else "$parent.$name"
                        collectStrings(path, value, messages)
                    }
                }
            }

            readSection(raw, defaults, "")
            val prefix = messages["prefix"]
                ?: throw ConfigurationException("Bundled language.yaml: 'prefix' must be a string.")
            return LanguageSettings(
                prefix = prefix,
                trySuggestions = Collections.unmodifiableList(ArrayList(suggestions)),
                messages = Collections.unmodifiableMap(LinkedHashMap(messages)),
            )
        }

        private fun readSuggestions(value: Any?): List<String> {
            // Paper discards YAML null entries, so missing and null both disable optional suggestions.
            if (value == null) {
                return emptyList()
            }
            if (value !is List<*>) {
                throw ConfigurationException("language.yaml: '$SUGGESTIONS' must be a list of strings or null.")
            }
            return value.mapIndexed { index, suggestion ->
                suggestion as? String ?: throw ConfigurationException(
                    "language.yaml: '$SUGGESTIONS[$index]' must be a string."
                )
            }
        }

        private fun validateFormat(path: String, value: String) {
            if (path !in FORMATTED_MESSAGES) {
                return
            }
            try {
                String.format(Locale.ROOT, value, "player")
            } catch (exception: IllegalFormatException) {
                throw ConfigurationException(
                    "language.yaml: '$path' must be a valid format accepting one string argument: ${exception.message}",
                    exception,
                )
            }
        }

        private fun collectStrings(path: String, value: Any?, messages: MutableMap<String, String>) {
            when (value) {
                is String -> messages[path] = value
                is ConfigurationSection -> {
                    for ((name, child) in value.getValues(false)) {
                        collectStrings("$path.$name", child, messages)
                    }
                }
            }
        }
    }
}
