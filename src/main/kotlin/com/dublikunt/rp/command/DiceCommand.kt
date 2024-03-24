package com.dublikunt.rp.command

import com.dublikunt.rp.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class DiceCommand : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (args.isEmpty()) {
                val output = String.format(
                    languageConfiguration.getString("message.dice.throw_one")!!,
                    sender.displayName
                ) + (1..6).localRandom().toString()
                say(sender.location, output)
                return true
            } else if (args.size == 1) {
                val numDices = args[0].toIntOrNull()
                if (numDices == null || numDices <= 0 || numDices > maxDices) {
                    val output = languageConfiguration.getString("message.dice.error_amount")!!
                    say(sender.location, output)
                    return false
                }
                val output = String.format(
                    languageConfiguration.getString("message.dice.throw_many")!!,
                    sender.displayName
                ) + (1..numDices).map { (1..6).localRandom() }.joinToString(", ")
                say(sender.location, output)
                return true
            } else {
                val numDices = args[0].toIntOrNull()
                val sides = args[1].toIntOrNull()
                if (numDices == null || sides == null || numDices <= 0 || sides <= 0 || sides > maxSides || numDices > maxDices) {
                    val output = languageConfiguration.getString("message.dice.error_sides")!!
                    say(sender.location, output)
                    return false
                }
                val output = if (numDices > 1) {
                    String.format(
                        languageConfiguration.getString("message.dice.throw_many")!!,
                        sender.displayName
                    ) + (1..numDices).map { (1..sides).localRandom() }.joinToString(", ")
                } else {
                    String.format(
                        languageConfiguration.getString("message.dice.throw_one")!!,
                        sender.displayName
                    ) + (1..sides).localRandom()
                }
                say(sender.location, output)
                return true
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String> {
        val complete: MutableList<String> = mutableListOf()

        if (args.size == 1) {
            complete.addAll(listOf("1", "2", "3", "4", "5", "6", maxDices.toString()))
        } else if (args.size == 2) {
            complete.addAll(listOf("4", "6", "12", "20", "100", maxSides.toString()))
        }

        return complete
    }
}
