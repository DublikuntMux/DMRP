package com.dublikunt.rp.command

import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.concurrent.ThreadLocalRandom

class TryCommand(
    private val config: PluginConfig,
    private val messages: MessageService,
) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        execute(sender, args)
        return true
    }

    private fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            messages.send(sender, messages.raw("message.try.error"))
            return
        }

        val success = ThreadLocalRandom.current().nextInt(100) < config.settings.successChance
        val key = if (success) "message.try.successful" else "message.try.unsuccessful"
        val text = messages.formatNamed(
            key,
            "who" to sender.name,
            "what" to args.joinToString(" "),
        )
        messages.sendNear(sender, text)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): List<String> {
        return config.language.trySuggestions
    }
}
