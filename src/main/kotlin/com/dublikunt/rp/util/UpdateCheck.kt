package com.dublikunt.rp.util

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

private val client = OkHttpClient()

fun getLatestVersion(): String {
    val request = Request.Builder()
        .url("https://api.modrinth.com/v2/project/dmrp/version")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val responseBody = response.body?.string()
        val jsonArray = JSONArray(responseBody)

        if (jsonArray.length() > 0) {
            val jsonObject = jsonArray.getJSONObject(0)
            return jsonObject.getString("version_number")
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

fun checkForUpdate() {
    val lastVersion = getLatestVersion()
    val currentVersion = "1.3.0"

    if (isNewerVersion(currentVersion, lastVersion)) {
        say("New update available: $currentVersion -> $lastVersion")
    }
}
