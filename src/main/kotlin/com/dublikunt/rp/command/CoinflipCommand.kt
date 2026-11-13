package com.dublikunt.rp.command

import com.dublikunt.rp.message.MessageService
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import java.util.concurrent.ThreadLocalRandom

@Suppress("UnstableApiUsage")
class CoinflipCommand(private val messages: MessageService) : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        val key = if (ThreadLocalRandom.current().nextBoolean()) {
            "message.coinflip.heads"
        } else {
            "message.coinflip.tails"
        }
        messages.sendNear(commandSourceStack.sender, messages.format(key, commandSourceStack.sender.name))
    }

    override fun permission(): String {
        return "dmrp.command.coinflip"
    }
}
