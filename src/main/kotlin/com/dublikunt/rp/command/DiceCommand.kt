package com.dublikunt.rp.command

import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import java.util.concurrent.ThreadLocalRandom

@Suppress("UnstableApiUsage")
class DiceCommand(
    private val config: PluginConfig,
    private val messages: MessageService,
) : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        val maxDice = config.settings.maxDice
        val maxSides = config.settings.maxSides

        if (args.isEmpty()) {
            roll(sender, 1, 6)
            return
        }

        if (args.size == 1) {
            val count = args[0].toIntOrNull()
            if (count == null || count !in 1..maxDice) {
                messages.send(sender, messages.raw("message.dice.error_amount"))
                return
            }
            roll(sender, count, 6)
            return
        }

        val count = args[0].toIntOrNull()
        val sides = args[1].toIntOrNull()
        if (count == null || sides == null || count !in 1..maxDice || sides !in 1..maxSides) {
            messages.send(sender, messages.raw("message.dice.error_sides"))
            return
        }
        roll(sender, count, sides)
    }

    private fun roll(sender: CommandSender, count: Int, sides: Int) {
        val rolls = (1..count)
            .map { ThreadLocalRandom.current().nextInt(1, sides + 1) }
            .joinToString(", ")
        val key = if (count == 1) "message.dice.throw_one" else "message.dice.throw_many"
        messages.sendNear(sender, messages.format(key, sender.name) + rolls)
    }

    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<out String>): Collection<String> {
        if (args.size == 1) {
            return listOf("1", "2", "3", "4", "5", "6", config.settings.maxDice.toString())
        } else if (args.size == 2) {
            return listOf("4", "6", "12", "20", "100", config.settings.maxSides.toString())
        }
        return emptyList()
    }

    override fun permission(): String {
        return "dmrp.command.dice"
    }
}
