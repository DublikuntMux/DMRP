package com.dublikunt.rp.command

import com.dublikunt.rp.languageConfiguration
import com.dublikunt.rp.replacePlaceholders
import com.dublikunt.rp.say
import com.dublikunt.rp.successChange
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.concurrent.ThreadLocalRandom

class TryCommand : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (args.isEmpty()) {
                say(sender, languageConfiguration.getString("message.try.error")!!)
            } else {
                val x = ThreadLocalRandom.current().nextInt(100) + 1 <= successChange
                val massage: String
                val who: String = sender.displayName
                val what = args.joinToString(" ")
                val placeholders = mapOf(
                    "who" to who,
                    "what" to what
                )
                massage = if (x) {
                    replacePlaceholders(languageConfiguration.getString("message.try.successful")!!, placeholders)
                } else {
                    replacePlaceholders(languageConfiguration.getString("message.try.unsuccessful")!!, placeholders)
                }
                say(sender.location, massage)
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): MutableList<String> {
        val complete: MutableList<String> = mutableListOf()
        if (args.size == 1) {
            complete.addAll(languageConfiguration.getStringList("message.try.suggestion"))
        }

        return complete
    }
}
