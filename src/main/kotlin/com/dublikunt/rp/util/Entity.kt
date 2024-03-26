package com.dublikunt.rp.util

import com.dublikunt.rp.DMRP
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

var noFallEntities: HashMap<Int, Int> = hashMapOf()
    private set

fun pushEntity(entity: Entity, location: Location, multiplier: Double = 1.0, damageEnabled: Boolean = true) {
    val boost = entity.velocity
    boost.x = 0.3
    entity.velocity = boost

    Bukkit.getScheduler().scheduleSyncDelayedTask(DMRP.getInstance(), {
        val gravity = -0.08
        val distance = location.distance(entity.location)

        val velocity = Vector(
            (1.0 + 0.07 * distance) * (location.x - entity.location.x) / distance,
            (1.0 + 0.03 * distance) * (location.y - entity.location.y) / distance - 0.5 * gravity * distance,
            (1.0 + 0.07 * distance) * (location.z - entity.location.z) / distance
        )

        velocity.multiply(multiplier)
        entity.velocity = velocity
    }, 1)

    if (!damageEnabled) {
        addNoFallDamage(entity, 100)
    }
}

fun addNoFallDamage(entity: Entity, ticks: Long) {
    if (noFallEntities.containsKey(entity.entityId)) {
        noFallEntities[entity.entityId]?.let { Bukkit.getServer().scheduler.cancelTask(it) }
    }

    val taskId = Bukkit.getServer().scheduler.scheduleSyncDelayedTask(DMRP.getInstance(), {
        if (noFallEntities.containsKey(entity.entityId)) {
            noFallEntities.remove(entity.entityId)
        }
    }, ticks)

    noFallEntities[entity.entityId] = taskId
}