package com.dublikunt.rp

import com.dublikunt.rp.leash.LeashService
import com.dublikunt.rp.locker.InventoryLockService
import com.dublikunt.rp.message.MessageService
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import java.util.Locale

class DMPlaceholders(
    private val plugin: DMRP,
    private val messages: MessageService,
    private val leashes: LeashService,
    private val locks: InventoryLockService,
) : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "dmrp"
    }

    @Suppress("UnstableApiUsage")
    override fun getAuthor(): String {
        return plugin.pluginMeta.authors.joinToString()
    }

    @Suppress("UnstableApiUsage")
    override fun getVersion(): String {
        return plugin.pluginMeta.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null) {
            return null
        }

        return when (params.lowercase(Locale.ROOT)) {
            "leashed" -> messages.raw(
                if (leashes.isLeashed(player)) {
                    "placeholder.leash.leashed"
                } else {
                    "placeholder.leash.not_leashed"
                }
            )

            "lockinv" -> messages.raw(
                if (locks.isLocked(player)) {
                    "placeholder.lockinv.locked"
                } else {
                    "placeholder.lockinv.unlocked"
                }
            )

            else -> null
        }
    }
}
