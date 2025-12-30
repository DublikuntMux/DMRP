package com.dublikunt.rp.command

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.util.ChatUtils
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import java.util.concurrent.ThreadLocalRandom

@Suppress("UnstableApiUsage")
class CoinflipCommand : BasicCommand {
    override fun execute(
        commandSourceStack: CommandSourceStack,
        args: Array<out String>
    ) {
        val x = ThreadLocalRandom.current().nextInt(100) + 1 <= 50
        val name = ChatUtils.mm.serialize(commandSourceStack.sender.name())
        val massage: String = if (x) {
            String.format(RPConfig.languageConfiguration.getString("message.coinflip.heads")!!, name)
        } else {
            String.format(RPConfig.languageConfiguration.getString("message.coinflip.tails")!!, name)
        }

        ChatUtils.sayDistance(commandSourceStack.sender, massage)
    }

    override fun permission(): String {
        return "dmrp.command.coinflip"
    }
}