package com.dublikunt.rp

import com.dublikunt.rp.command.*
import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.leash.PlayerLeash
import com.dublikunt.rp.locker.InventoryLocker
import com.dublikunt.rp.util.UpdateCheck
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
        PacketEvents.getAPI().init()

        RPConfig.setup()
        UpdateCheck.check()
        PlayerLeash.enable()
        InventoryLocker.enable()
        registerCommands()
    }

    @Suppress("UnstableApiUsage")
    private fun registerCommands() {
        registerCommand("rp-reload", ReloadCommand())
        registerCommand("coinflip", CoinflipCommand())
        registerCommand("try", TryCommand())
        registerCommand("dice", DiceCommand())
        registerCommand("leash", LeashCommand())
        registerCommand("lockinv", LockerCommand())
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
