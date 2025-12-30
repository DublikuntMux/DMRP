package com.dublikunt.rp.command

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.util.ChatUtils
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
class ReloadCommand : BasicCommand {
    override fun execute(
        commandSourceStack: CommandSourceStack,
        args: Array<out String>
    ) {
        RPConfig.reload()
        ChatUtils.say(commandSourceStack.sender, RPConfig.languageConfiguration.getString("message.reload")!!)
    }

    override fun permission(): String {
        return "dmrp.command.reload"
    }
}
