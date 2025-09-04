package com.dublikunt.rp.leash

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.util.say
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerLeashListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        removeLeashSession(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        removeLeashSession(event.entity)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        removeLeashSession(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            if (hasLeashSession(event.damager as Player)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerLeash(event: PlayerInteractEntityEvent) {
        if (event.player.inventory.getItem(event.hand)?.type == Material.LEAD) {
            if (event.player.hasPermission("dmrp.leash.use")) {
                if (event.rightClicked is Player) {
                    val leashedPlayer = event.rightClicked as Player
                    val session = getLeashSession(leashedPlayer)
                    if (session != null) {
                        if (session.owner == event.player || event.player.hasPermission("dmrp.leash.admin")) {
                            removeLeashSession(leashedPlayer)
                            say(
                                event.player,
                                String.format(
                                    languageConfiguration.getString("message.leash.unleash_owner")!!,
                                    leashedPlayer.name
                                )
                            )
                            say(
                                leashedPlayer,
                                String.format(
                                    languageConfiguration.getString("message.leash.unleash_target")!!,
                                    event.player.name
                                )
                            )
                        } else {
                            say(
                                event.player,
                                String.format(
                                    languageConfiguration.getString("message.leash.not_owner")!!
                                )
                            )
                        }
                    } else {
                        if (!leashedPlayer.hasPermission("dmrp.leash.can")) {
                            say(event.player, languageConfiguration.getString("message.leash.cannot_leash")!!)
                            return
                        }
                        addLeashSession(event.player, leashedPlayer)
                        say(
                            event.player,
                            String.format(
                                languageConfiguration.getString("message.leash.leash_owner")!!,
                                leashedPlayer.name
                            )
                        )
                        say(
                            leashedPlayer,
                            String.format(
                                languageConfiguration.getString("message.leash.leash_target")!!,
                                event.player.name
                            )
                        )
                    }
                }
            }
        }
    }
}
