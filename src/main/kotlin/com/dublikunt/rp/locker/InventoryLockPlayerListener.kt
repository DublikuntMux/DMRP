package com.dublikunt.rp.locker

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.util.ChatUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class InventoryLockPlayerListener : Listener {
    @EventHandler
    fun onPlayerLocker(event: PlayerInteractEntityEvent) {
        if (event.player.inventory.getItem(event.hand).type == Material.CHAIN) {
            if (event.player.hasPermission("dmrp.lock.use")) {
                if (event.rightClicked is Player) {
                    val lockedPlayer = event.rightClicked as Player
                    val session = InventoryLocker.getSession(lockedPlayer)
                    if (session != null) {
                        if (session.owner == event.player || event.player.hasPermission("dmrp.lock.admin")) {
                            InventoryLocker.removeSession(lockedPlayer)
                            ChatUtils.say(
                                event.player,
                                String.format(
                                    RPConfig.languageConfiguration.getString("message.inventory_lock.unlock_owner")!!,
                                    lockedPlayer.name
                                )
                            )
                            ChatUtils.say(
                                lockedPlayer,
                                String.format(
                                    RPConfig.languageConfiguration.getString("message.inventory_lock.unlock_target")!!,
                                    event.player.name
                                )
                            )
                        } else {
                            ChatUtils.say(
                                event.player,
                                String.format(
                                    RPConfig.languageConfiguration.getString("message.inventory_lock.not_owner")!!
                                )
                            )
                        }
                    } else {
                        if (!lockedPlayer.hasPermission("dmrp.lock.can")) {
                            ChatUtils.say(
                                event.player,
                                RPConfig.languageConfiguration.getString("message.inventory_lock.cannot_lock")!!
                            )
                            return
                        }
                        InventoryLocker.addSession(event.player, lockedPlayer)
                        ChatUtils.say(
                            event.player,
                            String.format(
                                RPConfig.languageConfiguration.getString("message.inventory_lock.lock_owner")!!,
                                lockedPlayer.name
                            )
                        )
                        ChatUtils.say(
                            lockedPlayer,
                            String.format(
                                RPConfig.languageConfiguration.getString("message.inventory_lock.lock_target")!!,
                                event.player.name
                            )
                        )
                    }
                }
            }
        }
    }
}
