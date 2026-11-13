package com.dublikunt.rp.message.placeholder

import org.bukkit.entity.Player

fun interface PlaceholderResolver {
    fun resolve(player: Player, text: String): String
}
