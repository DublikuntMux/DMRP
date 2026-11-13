package com.dublikunt.rp

import com.dublikunt.rp.command.CoinflipCommand
import com.dublikunt.rp.command.DiceCommand
import com.dublikunt.rp.command.LeashCommand
import com.dublikunt.rp.command.LockerCommand
import com.dublikunt.rp.command.ReloadCommand
import com.dublikunt.rp.command.TryCommand
import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.leash.LeashService
import com.dublikunt.rp.locker.InventoryLockService
import com.dublikunt.rp.message.MessageService
import com.dublikunt.rp.update.UpdateChecker
import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class DMRP : JavaPlugin() {
    lateinit var configuration: PluginConfig
        private set

    lateinit var messages: MessageService
        private set

    lateinit var leashes: LeashService
        private set

    lateinit var locks: InventoryLockService
        private set

    private lateinit var updateChecker: UpdateChecker

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        configuration = PluginConfig(this)
        configuration.load()

        messages = MessageService(this, configuration)
        leashes = LeashService(this, configuration, messages)
        locks = InventoryLockService(this, configuration, messages)
        updateChecker = UpdateChecker(this, configuration, messages)

        Metrics(this, BSTATS_PLUGIN_ID)
        PacketEvents.getAPI().init()

        leashes.start()
        locks.start()
        registerCommands()
        registerPlaceholders()
        updateChecker.checkAsync()
    }

    fun reloadConfiguration() {
        configuration.load()
        leashes.reloadSchedule()
    }

    @Suppress("UnstableApiUsage")
    private fun registerCommands() {
        registerCommand("rp-reload", ReloadCommand(::reloadConfiguration, messages))
        registerCommand("coinflip", CoinflipCommand(messages))
        registerCommand("try", TryCommand(configuration, messages))
        registerCommand("dice", DiceCommand(configuration, messages))
        registerCommand("leash", LeashCommand(configuration, messages, leashes))
        registerCommand("lockinv", LockerCommand(configuration, messages, locks))
    }

    private fun registerPlaceholders() {
        if (!configuration.usePlaceholders) {
            return
        }
        DMPlaceholders(this, messages, leashes, locks).register()
    }

    override fun onDisable() {
        if (::updateChecker.isInitialized) {
            updateChecker.close()
        }
        if (::leashes.isInitialized) {
            leashes.shutdown()
        }
        if (::locks.isInitialized) {
            locks.shutdown()
        }
        if (PacketEvents.getAPI().isInitialized) {
            PacketEvents.getAPI().terminate()
        }
    }

    private companion object {
        const val BSTATS_PLUGIN_ID = 21382
    }
}
