package com.dublikunt.rp.command

import com.dublikunt.rp.message.MessageService
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
class ReloadCommand(
    private val reload: () -> Unit,
    private val messages: MessageService,
) : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        reload()
        messages.send(commandSourceStack.sender, messages.raw("message.reload"))
    }

    override fun permission(): String {
        return "dmrp.command.reload"
    }
}
