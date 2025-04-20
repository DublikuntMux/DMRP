package com.dublikunt.rp.command

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.locker.lockedPlayers
import com.dublikunt.rp.util.say
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class LockerCommand : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args.isNullOrEmpty()) {
            if (sender is Player) {
                say(sender, languageConfiguration.getString("message.inventory_lock.error")!!)
            }
            return true
        }

        val playerName = args[0]
        val target = Bukkit.getPlayerExact(playerName)

        if (sender is Player && target != null && sender != target) {
            val distance = sender.location.distanceSquared(target.location)
            if (distance > settings.lockDistance * settings.lockDistance) {
                say(sender, languageConfiguration.getString("message.inventory_lock.too_far")!!)
                return true
            }
        }

        if (lockedPlayers.contains(playerName)) {
            lockedPlayers.remove(playerName)
            if (sender is Player) {
                say(
                    sender.location,
                    String.format(
                        languageConfiguration.getString("message.inventory_lock.unlock")!!,
                        sender.displayName
                    )
                )
            }
        } else {
            lockedPlayers.add(playerName)
            if (sender is Player) {
                say(
                    sender.location,
                    String.format(
                        languageConfiguration.getString("message.inventory_lock.lock")!!,
                        sender.displayName
                    )
                )
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
        if (args.size == 1) {
            val partialName = args[0].lowercase()
            return sender.server.onlinePlayers
                .map { it.name }
                .filter { it.lowercase().startsWith(partialName) }
        }

        return emptyList()
    }
}
