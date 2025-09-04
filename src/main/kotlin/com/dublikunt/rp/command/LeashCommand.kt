package com.dublikunt.rp.command

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.leash.addLeashSession
import com.dublikunt.rp.leash.getLeashSession
import com.dublikunt.rp.leash.hasLeashSession
import com.dublikunt.rp.leash.removeLeashSession
import com.dublikunt.rp.util.say
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class LeashCommand : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be run by a player.")
            return true
        }

        if (args.isNullOrEmpty()) {
            say(sender, languageConfiguration.getString("message.leash.error")!!)
            return true
        }

        val target = Bukkit.getPlayerExact(args[0])
        if (target == null) {
            say(sender, languageConfiguration.getString("message.leash.not_found")!!)
            return true
        }

        if (sender != target) {
            val distance = sender.location.distanceSquared(target.location)
            if (distance > settings.maxLeashDistance * settings.maxLeashDistance) {
                say(sender, languageConfiguration.getString("message.leash.too_far")!!)
                return true
            }
        }

        if (hasLeashSession(target)) {
            val session = getLeashSession(target)!!
            if (session.owner == sender || sender.hasPermission("dmrp.leash.admin")) {
                removeLeashSession(target)
                say(
                    sender,
                    String.format(languageConfiguration.getString("message.leash.unleash_owner")!!, target.name)
                )
                say(
                    target,
                    String.format(languageConfiguration.getString("message.leash.unleash_target")!!, sender.name)
                )
            } else {
                say(sender, languageConfiguration.getString("message.leash.not_owner")!!)
            }
        } else {
            addLeashSession(sender, target)
            say(sender, String.format(languageConfiguration.getString("message.leash.leash_owner")!!, target.name))
            say(target, String.format(languageConfiguration.getString("message.leash.leash_target")!!, sender.name))
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String> {
        if (args.size == 1) {
            val partialName = args[0].lowercase()
            return sender.server.onlinePlayers
                .map { it.name }
                .filter { it.lowercase().startsWith(partialName) }
        }

        return emptyList()
    }
}
