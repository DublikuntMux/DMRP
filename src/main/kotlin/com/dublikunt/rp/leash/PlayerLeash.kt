package com.dublikunt.rp.leash

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.settings
import com.dublikunt.rp.util.UtilEventListener
import com.dublikunt.rp.util.pushEntity
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.wrapper.play.server.*
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import kotlin.random.Random

val leashSessions: MutableList<LeashSession> = mutableListOf()
val slimeOffset: org.bukkit.util.Vector = org.bukkit.util.Vector(0.0, 0.7, 0.0)

private fun generateEntityId(): Int {
    return -Random.nextInt(Int.MAX_VALUE)
}

fun getLeashSession(player: OfflinePlayer): LeashSession? {
    for (session in leashSessions) {
        if (session.owner === player || session.leashed === player) {
            return session
        }
    }
    return null
}

fun hasLeashSession(player: OfflinePlayer): Boolean {
    for (session in leashSessions) {
        if (session.owner === player || session.leashed === player) {
            return true
        }
    }
    return false
}

fun addLeashSession(owner: Player, leashed: Player) {
    val slimeId = generateEntityId()
    val slimeUuid = UUID.randomUUID()
    val session = LeashSession(owner, leashed, slimeId, slimeUuid)
    leashSessions.add(session)

    val entityData = listOf(
        EntityData(0, EntityDataTypes.BYTE, 0x20.toByte()), // Invisible
        EntityData(4, EntityDataTypes.BOOLEAN, true), // Is silent
        EntityData(5, EntityDataTypes.BOOLEAN, true), // Has no gravity
        EntityData(15, EntityDataTypes.BYTE, 1.toByte()), // noAI
        EntityData(16, EntityDataTypes.INT, 1) // Small
    )
    val spawnLocation = SpigotConversionUtil.fromBukkitLocation(leashed.location.add(slimeOffset))
    val spawnPacket = WrapperPlayServerSpawnEntity(
        slimeId, slimeUuid, EntityTypes.SLIME,
        spawnLocation, 0.0f, 0, null
    )
    val leashPacket = WrapperPlayServerAttachEntity(slimeId, owner.entityId, true)
    val dataPacket = WrapperPlayServerEntityMetadata(slimeId, entityData)

    for (viewer in Bukkit.getOnlinePlayers()) {
        PacketEvents.getAPI().playerManager.sendPacket(viewer, spawnPacket)
        PacketEvents.getAPI().playerManager.sendPacket(viewer, leashPacket)
        PacketEvents.getAPI().playerManager.sendPacket(viewer, dataPacket)
    }

    val teamPacket = createTeamPacket(session)
    teamPacket.teamMode = WrapperPlayServerTeams.TeamMode.CREATE

    PacketEvents.getAPI().playerManager.sendPacket(leashed, teamPacket)
}

fun removeLeashSession(player: Player) {
    if (hasLeashSession(player)) {
        val session: LeashSession = getLeashSession(player)!!

        val packet = WrapperPlayServerDestroyEntities(session.slimeId)
        for (viewer in Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().playerManager.sendPacket(viewer, packet)
        }

        val teamPacket = createTeamPacket(session)
        teamPacket.teamMode = WrapperPlayServerTeams.TeamMode.REMOVE
        PacketEvents.getAPI().playerManager.sendPacket(session.leashed, teamPacket)

        leashSessions.remove(session)
    }
}

fun createTeamPacket(session: LeashSession): WrapperPlayServerTeams {
    val teamName = "nocollide_" + session.leashed.name
    val teamMembers = listOf(session.leashed.name, session.slimeUUID.toString())
    val teamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
        Component.empty(),
        null,
        null,
        WrapperPlayServerTeams.NameTagVisibility.NEVER,
        WrapperPlayServerTeams.CollisionRule.NEVER,
        null,
        WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE
    )

    val teamPacket = WrapperPlayServerTeams(teamName, WrapperPlayServerTeams.TeamMode.CREATE, teamInfo, teamMembers)
    return teamPacket
}

fun enableLeash() {
    Bukkit.getServer().pluginManager.registerEvents(PlayerLeashListener(), DMRP.getInstance())
    Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(DMRP.getInstance(), {
        for (session in leashSessions) {
            val distance = session.owner.location.distanceSquared(session.leashed.location)
            if (distance > settings.maxLeashDistance) {
                pushEntity(session.leashed, session.owner.location)
            }
        }
    }, 20, 20)
    Bukkit.getServer().pluginManager.registerEvents(UtilEventListener(), DMRP.getInstance())
}
