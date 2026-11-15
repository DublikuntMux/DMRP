package com.dublikunt.rp.command

import com.dublikunt.rp.message.MessageService
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack

class ReloadCommand(
    private val reload: () -> Boolean,
    private val messages: MessageService,
) : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val key = if (reload()) "message.reload" else "message.reload_failed"
        messages.send(commandSourceStack.sender, messages.raw(key))
    }

    override fun permission(): String {
        return "dmrp.command.reload"
    }
}
