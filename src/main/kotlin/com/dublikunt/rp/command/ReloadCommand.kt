package com.dublikunt.rp.command

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.config.reload
import com.dublikunt.rp.util.say
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        reload()

        say(languageConfiguration.getString("message.reload")!!)
        if (sender is Player) {
            say(sender, languageConfiguration.getString("message.reload")!!)
        }

        return true
    }
}
