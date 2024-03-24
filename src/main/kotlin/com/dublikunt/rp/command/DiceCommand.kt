package com.dublikunt.rp.command

import com.dublikunt.rp.getLanguageConfiguration
import com.dublikunt.rp.say
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class DiceCommand : CommandExecutor, TabExecutor {
    companion object {
        const val MAX_DICE = 100
        const val MAX_SIDES = 255
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (args.isEmpty()) {
                val output = String.format(
                    getLanguageConfiguration().getString("message.dice.throw_one")!!,
                    sender.displayName
                ) + (1..6).random().toString()
                say(sender.location, output)
                return true
            } else if (args.size == 1) {
                val numDices = args[0].toIntOrNull()
                if (numDices == null || numDices <= 0 || numDices > MAX_DICE) {
                    val output = getLanguageConfiguration().getString("message.dice.error_amount")!!
                    say(sender.location, output)
                    return false
                }
                val output = String.format(
                    getLanguageConfiguration().getString("message.dice.throw_many")!!,
                    sender.displayName
                ) + (1..numDices).map { (1..6).random() }.joinToString(", ")
                say(sender.location, output)
                return true
            } else {
                val numDices = args[0].toIntOrNull()
                val sides = args[1].toIntOrNull()
                if (numDices == null || sides == null || numDices <= 0 || sides <= 0 || numDices > MAX_DICE || sides > MAX_SIDES) {
                    val output = getLanguageConfiguration().getString("message.dice.error_sides")!!
                    say(sender.location, output)
                    return false
                }
                val output = if (numDices > 1) {
                    String.format(
                        getLanguageConfiguration().getString("message.dice.throw_many")!!,
                        sender.displayName
                    ) + (1..numDices).map { (1..sides).random() }.joinToString(", ")
                } else {
                    String.format(
                        getLanguageConfiguration().getString("message.dice.throw_one")!!,
                        sender.displayName
                    ) + (1..sides).random()
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
            complete.add("1")
            complete.add("2")
            complete.add("3")
            complete.add("4")
            complete.add("5")
            complete.add("6")
        } else if (args.size == 2) {
            complete.add("4")
            complete.add("6")
            complete.add("12")
            complete.add("20")
            complete.add("100")
        }

        return complete
    }
}
