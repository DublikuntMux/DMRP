package com.dublikunt.rp.leash

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.util.*
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.random.Random

val sessions: MutableList<LeashSession> = emptyList<LeashSession>().toMutableList()

private fun generateEntityId(): Int {
    return -Random.nextInt(Int.MAX_VALUE)
}

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
    val slimeId = generateEntityId()
    val session = LeashSession(owner, leashed, slimeId)
    sessions.add(session)

    val entityData = listOf(
        EntityData(0, EntityDataTypes.BYTE, 0x20.toByte()), // Invisible
        EntityData(4, EntityDataTypes.BOOLEAN, true), // Is silent
        EntityData(5, EntityDataTypes.BOOLEAN, true), // Has no gravity
        EntityData(15, EntityDataTypes.BYTE, 1.toByte()), // noAI
        EntityData(16, EntityDataTypes.INT, 1) // Small
    )
    val spawnLocation = SpigotConversionUtil.fromBukkitLocation(leashed.location.add(0.0, 1.0, 0.0))
    val spawnPacket = WrapperPlayServerSpawnEntity(slimeId, UUID.randomUUID(), EntityTypes.SLIME,
        spawnLocation, 0.0f, 0, null
    )
    val leashPacket = WrapperPlayServerAttachEntity(slimeId, owner.entityId, true)
    val dataPacket = WrapperPlayServerEntityMetadata(slimeId, entityData)
    for (viewer in Bukkit.getOnlinePlayers()) {
        PacketEvents.getAPI().playerManager.sendPacket(viewer, spawnPacket)
        PacketEvents.getAPI().playerManager.sendPacket(viewer, leashPacket)
        PacketEvents.getAPI().playerManager.sendPacket(viewer, dataPacket)
    }
}

fun unLeashPlayer(player: Player) {
    if (hasLeashSession(player)) {
        val session: LeashSession = getSession(player)!!

        val packet = WrapperPlayServerDestroyEntities(session.slimeId)
        for (viewer in Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().playerManager.sendPacket(viewer, packet)
        }

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
