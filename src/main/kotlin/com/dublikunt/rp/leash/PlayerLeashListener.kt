package com.dublikunt.rp.leash

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.util.say
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.EntityPositionData
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityPositionSync
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
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

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (hasLeashSession(event.player)) {
            val session = getLeashSession(event.player)!!
            val newLocation = SpigotConversionUtil.fromBukkitLocation(session.leashed.location.add(slimeOffset))
            val packet = WrapperPlayServerEntityPositionSync(
                session.slimeId,
                EntityPositionData(
                    newLocation.position,
                    Vector3d.zero(),
                    0.0f,
                    0.0f
                ), false
            )
            for (viewer in Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().playerManager.sendPacket(viewer, packet)
            }
        }
    }

    @EventHandler
    fun onPlayerLeash(event: PlayerInteractEntityEvent) {
        if (event.player.inventory.getItem(event.hand)?.type == Material.LEAD) {
            if (event.player.hasPermission("dmrp.leash.use")) {
                if (event.rightClicked is Player) {
                    val leashedPlayer = event.rightClicked as Player
                    if (hasLeashSession(leashedPlayer)) {
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
