package com.dublikunt.rp.locker

import com.dublikunt.rp.DMRP
import com.dublikunt.rp.config.PluginConfig
import com.dublikunt.rp.message.MessageService
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

class InventoryLockService(
    private val plugin: DMRP,
    private val config: PluginConfig,
    private val messages: MessageService,
) {
    private val sessionsByLocked = HashMap<UUID, InventoryLockSession>()
    private val sessionsByOwner = HashMap<UUID, MutableSet<UUID>>()

    fun isLocked(player: OfflinePlayer): Boolean {
        return sessionsByLocked.containsKey(player.uniqueId)
    }

    fun toggle(actor: Player, target: Player) {
        val existing = sessionsByLocked[target.uniqueId]
        if (existing != null) {
            if (existing.owner.uniqueId != actor.uniqueId && !actor.hasPermission("dmrp.lock.admin")) {
                messages.send(actor, messages.raw("message.inventory_lock.not_owner"))
                return
            }

            removeSession(existing)
            messages.send(actor, messages.format("message.inventory_lock.unlock_owner", target.name))
            messages.send(target, messages.format("message.inventory_lock.unlock_target", actor.name))
            return
        }

        if (!target.hasPermission("dmrp.lock.can")) {
            messages.send(actor, messages.raw("message.inventory_lock.cannot_lock"))
            return
        }

        addSession(actor, target)
        messages.send(actor, messages.format("message.inventory_lock.lock_owner", target.name))
        messages.send(target, messages.format("message.inventory_lock.lock_target", actor.name))
    }

    fun removeSessionsInvolving(player: Player) {
        val id = player.uniqueId
        sessionsByOwner[id]?.toList()?.forEach { targetId ->
            sessionsByLocked[targetId]?.let(::removeSession)
        }
        sessionsByLocked[id]?.let(::removeSession)
    }

    fun start() {
        plugin.server.pluginManager.registerEvents(InventoryLockListener(this), plugin)
        plugin.server.pluginManager.registerEvents(InventoryLockPlayerListener(config, this), plugin)
    }

    fun shutdown() {
        sessionsByLocked.clear()
        sessionsByOwner.clear()
    }

    private fun addSession(owner: Player, locked: Player) {
        val session = InventoryLockSession(owner, locked)
        sessionsByLocked[locked.uniqueId] = session
        sessionsByOwner.getOrPut(owner.uniqueId) { HashSet() }.add(locked.uniqueId)
    }

    private fun removeSession(session: InventoryLockSession) {
        val lockedId = session.locked.uniqueId
        if (sessionsByLocked[lockedId] === session) {
            sessionsByLocked.remove(lockedId)
        }

        sessionsByOwner[session.owner.uniqueId]?.let { owned ->
            owned.remove(lockedId)
            if (owned.isEmpty()) {
                sessionsByOwner.remove(session.owner.uniqueId)
            }
        }
    }
}
