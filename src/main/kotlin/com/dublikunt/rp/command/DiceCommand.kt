package com.dublikunt.rp.command

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.localRandom
import com.dublikunt.rp.util.ChatUtils
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
class DiceCommand : BasicCommand {
    override fun execute(
        commandSourceStack: CommandSourceStack,
        args: Array<out String>
    ) {
        if (args.isEmpty()) {
            val output = String.format(
                RPConfig.languageConfiguration.getString("message.dice.throw_one")!!,
                ChatUtils.mm.serialize(commandSourceStack.sender.name())
            ) + (1..6).localRandom().toString()
            ChatUtils.sayDistance(commandSourceStack.sender, output)
            return
        } else if (args.size == 1) {
            val numDices = args[0].toIntOrNull()
            if (numDices == null || numDices <= 0 || numDices > RPConfig.settings.maxDices) {
                val output = RPConfig.languageConfiguration.getString("message.dice.error_amount")!!
                ChatUtils.sayDistance(commandSourceStack.sender, output)
                return
            }
            val output = String.format(
                RPConfig.languageConfiguration.getString("message.dice.throw_many")!!,
                ChatUtils.mm.serialize(commandSourceStack.sender.name())
            ) + (1..numDices).map { (1..6).localRandom() }.joinToString(", ")
            ChatUtils.sayDistance(commandSourceStack.sender, output)
            return
        } else {
            val numDices = args[0].toIntOrNull()
            val sides = args[1].toIntOrNull()
            if (numDices == null || sides == null || numDices <= 0 || sides <= 0 || sides > RPConfig.settings.maxSides || numDices > RPConfig.settings.maxDices) {
                val output = RPConfig.languageConfiguration.getString("message.dice.error_sides")!!
                ChatUtils.sayDistance(commandSourceStack.sender, output)
                return
            }
            val output = if (numDices > 1) {
                String.format(
                    RPConfig.languageConfiguration.getString("message.dice.throw_many")!!,
                    ChatUtils.mm.serialize(commandSourceStack.sender.name())
                ) + (1..numDices).map { (1..sides).localRandom() }.joinToString(", ")
            } else {
                String.format(
                    RPConfig.languageConfiguration.getString("message.dice.throw_one")!!,
                    ChatUtils.mm.serialize(commandSourceStack.sender.name())
                ) + (1..sides).localRandom()
            }
            ChatUtils.sayDistance(commandSourceStack.sender, output)
            return
        }
    }

    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<out String>): Collection<String> {
        if (args.size == 1) {
            return listOf("1", "2", "3", "4", "5", "6", RPConfig.settings.maxDices.toString())
        } else if (args.size == 2) {
            return listOf("4", "6", "12", "20", "100", RPConfig.settings.maxSides.toString())
        }
        return emptyList()
    }

    override fun permission(): String {
        return "dmrp.command.dice"
    }
}
