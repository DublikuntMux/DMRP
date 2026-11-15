package com.dublikunt.rp.command

import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.concurrent.ThreadLocalRandom

class DiceCommand(
    private val config: PluginConfig,
    private val messages: MessageService,
) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        execute(sender, args)
        return true
    }

    private fun execute(sender: CommandSender, args: Array<out String>) {
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

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): List<String> {
        if (args.size == 1) {
            return listOf("1", "2", "3", "4", "5", "6", config.settings.maxDice.toString())
        } else if (args.size == 2) {
            return listOf("4", "6", "12", "20", "100", config.settings.maxSides.toString())
        }
        return emptyList()
    }
}
