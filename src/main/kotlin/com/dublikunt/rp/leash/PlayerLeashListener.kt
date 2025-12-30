package com.dublikunt.rp.leash

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.util.ChatUtils
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
        PlayerLeash.removeSession(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        PlayerLeash.removeSession(event.entity)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        PlayerLeash.removeSession(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            if (PlayerLeash.hasSession(event.damager as Player)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerLeash(event: PlayerInteractEntityEvent) {
        val configMaterialName = RPConfig.settings.leashItem
        val leashMaterial: Material
        try {
            leashMaterial = Material.matchMaterial(configMaterialName) ?: Material.LEAD
        } catch (e: NoSuchFieldError) {
            ChatUtils.say("Invalid leash item material in config: $configMaterialName. Check is you write item name correctly and for your server version.")
            return
        }

        val item = event.player.inventory.getItem(event.hand)
        if (item.type == Material.AIR) return

        if (item.type == leashMaterial) {
            if (event.player.hasPermission("dmrp.leash.use")) {
                if (event.rightClicked is Player) {
                    val leashedPlayer = event.rightClicked as Player
                    val session = PlayerLeash.getSession(leashedPlayer)
                    if (session != null) {
                        if (session.owner == event.player || event.player.hasPermission("dmrp.leash.admin")) {
                            PlayerLeash.removeSession(leashedPlayer)
                            ChatUtils.say(
                                event.player,
                                String.format(
                                    RPConfig.languageConfiguration.getString("message.leash.unleash_owner")!!,
                                    leashedPlayer.name
                                )
                            )
                            ChatUtils.say(
                                leashedPlayer,
                                String.format(
                                    RPConfig.languageConfiguration.getString("message.leash.unleash_target")!!,
                                    event.player.name
                                )
                            )
                        } else {
                            ChatUtils.say(
                                event.player,
                                String.format(
                                    RPConfig.languageConfiguration.getString("message.leash.not_owner")!!
                                )
                            )
                        }
                    } else {
                        if (!leashedPlayer.hasPermission("dmrp.leash.can")) {
                            ChatUtils.say(
                                event.player,
                                RPConfig.languageConfiguration.getString("message.leash.cannot_leash")!!
                            )
                            return
                        }
                        PlayerLeash.addSession(event.player, leashedPlayer)
                        ChatUtils.say(
                            event.player,
                            String.format(
                                RPConfig.languageConfiguration.getString("message.leash.leash_owner")!!,
                                leashedPlayer.name
                            )
                        )
                        ChatUtils.say(
                            leashedPlayer,
                            String.format(
                                RPConfig.languageConfiguration.getString("message.leash.leash_target")!!,
                                event.player.name
                            )
                        )
                    }
                }
            }
        }
    }
}
