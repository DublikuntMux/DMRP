package com.dublikunt.rp.command

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.util.say
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.ThreadLocalRandom

class CoinflipCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val x = ThreadLocalRandom.current().nextInt(100) + 1 <= 50
            val massage: String = if (x) {
                String.format(languageConfiguration.getString("message.coinflip.heads")!!, sender.displayName)
            } else {
                String.format(languageConfiguration.getString("message.coinflip.tails")!!, sender.displayName)
            }
            say(sender.location, massage)
        }
        return true
    }
}