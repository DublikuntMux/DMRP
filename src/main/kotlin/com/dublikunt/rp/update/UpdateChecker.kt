package com.dublikunt.rp.update

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import com.google.gson.JsonParser
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class UpdateChecker(
    private val plugin: DMRP,
    private val config: PluginConfig,
    private val messages: MessageService,
) : AutoCloseable {
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(REQUEST_TIMEOUT)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
    private var task: ScheduledTask? = null

    fun checkAsync() {
        if (!config.settings.update) {
            return
        }

        task?.cancel()
        task = plugin.server.asyncScheduler.runNow(plugin) {
            check()
        }
    }

    private fun check() {
        val latestVersion = fetchLatestVersion() ?: return
        val currentVersion = plugin.pluginMeta.version
        if (!isNewerVersion(currentVersion, latestVersion)) {
            return
        }

        if (!plugin.isEnabled) {
            return
        }
        plugin.server.scheduler.runTask(plugin, Runnable {
            messages.console("New update available: $currentVersion -> $latestVersion")
            messages.console("Get last version: https://modrinth.com/plugin/dmrp")
        })
    }

    private fun fetchLatestVersion(): String? {
        val request = HttpRequest.newBuilder(URI.create(VERSIONS_URL))
            .timeout(REQUEST_TIMEOUT)
            .header("User-Agent", "DMRP/${plugin.pluginMeta.version}")
            .GET()
            .build()

        return try {
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() !in 200..299) {
                plugin.logger.warning("Update check failed with HTTP ${response.statusCode()}.")
                return null
            }

            val versions = JsonParser.parseString(response.body()).asJsonArray
            if (versions.size() == 0) {
                plugin.logger.warning("Update check returned no versions.")
                return null
            }
            versions.get(0).asJsonObject.get("version_number")?.asString
        } catch (exception: Exception) {
            plugin.logger.warning("Update check failed: ${exception.message}")
            null
        }
    }

    override fun close() {
        task?.cancel()
        task = null
        httpClient.shutdownNow()
    }

    companion object {
        private const val VERSIONS_URL = "https://api.modrinth.com/v2/project/dmrp/version"
        private val REQUEST_TIMEOUT: Duration = Duration.ofSeconds(10)

        internal fun isNewerVersion(currentVersion: String, latestVersion: String): Boolean {
            val currentParts = currentVersion.split('.')
            val latestParts = latestVersion.split('.')

            for (index in 0 until minOf(currentParts.size, latestParts.size)) {
                val currentPart = currentParts[index].toIntOrNull() ?: break
                val latestPart = latestParts[index].toIntOrNull() ?: break
                if (currentPart < latestPart) {
                    return true
                }
                if (currentPart > latestPart) {
                    return false
                }
            }
            return false
        }
    }
}
