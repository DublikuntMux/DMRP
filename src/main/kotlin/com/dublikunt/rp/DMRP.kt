package com.dublikunt.rp

import com.dublikunt.rp.command.DiceCommand
import com.dublikunt.rp.command.ReloadCommand
import com.dublikunt.rp.command.TryCommand
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class DMRP : JavaPlugin() {
    override fun onEnable() {
        setup()

        Metrics(this, 21382)

        getCommand("rp-reload")!!.setExecutor(ReloadCommand())

        getCommand("try")!!.setExecutor(TryCommand())
        
        getCommand("dice")!!.setExecutor(DiceCommand())
        getCommand("dice")!!.tabCompleter = DiceCommand()
    }

    override fun onDisable() {
        logger.info("${description.name} disabled.")
    }

    companion object {
        fun getInstance(): DMRP {
            return getPlugin(DMRP::class.java)
        }
    }
}
