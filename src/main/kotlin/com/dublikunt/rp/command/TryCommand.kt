package com.dublikunt.rp.command

import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import java.util.concurrent.ThreadLocalRandom

@Suppress("UnstableApiUsage")
class TryCommand(
    private val config: PluginConfig,
    private val messages: MessageService,
) : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
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

    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<out String>): Collection<String> {
        return config.language.trySuggestions
    }

    override fun permission(): String {
        return "dmrp.command.try"
    }
}
