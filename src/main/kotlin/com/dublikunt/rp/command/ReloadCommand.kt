package com.dublikunt.rp.command

import com.dublikunt.rp.message.MessageService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ReloadCommand(
    private val reload: () -> Boolean,
    private val messages: MessageService,
) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val key = if (reload()) "message.reload" else "message.reload_failed"
        messages.send(sender, messages.raw(key))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): List<String> = emptyList()
}
