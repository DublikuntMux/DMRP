package com.dublikunt.rp.command

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.leash.PlayerLeash
import com.dublikunt.rp.util.ChatUtils
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
class LeashCommand : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val sender = commandSourceStack.sender
        if (sender !is Player) {
            sender.sendMessage("This command can only be run by a player.")
            return
        }

        if (args.isEmpty()) {
            ChatUtils.say(sender, RPConfig.languageConfiguration.getString("message.leash.error")!!)
            return
        }

        val target = Bukkit.getPlayerExact(args[0])
        if (target == null) {
            ChatUtils.say(sender, RPConfig.languageConfiguration.getString("message.leash.not_found")!!)
            return
        }

        if (sender != target) {
            val distance = sender.location.distanceSquared(target.location)
            if (distance > RPConfig.settings.maxLeashDistance * RPConfig.settings.maxLeashDistance) {
                ChatUtils.say(sender, RPConfig.languageConfiguration.getString("message.leash.too_far")!!)
                return
            }
        }

        if (PlayerLeash.hasSession(target)) {
            val session = PlayerLeash.getSession(target)!!
            if (session.owner == sender || sender.hasPermission("dmrp.leash.admin")) {
                PlayerLeash.removeSession(target)
                ChatUtils.say(
                    sender,
                    String.format(
                        RPConfig.languageConfiguration.getString("message.leash.unleash_owner")!!,
                        ChatUtils.mm.serialize(target.name())
                    )
                )
                ChatUtils.say(
                    target,
                    String.format(
                        RPConfig.languageConfiguration.getString("message.leash.unleash_target")!!,
                        ChatUtils.mm.serialize(sender.name())
                    )
                )
            } else {
                ChatUtils.say(sender, RPConfig.languageConfiguration.getString("message.leash.not_owner")!!)
            }
        } else {
            PlayerLeash.addSession(sender, target)
            ChatUtils.say(
                sender,
                String.format(
                    RPConfig.languageConfiguration.getString("message.leash.leash_owner")!!,
                    ChatUtils.mm.serialize(target.name())
                )
            )
            ChatUtils.say(
                target,
                String.format(
                    RPConfig.languageConfiguration.getString("message.leash.leash_target")!!,
                    ChatUtils.mm.serialize(sender.name())
                )
            )
        }
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
        return "dmrp.leash.use"
    }
}
