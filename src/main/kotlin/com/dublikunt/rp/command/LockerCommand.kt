package com.dublikunt.rp.command

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.locker.addLockSession
import com.dublikunt.rp.locker.getLockSession
import com.dublikunt.rp.locker.hasLockSession
import com.dublikunt.rp.locker.removeLockSession
import com.dublikunt.rp.util.say
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class LockerCommand : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be run by a player.")
            return true
        }

        if (!sender.hasPermission("dmrp.lock.use")) {
            say(sender, languageConfiguration.getString("message.inventory_lock.no_permission")!!)
            return true
        }

        if (args.isNullOrEmpty()) {
            say(sender, languageConfiguration.getString("message.inventory_lock.error")!!)
            return true
        }

        val target = Bukkit.getPlayerExact(args[0])
        if (target == null) {
            say(sender, languageConfiguration.getString("message.inventory_lock.not_found")!!)
            return true
        }

        if (sender != target) {
            val distance = sender.location.distanceSquared(target.location)
            if (distance > settings.lockDistance * settings.lockDistance) {
                say(sender, languageConfiguration.getString("message.inventory_lock.too_far")!!)
                return true
            }
        }

        if (hasLockSession(target)) {
            val session = getLockSession(target)!!
            if (session.owner == sender || sender.hasPermission("dmrp.lock.admin")) {
                removeLockSession(target)
                say(
                    sender,
                    String.format(languageConfiguration.getString("message.inventory_lock.unlock_owner")!!, target.name)
                )
                say(
                    target,
                    String.format(
                        languageConfiguration.getString("message.inventory_lock.unlock_target")!!,
                        sender.name
                    )
                )
            } else {
                say(sender, languageConfiguration.getString("message.inventory_lock.not_owner")!!)
            }
        } else {
            if (!target.hasPermission("dmrp.lock.can")) {
                say(sender, languageConfiguration.getString("message.inventory_lock.cannot_lock")!!)
                return true
            }
            addLockSession(sender, target)
            say(
                sender,
                String.format(languageConfiguration.getString("message.inventory_lock.lock_owner")!!, target.name)
            )
            say(
                target,
                String.format(languageConfiguration.getString("message.inventory_lock.lock_target")!!, sender.name)
            )
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
