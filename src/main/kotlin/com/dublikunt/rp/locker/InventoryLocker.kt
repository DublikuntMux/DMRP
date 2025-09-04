package com.dublikunt.rp.locker

import com.dublikunt.rp.DMRP
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

val lockSessions: MutableList<InventoryLockSession> = mutableListOf()

fun getLockSession(player: OfflinePlayer): InventoryLockSession? {
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

fun addLockSession(owner: Player, locked: Player) {
    val session = InventoryLockSession(owner, locked)
    lockSessions.add(session)
}

fun removeLockSession(player: Player) {
    if (hasLockSession(player)) {
        val session: InventoryLockSession = getLockSession(player)!!
        lockSessions.remove(session)
    }
}

fun enableInventoryLocker() {
    Bukkit.getServer().pluginManager.registerEvents(InventoryLockListener(), DMRP.getInstance())
    Bukkit.getServer().pluginManager.registerEvents(InventoryLockPlayerListener(), DMRP.getInstance())
}