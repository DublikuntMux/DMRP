package com.dublikunt.rp.locker

import com.dublikunt.rp.DMRP
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

val lockSessions: MutableList<InventoryLockSession> = emptyList<InventoryLockSession>().toMutableList()

fun getSession(player: OfflinePlayer): InventoryLockSession? {
    for (session in lockSessions) {
        if (session.owner === player || session.locked === player) {
            return session
        }
    }
    return null
}

fun hasLockSession(player: OfflinePlayer): Boolean {
    for (session in lockSessions) {
        if (session.owner === player || session.locked === player) {
            return true
        }
    }
    return false
}

fun addSession(session: InventoryLockSession) {
    lockSessions.add(session)
}

fun removeSession(session: InventoryLockSession) {
    lockSessions.remove(session)
}

fun enableInventoryLocker() {
    Bukkit.getServer().pluginManager.registerEvents(InventoryLockListener(), DMRP.getInstance())
    Bukkit.getServer().pluginManager.registerEvents(InventoryLockPlayerListener(), DMRP.getInstance())
}