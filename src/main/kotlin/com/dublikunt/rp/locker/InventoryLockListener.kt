package com.dublikunt.rp.locker

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerQuitEvent

class InventoryLockListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (player is Player) {
            val session = getSession(player) ?: return
            if (session.locked != player)
                return

            if (event.view.topInventory == event.clickedInventory || event.clickedInventory != null) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked
        if (player is Player) {
            val session = getSession(player) ?: return
            if (session.locked != player)
                return

            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (hasLockSession(event.player)) {
            removeSession(getSession(event.player)!!)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (hasLockSession(event.entity)) {
            removeSession(getSession(event.entity)!!)
        }
    }
}