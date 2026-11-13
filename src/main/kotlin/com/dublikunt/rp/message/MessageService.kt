package com.dublikunt.rp.message

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.placeholder.PlaceholderApiResolver
import com.dublikunt.rp.message.placeholder.PlaceholderResolver
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.Locale

class MessageService(
    private val plugin: DMRP,
    private val config: PluginConfig,
) {
    private val miniMessage = MiniMessage.miniMessage()
    private val placeholderResolver: PlaceholderResolver? = createPlaceholderResolver()

    fun raw(key: String): String {
        return config.language.message(key)
    }

    fun format(key: String, vararg args: Any): String {
        val escaped = args.map { if (it is String) escape(it) else it }.toTypedArray()
        return String.format(Locale.ROOT, raw(key), *escaped)
    }

    fun formatNamed(key: String, vararg args: Pair<String, String>): String {
        var result = raw(key)
        for ((name, value) in args) {
            result = result.replace("%$name%", escape(value))
        }
        return result
    }

    private fun escape(text: String): String {
        return miniMessage.escapeTags(text)
    }

    fun send(sender: CommandSender, text: String) {
        sender.sendMessage(render(text, sender))
    }

    fun sendNear(source: CommandSender, text: String) {
        if (source !is Entity) {
            send(source, text)
            return
        }

        val radius = config.settings.sayDistance.toDouble()
        for (player in source.world.getNearbyPlayers(source.location, radius)) {
            send(player, text)
        }
    }

    fun console(text: String) {
        val console = plugin.server.consoleSender
        console.sendMessage(render(text, console))
    }

    private fun render(text: String, audience: CommandSender): Component {
        val resolved = if (placeholderResolver != null && audience is Player) {
            placeholderResolver.resolve(audience, text)
        } else {
            text
        }
        val prefix = config.language.prefix
        return miniMessage.deserialize("$prefix > $resolved")
    }

    private fun createPlaceholderResolver(): PlaceholderResolver? {
        if (!config.usePlaceholders) {
            return null
        }
        return PlaceholderApiResolver()
    }
}
