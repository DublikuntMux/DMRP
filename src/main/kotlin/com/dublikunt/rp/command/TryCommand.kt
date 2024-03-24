package com.dublikunt.rp.command

import com.dublikunt.rp.getLanguageConfiguration
import com.dublikunt.rp.say
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.ThreadLocalRandom

class TryCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (args.isEmpty()){
                say(sender, getLanguageConfiguration().getString("message.try.error")!!)
            } else {
                val x = ThreadLocalRandom.current().nextBoolean()
                val massage: String
                val who: String = sender.displayName
                val what = args.joinToString(" ")
                massage = if (x) {
                    String.format(getLanguageConfiguration().getString("message.try.successful")!!, who, what)
                } else {
                    String.format(
                        getLanguageConfiguration().getString("message.try.unsuccessful")!!,
                        who,
                        what
                    )
                }
                say(sender.location, massage)
            }
        }
        return true
    }
}
