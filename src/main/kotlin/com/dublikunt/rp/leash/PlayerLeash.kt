package com.dublikunt.rp.leash

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.util.UtilEventListener
import com.dublikunt.rp.util.pushEntity
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

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

fun hasSession(player: OfflinePlayer): Boolean {
    for (session in sessions) {
        if (session.owner === player || session.leashed === player) {
            return true
        }
    }
    return false
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
