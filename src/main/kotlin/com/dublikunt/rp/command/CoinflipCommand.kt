package com.dublikunt.rp.command

import com.dublikunt.rp.message.MessageService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.concurrent.ThreadLocalRandom

class CoinflipCommand(private val messages: MessageService) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val key = if (ThreadLocalRandom.current().nextBoolean()) {
            "message.coinflip.heads"
        } else {
            "message.coinflip.tails"
        }
        messages.sendNear(sender, messages.format(key, sender.name))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): List<String> = emptyList()
}
