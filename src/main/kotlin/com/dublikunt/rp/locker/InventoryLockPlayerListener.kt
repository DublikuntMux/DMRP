package com.dublikunt.rp.locker

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.util.say
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class InventoryLockPlayerListener : Listener {
    @EventHandler
    fun onPlayerLocker(event: PlayerInteractEntityEvent) {
        if (event.player.inventory.getItem(event.hand)?.type == Material.CHAIN) {
            if (event.player.hasPermission("dmrp.lock.use")) {
                if (event.rightClicked is Player) {
                    val lockedPlayer = event.rightClicked as Player
                    val session = getLockSession(lockedPlayer)
                    if (session != null) {
                        if (session.owner == event.player || event.player.hasPermission("dmrp.lock.admin")) {
                            removeLockSession(lockedPlayer)
                            say(
                                event.player,
                                String.format(
                                    languageConfiguration.getString("message.inventory_lock.unlock_owner")!!,
                                    lockedPlayer.name
                                )
                            )
                            say(
                                lockedPlayer,
                                String.format(
                                    languageConfiguration.getString("message.inventory_lock.unlock_target")!!,
                                    event.player.name
                                )
                            )
                        } else {
                            say(
                                event.player,
                                String.format(
                                    languageConfiguration.getString("message.inventory_lock.not_owner")!!
                                )
                            )
                        }
                    } else {
                        if (!lockedPlayer.hasPermission("dmrp.lock.can")) {
                            say(event.player, languageConfiguration.getString("message.inventory_lock.cannot_lock")!!)
                            return
                        }
                        addLockSession(event.player, lockedPlayer)
                        say(
                            event.player,
                            String.format(
                                languageConfiguration.getString("message.inventory_lock.lock_owner")!!,
                                lockedPlayer.name
                            )
                        )
                        say(
                            lockedPlayer,
                            String.format(
                                languageConfiguration.getString("message.inventory_lock.lock_target")!!,
                                event.player.name
                            )
                        )
                    }
                }
            }
        }
    }
}
