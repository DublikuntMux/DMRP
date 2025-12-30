package com.dublikunt.rp.leash

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.util.EntityUtils
import com.dublikunt.rp.util.UtilEventListener
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.EntityPositionData
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.*
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import kotlin.random.Random

object PlayerLeash {
    val leashSessions: MutableList<LeashSession> = mutableListOf()
    val slimeOffset: org.bukkit.util.Vector = org.bukkit.util.Vector(0.0, 0.7, 0.0)
    private var syncTask: Int = -1

    private fun generateEntityId(): Int {
        return -Random.nextInt(Int.MAX_VALUE)
    }

    fun getSession(player: OfflinePlayer): LeashSession? {
        for (session in leashSessions) {
            if (session.owner === player || session.leashed === player) {
                return session
            }
        }
        return null
    }

    fun hasSession(player: OfflinePlayer): Boolean {
        for (session in leashSessions) {
            if (session.owner === player || session.leashed === player) {
                return true
            }
        }
        return false
    }

    fun addSession(owner: Player, leashed: Player) {
        val slimeId = generateEntityId()
        val slimeUuid = UUID.randomUUID()

        val entityData = listOf(
            EntityData(0, EntityDataTypes.BYTE, 0x20.toByte()), // Invisible
            EntityData(4, EntityDataTypes.BOOLEAN, true), // Is silent
            EntityData(5, EntityDataTypes.BOOLEAN, true), // Has no gravity
            EntityData(15, EntityDataTypes.BYTE, 1.toByte()), // noAI
            EntityData(16, EntityDataTypes.INT, 1) // Small
        )
        val leashPacket = WrapperPlayServerAttachEntity(slimeId, owner.entityId, true)
        val dataPacket = WrapperPlayServerEntityMetadata(slimeId, entityData)

        val session = LeashSession(owner, leashed, slimeId, slimeUuid, leashPacket, dataPacket)
        leashSessions.add(session)
    }

    fun removeSession(player: Player) {
        if (hasSession(player)) {
            val session: LeashSession = getSession(player)!!

            val packet = WrapperPlayServerDestroyEntities(session.slimeId)
            for (viewerId in session.viewers) {
                val viewer = Bukkit.getPlayer(viewerId)
                viewer?.let { PacketEvents.getAPI().playerManager.sendPacket(it, packet) }
            }

            val teamPacket = createTeam(session)
            teamPacket.teamMode = WrapperPlayServerTeams.TeamMode.REMOVE
            PacketEvents.getAPI().playerManager.sendPacket(session.leashed, teamPacket)

            leashSessions.remove(session)
        }
    }

    fun createTeam(session: LeashSession): WrapperPlayServerTeams {
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

    fun reload() {
        if (syncTask != -1) {
            Bukkit.getServer().scheduler.cancelTask(syncTask)
        }
        syncTask = Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(DMRP.getInstance(), {
            for (session in leashSessions) {
                val leashedPlayer = session.leashed
                val viewDistance = leashedPlayer.world.viewDistance * 16
                val viewDistanceSq = (viewDistance * viewDistance).toDouble()

                val currentViewers = mutableSetOf<UUID>()

                for (viewer in leashedPlayer.world.players) {
                    if (viewer.location.distanceSquared(leashedPlayer.location) <= viewDistanceSq) {
                        currentViewers.add(viewer.uniqueId)

                        if (!session.viewers.contains(viewer.uniqueId)) {
                            val spawnLocation =
                                SpigotConversionUtil.fromBukkitLocation(leashedPlayer.location.add(slimeOffset))
                            val spawnPacket = WrapperPlayServerSpawnEntity(
                                session.slimeId, session.slimeUUID, EntityTypes.SLIME,
                                spawnLocation, 0.0f, 0, null
                            )
                            PacketEvents.getAPI().playerManager.sendPacket(viewer, spawnPacket)
                            PacketEvents.getAPI().playerManager.sendPacket(viewer, session.leashPacket)
                            PacketEvents.getAPI().playerManager.sendPacket(viewer, session.dataPacket)

                            if (viewer == leashedPlayer) {
                                val teamPacket = createTeam(session)
                                teamPacket.teamMode = WrapperPlayServerTeams.TeamMode.CREATE
                                PacketEvents.getAPI().playerManager.sendPacket(viewer, teamPacket)
                            }
                        } else {
                            val newLocation =
                                SpigotConversionUtil.fromBukkitLocation(leashedPlayer.location.add(slimeOffset))
                            val packet = WrapperPlayServerEntityPositionSync(
                                session.slimeId,
                                EntityPositionData(
                                    newLocation.position,
                                    Vector3d.zero(),
                                    0.0f,
                                    0.0f
                                ), false
                            )
                            PacketEvents.getAPI().playerManager.sendPacket(viewer, packet)
                        }
                    }
                }

                val removedViewers = session.viewers.filter { !currentViewers.contains(it) }
                if (removedViewers.isNotEmpty()) {
                    val destroyPacket = WrapperPlayServerDestroyEntities(session.slimeId)
                    for (removedViewerId in removedViewers) {
                        val removedViewer = Bukkit.getPlayer(removedViewerId)
                        removedViewer?.let { PacketEvents.getAPI().playerManager.sendPacket(it, destroyPacket) }
                    }
                }

                session.viewers.clear()
                session.viewers.addAll(currentViewers)
            }
        }, RPConfig.settings.leashSyncRate.toLong(), RPConfig.settings.leashSyncRate.toLong())
    }

    fun enable() {
        Bukkit.getServer().pluginManager.registerEvents(PlayerLeashListener(), DMRP.getInstance())
        Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(DMRP.getInstance(), {
            for (session in leashSessions) {
                val distance = session.owner.location.distanceSquared(session.leashed.location)
                if (distance > RPConfig.settings.maxLeashDistance) {
                    EntityUtils.pushEntity(session.leashed, session.owner.location)
                }
            }
        }, 20, 20)
        reload()
        Bukkit.getServer().pluginManager.registerEvents(UtilEventListener(), DMRP.getInstance())
    }
}