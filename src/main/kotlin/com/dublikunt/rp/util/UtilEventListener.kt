package com.dublikunt.rp.util

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class UtilEventListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            if (EntityUtils.noFallEntities.containsKey(event.entity.entityId)) {
                event.isCancelled = true
            }
        }
    }
}