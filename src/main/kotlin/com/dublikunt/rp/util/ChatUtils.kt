package com.dublikunt.rp.util

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.RPConfig
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.AudienceProvider
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object ChatUtils {
    val mm = MiniMessage.miniMessage()
    private val provider: AudienceProvider = BukkitAudiences.create(DMRP.getInstance())

    fun sayDistance(sender: CommandSender, text: String) {
        if (sender is Entity) {
            say(sender.location, text)
        } else {
            say(sender, text)
        }
    }

    fun say(location: Location, text: String) {
        for (player in Bukkit.getOnlinePlayers()) {
            val playerLocation = player.location
            if (playerLocation.distanceSquared(location) <= RPConfig.settings.sayDistance * RPConfig.settings.sayDistance) {
                say(player, text)
            }
        }
    }

    fun say(player: Player, text: String) {
        val withPlaceholders: String = if (RPConfig.usePlaceholders) {
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
        val prefixed = RPConfig.languageConfiguration.getString("prefix") + " > " + text
        audience.sendMessage(mm.deserialize(prefixed))
    }
}