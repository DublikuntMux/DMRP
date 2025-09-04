package com.dublikunt.rp

import com.dublikunt.rp.command.*
import com.dublikunt.rp.config.setup
import com.dublikunt.rp.leash.enableLeash
import com.dublikunt.rp.locker.enableInventoryLocker
import com.dublikunt.rp.util.checkForUpdate
import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class DMRP : JavaPlugin() {
    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        Metrics(this, 21382)
        setup()
        checkForUpdate()

        PacketEvents.getAPI().init()

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

    override fun onDisable() {
        PacketEvents.getAPI().terminate()
    }

    companion object {
        fun getInstance(): DMRP {
            return getPlugin(DMRP::class.java)
        }
    }
}
