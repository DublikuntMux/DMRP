package com.dublikunt.rp

import com.dublikunt.rp.command.DiceCommand
import com.dublikunt.rp.command.ReloadCommand
import com.dublikunt.rp.command.TryCommand
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.config.setup
import com.dublikunt.rp.leash.enableLeash
import com.dublikunt.rp.util.checkForUpdate
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class DMRP : JavaPlugin() {
    override fun onEnable() {
        setup()

        Metrics(this, 21382)

        if (settings.update)
            checkForUpdate()

        getCommand("rp-reload")!!.setExecutor(ReloadCommand())

        getCommand("try")!!.setExecutor(TryCommand())
        getCommand("try")!!.tabCompleter = TryCommand()

        getCommand("dice")!!.setExecutor(DiceCommand())
        getCommand("dice")!!.tabCompleter = DiceCommand()

        enableLeash()
    }

    companion object {
        fun getInstance(): DMRP {
            return getPlugin(DMRP::class.java)
        }
    }
}
