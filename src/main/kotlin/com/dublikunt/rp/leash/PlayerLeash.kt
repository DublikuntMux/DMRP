package com.dublikunt.rp.leash

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.util.UtilEventListener
import com.dublikunt.rp.util.pushEntity
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Slime

val sessions: MutableList<LeashSession> = emptyList<LeashSession>().toMutableList()
val collisionTeam = CollisionTeam()

fun getSession(player: OfflinePlayer): LeashSession? {
    for (session in sessions) {
        if (session.owner === player || session.leashed === player) {
            return session
        }
    }
    return null
}

fun hasLeashSession(player: OfflinePlayer): Boolean {
    for (session in sessions) {
        if (session.owner === player || session.leashed === player) {
            return true
        }
    }
    return false
}

fun leashPlayer(owner: Player, leashed: Player) {
    val slime = leashed.world.spawnEntity(
        leashed.location.add(0.0, 1.0, 0.0),
        EntityType.SLIME
    ) as Slime
    slime.size = 0
    slime.setAI(false)
    slime.setGravity(false)
    slime.setLeashHolder(owner)
    slime.isInvulnerable = true
    slime.isSilent = true
    slime.isInvisible = true
    slime.canPickupItems = false

    collisionTeam.team.addEntry(slime.uniqueId.toString())
    leashed.scoreboard = collisionTeam.board

    val session = LeashSession(owner, leashed, slime)
    sessions.add(session)
}

fun unLeashPlayer(player: Player) {
    if (hasLeashSession(player)) {
        val session: LeashSession = getSession(player)!!
        session.slime.setLeashHolder(null)
        collisionTeam.team.removeEntry(session.slime.uniqueId.toString())
        session.slime.remove()
        sessions.remove(session)
    }
}

fun enableLeash() {
    Bukkit.getServer().pluginManager.registerEvents(PlayerLeashListener(), DMRP.getInstance())
    Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(DMRP.getInstance(), {
        for (session in sessions) {
            val distance = session.owner.location.distanceSquared(session.leashed.location)
            if (distance > settings.maxLeashDistance) {
                pushEntity(session.leashed, session.owner.location)
            }
        }
    }, 20, 20)
    Bukkit.getServer().pluginManager.registerEvents(UtilEventListener(), DMRP.getInstance())
}
