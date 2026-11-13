package com.dublikunt.rp.leash

import com.dublikunt.rp.config.PluginConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

class LeashListener(
    private val config: PluginConfig,
    private val leashes: LeashService,
) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        leashes.removeSessionsInvolving(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        leashes.removeSessionsInvolving(event.entity)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        leashes.removeSessionsInvolving(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        if (leashes.isParticipating(damager)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerLeash(event: PlayerInteractEntityEvent) {
        val target = event.rightClicked as? Player ?: return
        val item = event.player.inventory.getItem(event.hand)
        if (item.type != config.settings.leashItem) {
            return
        }
        if (!event.player.hasPermission("dmrp.leash.use")) {
            return
        }
        leashes.toggle(event.player, target)
    }
}
