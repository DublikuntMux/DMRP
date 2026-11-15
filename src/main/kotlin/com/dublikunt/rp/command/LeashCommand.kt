package com.dublikunt.rp.command

import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import com.dublikunt.rp.leash.LeashService
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class LeashCommand(
    private val config: PluginConfig,
    private val messages: MessageService,
    private val leashes: LeashService,
) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        execute(sender, args)
        return true
    }

    private fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) {
            sender.sendMessage(Component.text("This command can only be run by a player."))
            return
        }

        if (args.isEmpty()) {
            messages.send(sender, messages.raw("message.leash.error"))
            return
        }

        val target = sender.server.getPlayerExact(args[0])
        if (target == null) {
            messages.send(sender, messages.raw("message.leash.not_found"))
            return
        }

        val maxDistance = config.settings.maxLeashDistance.toDouble()
        if (sender.world !== target.world ||
            sender.location.distanceSquared(target.location) > maxDistance * maxDistance
        ) {
            messages.send(sender, messages.raw("message.leash.too_far"))
            return
        }

        leashes.toggle(sender, target)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
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
