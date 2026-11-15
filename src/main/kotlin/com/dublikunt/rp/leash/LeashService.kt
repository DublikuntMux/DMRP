package com.dublikunt.rp.leash

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.entity.EntityPositionData
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.world.Location as PacketLocation
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityPositionSync
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

class LeashService(
    private val plugin: DMRP,
    private val config: PluginConfig,
    private val messages: MessageService,
) {
    private val sessions = HashMap<UUID, LeashSession>()
    private val sessionsByOwner = HashMap<UUID, MutableSet<UUID>>()
    private var syncTask: BukkitTask? = null
    private var followTask: BukkitTask? = null
    private val serverVersion: ServerVersion = PacketEvents.getAPI().serverManager.version

    fun isLeashed(player: OfflinePlayer): Boolean {
        return sessions.containsKey(player.uniqueId)
    }

    fun isParticipating(player: OfflinePlayer): Boolean {
        val id = player.uniqueId
        return sessions.containsKey(id) || sessionsByOwner.containsKey(id)
    }

    fun toggle(actor: Player, target: Player) {
        if (actor.uniqueId == target.uniqueId) {
            messages.send(actor, messages.raw("message.leash.self"))
            return
        }

        val existing = sessions[target.uniqueId]
        if (existing != null) {
            if (existing.owner.uniqueId != actor.uniqueId && !actor.hasPermission("dmrp.leash.admin")) {
                messages.send(actor, messages.raw("message.leash.not_owner"))
                return
            }

            removeSession(existing)
            messages.send(actor, messages.format("message.leash.unleash_owner", target.name))
            messages.send(target, messages.format("message.leash.unleash_target", actor.name))
            return
        }

        if (!target.hasPermission("dmrp.leash.can")) {
            messages.send(actor, messages.raw("message.leash.cannot_leash"))
            return
        }

        addSession(actor, target)
        messages.send(actor, messages.format("message.leash.leash_owner", target.name))
        messages.send(target, messages.format("message.leash.leash_target", actor.name))
    }

    fun removeSessionsInvolving(player: Player) {
        val id = player.uniqueId
        sessionsByOwner[id]?.toList()?.forEach { targetId ->
            sessions[targetId]?.let(::removeSession)
        }
        sessions[id]?.let(::removeSession)
    }

    fun start() {
        plugin.server.pluginManager.registerEvents(LeashListener(config, this), plugin)
        followTask = plugin.server.scheduler.runTaskTimer(plugin, Runnable(::followLeashed), 20L, 20L)
        restartSyncTask()
    }

    fun reloadSchedule(syncRate: Int) {
        restartSyncTask(syncRate)
    }

    fun shutdown() {
        followTask?.cancel()
        syncTask?.cancel()
        followTask = null
        syncTask = null

        for (session in sessions.values.toList()) {
            removeSession(session)
        }
        sessions.clear()
        sessionsByOwner.clear()
    }

    private fun addSession(owner: Player, leashed: Player) {
        val slimeId = -ThreadLocalRandom.current().nextInt(1, Int.MAX_VALUE)
        val metadata = listOf(
            EntityData(0, EntityDataTypes.BYTE, 0x20.toByte()), // Invisible
            EntityData(4, EntityDataTypes.BOOLEAN, true), // Is silent
            EntityData(5, EntityDataTypes.BOOLEAN, true), // Has no gravity
            EntityData(15, EntityDataTypes.BYTE, 1.toByte()), // No AI
            // Slimes became ageable in 26.2, which shifted the size index from 16 to 18.
            EntityData(
                if (serverVersion.isNewerThanOrEquals(ServerVersion.V_26_2)) 18 else 16,
                EntityDataTypes.INT,
                1,
            ), // Small
        )

        val session = LeashSession(
            owner = owner,
            leashed = leashed,
            slimeId = slimeId,
            slimeUUID = UUID.randomUUID(),
            leashPacket = WrapperPlayServerAttachEntity(slimeId, owner.entityId, true),
            dataPacket = WrapperPlayServerEntityMetadata(slimeId, metadata),
        )

        sessions[leashed.uniqueId] = session
        sessionsByOwner.getOrPut(owner.uniqueId) { HashSet() }.add(leashed.uniqueId)
    }

    private fun removeSession(session: LeashSession) {
        val leashedId = session.leashed.uniqueId
        if (sessions[leashedId] === session) {
            sessions.remove(leashedId)
        }

        sessionsByOwner[session.owner.uniqueId]?.let { owned ->
            owned.remove(leashedId)
            if (owned.isEmpty()) {
                sessionsByOwner.remove(session.owner.uniqueId)
            }
        }

        val playerManager = PacketEvents.getAPI().playerManager
        val destroyPacket = WrapperPlayServerDestroyEntities(session.slimeId)
        for (viewerId in session.viewers) {
            plugin.server.getPlayer(viewerId)?.let { playerManager.sendPacket(it, destroyPacket) }
        }
        session.viewers.clear()

        if (session.leashed.isOnline) {
            val removeTeam = createTeam(session).apply {
                teamMode = WrapperPlayServerTeams.TeamMode.REMOVE
            }
            playerManager.sendPacket(session.leashed, removeTeam)
        }
    }

    private fun restartSyncTask(syncRate: Int = config.settings.leashSyncRate) {
        val rate = syncRate.toLong()
        val replacement = plugin.server.scheduler.runTaskTimer(plugin, Runnable(::syncLeashes), rate, rate)
        syncTask?.cancel()
        syncTask = replacement
    }

    private fun followLeashed() {
        if (sessions.isEmpty()) {
            return
        }

        val maxDistance = config.settings.maxLeashDistance.toDouble()
        val maxDistanceSquared = maxDistance * maxDistance
        for (session in sessions.values) {
            val owner = session.owner
            val leashed = session.leashed
            if (!owner.isOnline || !leashed.isOnline || owner.world !== leashed.world) {
                continue
            }
            if (owner.location.distanceSquared(leashed.location) <= maxDistanceSquared) {
                continue
            }
            pullTowards(leashed, owner.location)
        }
    }

    private fun pullTowards(player: Player, target: Location) {
        val offset = target.toVector().subtract(player.location.toVector())
        val distance = offset.length()
        if (distance < 0.01) {
            return
        }

        val horizontal = 1.0 + 0.07 * distance
        player.velocity = Vector(
            horizontal * offset.x / distance,
            (1.0 + 0.03 * distance) * offset.y / distance + 0.04 * distance,
            horizontal * offset.z / distance,
        )
    }

    private fun syncLeashes() {
        if (sessions.isEmpty()) {
            return
        }

        val playerManager = PacketEvents.getAPI().playerManager
        for (session in sessions.values) {
            val leashed = session.leashed
            if (!leashed.isOnline) {
                continue
            }

            val world = leashed.world
            val viewDistance = (world.viewDistance * 16).toDouble()
            val leashedLocation = leashed.location
            val slimePosition = SpigotConversionUtil.fromBukkitLocation(leashedLocation.clone().add(SLIME_OFFSET))
            val currentViewers = HashSet<UUID>()

            for (viewer in world.getNearbyPlayers(leashedLocation, viewDistance)) {
                currentViewers.add(viewer.uniqueId)

                if (!session.viewers.contains(viewer.uniqueId)) {
                    playerManager.sendPacket(viewer, createSpawnPacket(session, slimePosition))
                    playerManager.sendPacket(viewer, session.leashPacket)
                    playerManager.sendPacket(viewer, session.dataPacket)

                    if (viewer === leashed) {
                        playerManager.sendPacket(viewer, createTeam(session))
                    }
                } else {
                    playerManager.sendPacket(viewer, createPositionPacket(session, slimePosition))
                }
            }

            val removedViewers = session.viewers.filterNot(currentViewers::contains)
            if (removedViewers.isNotEmpty()) {
                val destroyPacket = WrapperPlayServerDestroyEntities(session.slimeId)
                for (viewerId in removedViewers) {
                    plugin.server.getPlayer(viewerId)?.let { playerManager.sendPacket(it, destroyPacket) }
                }
            }

            session.viewers.clear()
            session.viewers.addAll(currentViewers)
        }
    }

    private fun createSpawnPacket(session: LeashSession, position: PacketLocation): PacketWrapper<*> {
        // Living entities had a dedicated spawn packet before 1.19.
        if (serverVersion.isOlderThan(ServerVersion.V_1_19)) {
            return WrapperPlayServerSpawnLivingEntity(
                session.slimeId,
                session.slimeUUID,
                EntityTypes.SLIME,
                position,
                0.0f,
                Vector3d.zero(),
                emptyList(),
            )
        }
        return WrapperPlayServerSpawnEntity(
            session.slimeId,
            session.slimeUUID,
            EntityTypes.SLIME,
            position,
            0.0f,
            0,
            null,
        )
    }

    private fun createPositionPacket(session: LeashSession, position: PacketLocation): PacketWrapper<*> {
        // The position sync packet was added in 1.21.2.
        if (serverVersion.isOlderThan(ServerVersion.V_1_21_2)) {
            return WrapperPlayServerEntityTeleport(session.slimeId, position, false)
        }
        return WrapperPlayServerEntityPositionSync(
            session.slimeId,
            EntityPositionData(position.position, Vector3d.zero(), 0.0f, 0.0f),
            false,
        )
    }

    private fun createTeam(session: LeashSession): WrapperPlayServerTeams {
        val teamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.empty(),
            null,
            null,
            WrapperPlayServerTeams.NameTagVisibility.NEVER,
            WrapperPlayServerTeams.CollisionRule.NEVER,
            null,
            WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE,
        )

        return WrapperPlayServerTeams(
            "nocollide_${session.leashed.name}",
            WrapperPlayServerTeams.TeamMode.CREATE,
            teamInfo,
            listOf(session.leashed.name, session.slimeUUID.toString()),
        )
    }

    private companion object {
        val SLIME_OFFSET: Vector = Vector(0.0, 0.7, 0.0)
    }
}
