package com.dublikunt.rp.leash

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Slime
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
        unLeashPlayer(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        unLeashPlayer(event.entity)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        unLeashPlayer(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            if (hasSession(event.damager as Player)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (hasSession(event.player)) {
            val session = getSession(event.player)!!
            session.slime.teleport(session.leashed.location.add(0.0, 1.0, 0.0))
        }
    }

    @EventHandler
    fun onPlayerLeash(event: PlayerInteractEntityEvent) {
        if (event.player.inventory.getItem(event.hand)?.type == Material.LEAD) {
            if (event.player.hasPermission("dmrp.leash.use")) {
                if (event.rightClicked is Player) {
                    val leashedPlayer = event.rightClicked as Player
                    if (hasSession(leashedPlayer)) {
                        unLeashPlayer(leashedPlayer)
                    } else if (leashedPlayer.hasPermission("dmrp.leash.can")) {
                        val slime = leashedPlayer.world.spawnEntity(
                            leashedPlayer.location.add(0.0, 1.0, 0.0),
                            EntityType.SLIME
                        ) as Slime
                        slime.size = 0
                        slime.setAI(false)
                        slime.setGravity(false)
                        slime.setLeashHolder(event.player)
                        slime.isInvulnerable = true
                        slime.isInvisible = true
                        slime.canPickupItems = false

                        collisionTeam.team.addEntry(slime.uniqueId.toString())
                        leashedPlayer.scoreboard = collisionTeam.board

                        val session = LeashSession(event.player, leashedPlayer, slime)
                        sessions.add(session)
                    }
                }
            }
        }
    }

    private fun unLeashPlayer(player: Player) {
        if (hasSession(player)) {
            val session: LeashSession = getSession(player)!!
            session.slime.setLeashHolder(null)
            collisionTeam.team.removeEntry(session.slime.uniqueId.toString())
            session.slime.remove()
            sessions.remove(session)
        }
    }
}