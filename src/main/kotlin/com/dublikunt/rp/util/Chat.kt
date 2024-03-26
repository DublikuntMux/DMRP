package com.dublikunt.rp.util

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.config.usePlaceholders
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.AudienceProvider
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

private val mm = MiniMessage.miniMessage()
private val provider: AudienceProvider = BukkitAudiences.create(DMRP.getInstance())

fun say(location: Location, text: String) {
    for (player in Bukkit.getOnlinePlayers()) {
        val playerLocation = player.location
        if (playerLocation.distanceSquared(location) <= settings.sayDistance * settings.sayDistance) {
            say(player, text)
        }
    }
}

fun say(player: Player, text: String) {
    val withPlaceholders: String = if (usePlaceholders) {
        PlaceholderAPI.setPlaceholders(player, text)
    } else {
        text
    }
    say(provider.player(player.uniqueId), withPlaceholders)
}

fun say(text: String) {
    say(provider.console(), text)
}

fun say(audience: Audience, text: String) {
    val prefixed = languageConfiguration.getString("prefix") + " > " + text
    audience.sendMessage(mm.deserialize(prefixed))
}
