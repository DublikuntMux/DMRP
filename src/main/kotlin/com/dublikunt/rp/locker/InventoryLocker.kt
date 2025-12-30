package com.dublikunt.rp.locker

import com.dublikunt.rp.DMRP
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object InventoryLocker {
    val lockSessions: MutableList<InventoryLockSession> = mutableListOf()

    fun getSession(player: OfflinePlayer): InventoryLockSession? {
        for (session in lockSessions) {
            if (session.owner === player || session.locked === player) {
                return session
            }
        }
        return null
    }

    fun hasSession(player: OfflinePlayer): Boolean {
        for (session in lockSessions) {
            if (session.owner === player || session.locked === player) {
                return true
            }
        }
        return false
    }

    fun addSession(owner: Player, locked: Player) {
        val session = InventoryLockSession(owner, locked)
        lockSessions.add(session)
    }

    fun removeSession(player: Player) {
        if (hasSession(player)) {
            val session: InventoryLockSession = getSession(player)!!
            lockSessions.remove(session)
        }
    }

    fun enable() {
        Bukkit.getServer().pluginManager.registerEvents(InventoryLockListener(), DMRP.getInstance())
        Bukkit.getServer().pluginManager.registerEvents(InventoryLockPlayerListener(), DMRP.getInstance())
    }
}