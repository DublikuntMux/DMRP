package com.dublikunt.rp.command

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.util.ChatUtils
import com.dublikunt.rp.util.TextUtils
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import java.util.concurrent.ThreadLocalRandom

@Suppress("UnstableApiUsage")
class TryCommand : BasicCommand {
    override fun execute(
        commandSourceStack: CommandSourceStack,
        args: Array<out String>
    ) {
        if (args.isEmpty()) {
            ChatUtils.say(commandSourceStack.sender, RPConfig.languageConfiguration.getString("message.try.error")!!)
        } else {
            val x = ThreadLocalRandom.current().nextInt(100) + 1 <= RPConfig.settings.successChange
            val massage: String
            val who = ChatUtils.mm.serialize(commandSourceStack.sender.name())
            val what = args.joinToString(" ")
            val placeholders = mapOf(
                "who" to who,
                "what" to what
            )
            massage = if (x) {
                TextUtils.replacePlaceholders(
                    RPConfig.languageConfiguration.getString("message.try.successful")!!,
                    placeholders
                )
            } else {
                TextUtils.replacePlaceholders(
                    RPConfig.languageConfiguration.getString("message.try.unsuccessful")!!,
                    placeholders
                )
            }
            ChatUtils.sayDistance(commandSourceStack.sender, massage)
        }
    }

    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<out String>): Collection<String> {
        return RPConfig.languageConfiguration.getStringList("message.try.suggestion")
    }

    override fun permission(): String {
        return "dmrp.command.try"
    }
}
