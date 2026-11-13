package com.dublikunt.rp.message.placeholder

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

class PlaceholderApiResolver : PlaceholderResolver {
    override fun resolve(player: Player, text: String): String {
        return PlaceholderAPI.setPlaceholders(player, text)
    }
}
