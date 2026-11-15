package com.dublikunt.rp.command

import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.locker.InventoryLockService
import com.dublikunt.rp.message.MessageService
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class LockerCommand(
    private val config: PluginConfig,
    private val messages: MessageService,
    private val locks: InventoryLockService,
) : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) {
            sender.sendMessage(Component.text("This command can only be run by a player."))
            return
        }

        if (args.isEmpty()) {
            messages.send(sender, messages.raw("message.inventory_lock.error"))
            return
        }

        val target = sender.server.getPlayerExact(args[0])
        if (target == null) {
            messages.send(sender, messages.raw("message.inventory_lock.not_found"))
            return
        }

        if (sender !== target) {
            val maxDistance = config.settings.lockDistance.toDouble()
            if (sender.world !== target.world ||
                sender.location.distanceSquared(target.location) > maxDistance * maxDistance
            ) {
                messages.send(sender, messages.raw("message.inventory_lock.too_far"))
                return
            }
        }

        locks.toggle(sender, target)
    }

    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<out String>): Collection<String> {
        if (args.size == 1) {
            val partialName = args[0].lowercase()
            return commandSourceStack.sender.server.onlinePlayers
                .map { it.name }
                .filter { it.lowercase().startsWith(partialName) }
        }

        return emptyList()
    }

    override fun permission(): String {
        return "dmrp.lock.use"
    }
}
