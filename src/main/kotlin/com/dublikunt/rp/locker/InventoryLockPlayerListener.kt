package com.dublikunt.rp.locker

import com.dublikunt.rp.config.PluginConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class InventoryLockPlayerListener(
    private val config: PluginConfig,
    private val locks: InventoryLockService,
) : Listener {
    @EventHandler
    fun onPlayerLocker(event: PlayerInteractEntityEvent) {
        val target = event.rightClicked as? Player ?: return
        val item = event.player.inventory.getItem(event.hand)
        if (item.type != config.settings.lockItem) {
            return
        }
        if (!event.player.hasPermission("dmrp.lock.use")) {
            return
        }
        locks.toggle(event.player, target)
    }
}
