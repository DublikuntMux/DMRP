package com.dublikunt.rp.util

fun replacePlaceholders(input: String, placeholders: Map<String, String>): String {
    var result = input
    placeholders.forEach { (key, value) ->
        result = result.replace("%$key%", value)
    }
    return result
}
