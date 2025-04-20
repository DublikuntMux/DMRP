package com.dublikunt.rp.locker

import com.dublikunt.rp.DMRP
import org.bukkit.Bukkit

val lockedPlayers = mutableSetOf<String>()

fun enableInventoryLock() {
    Bukkit.getServer().pluginManager.registerEvents(InventoryLockListener(), DMRP.getInstance())
}