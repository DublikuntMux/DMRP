package com.dublikunt.rp.locker

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.util.say
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
            if (lockedPlayers.contains(player.name)) {
                if (event.view.topInventory == event.clickedInventory || event.clickedInventory != null) {
                    event.isCancelled = true
                    say(player, languageConfiguration.getString("message.inventory_lock.on_move")!!)
                }
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked
        if (player is Player) {
            if (lockedPlayers.contains(player.name)) {
                event.isCancelled = true
                say(player, languageConfiguration.getString("message.inventory_lock.on_move")!!)
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (lockedPlayers.contains(event.player.name))
            lockedPlayers.remove(event.player.name)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (lockedPlayers.contains(event.entity.name))
            lockedPlayers.remove(event.entity.name)
    }
}