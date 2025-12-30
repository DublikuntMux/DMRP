package com.dublikunt.rp.util

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.RPConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object UpdateCheck {
    private val client = OkHttpClient()

    @Serializable
    data class ModrinthVersion(
        @SerialName("version_number")
        val versionNumber: String
    )

    fun getLatestVersion(): String {
        val request = Request.Builder()
            .url("https://api.modrinth.com/v2/project/dmrp/version")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val responseBody = response.body?.string() ?: throw IOException("Empty response body")

            val json = Json {
                ignoreUnknownKeys = true
            }
            val versions = json.decodeFromString<List<ModrinthVersion>>(responseBody)
            if (versions.isNotEmpty()) {
                return versions[0].versionNumber
            } else {
                throw IOException("No versions found")
            }
        }
    }

    fun isNewerVersion(currentVersion: String, latestVersion: String): Boolean {
        val currentParts = currentVersion.split('.')
        val latestParts = latestVersion.split('.')

        for (i in 0 until minOf(currentParts.size, latestParts.size)) {
            val currentPart = currentParts[i].toIntOrNull()
            val latestPart = latestParts[i].toIntOrNull()

            if (currentPart != null && latestPart != null) {
                if (currentPart < latestPart) {
                    return true
                } else if (currentPart > latestPart) {
                    return false
                }
            }
        }

        return false
    }

    @Suppress("UnstableApiUsage")
    fun check() {
        if (!RPConfig.settings.update)
            return

        val lastVersion = getLatestVersion()
        val currentVersion = DMRP.getInstance().pluginMeta.version

        if (isNewerVersion(currentVersion, lastVersion)) {
            ChatUtils.say("New update available: $currentVersion -> $lastVersion")
            ChatUtils.say("Get last version: https://modrinth.com/plugin/dmrp")
        }
    }
}