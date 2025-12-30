package com.dublikunt.rp.util

object TextUtils {
    fun replacePlaceholders(input: String, placeholders: Map<String, String>): String {
        var result = input
        placeholders.forEach { (key, value) ->
            result = result.replace("%$key%", value)
        }
        return result
    }
}