package com.dublikunt.rp

import com.dublikunt.rp.command.*
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.config.setup
import com.dublikunt.rp.leash.enableLeash
import com.dublikunt.rp.locker.enableInventoryLocker
import com.dublikunt.rp.util.checkForUpdate
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class DMRP : JavaPlugin() {
    override fun onEnable() {
        Metrics(this, 21382)
        setup()

        if (settings.update)
            checkForUpdate()

        getCommand("rp-reload")!!.setExecutor(ReloadCommand())

        getCommand("try")!!.setExecutor(TryCommand())
        getCommand("try")!!.tabCompleter = TryCommand()

        getCommand("dice")!!.setExecutor(DiceCommand())
        getCommand("dice")!!.tabCompleter = DiceCommand()

        getCommand("coinflip")!!.setExecutor(CoinflipCommand())

        getCommand("lockinv")!!.setExecutor(LockerCommand())
        getCommand("lockinv")!!.tabCompleter = LockerCommand()

        getCommand("leash")!!.setExecutor(LeashCommand())
        getCommand("leash")!!.tabCompleter = LeashCommand()

        enableLeash()
        enableInventoryLocker()
    }

    companion object {
        fun getInstance(): DMRP {
            return getPlugin(DMRP::class.java)
        }
    }
}
