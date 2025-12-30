package com.dublikunt.rp

import com.dublikunt.rp.config.RPConfig
import com.dublikunt.rp.leash.PlayerLeash
import com.dublikunt.rp.locker.InventoryLocker
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer


class DMPlaceholders : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "dmrp"
    }

    @Suppress("UnstableApiUsage")
    override fun getAuthor(): String {
        return DMRP.getInstance().pluginMeta.authors.joinToString()
    }

    @Suppress("UnstableApiUsage")
    override fun getVersion(): String {
        return DMRP.getInstance().pluginMeta.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null) {
            return null
        }

        if (params.equals("leashed", ignoreCase = true)) {
            return if (PlayerLeash.hasSession(player)) {
                RPConfig.languageConfiguration.getString("placeholder.leash.leashed")!!
            } else {
                RPConfig.languageConfiguration.getString("placeholder.leash.not_leashed")!!
            }
        }

        if (params.equals("lockinv", ignoreCase = true)) {
            return if (InventoryLocker.hasSession(player)) {
                RPConfig.languageConfiguration.getString("placeholder.lockinv.locked")!!
            } else {
                RPConfig.languageConfiguration.getString("placeholder.lockinv.unlocked")!!
            }
        }

        return null
    }
}