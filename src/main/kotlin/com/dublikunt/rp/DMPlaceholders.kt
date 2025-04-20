package com.dublikunt.rp

import com.dublikunt.rp.config.languageConfiguration
import com.dublikunt.rp.leash.hasSession
import com.dublikunt.rp.locker.lockedPlayers
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer


class DMPlaceholders : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "dmrp"
    }

    override fun getAuthor(): String {
        return DMRP.getInstance().description.authors.joinToString()
    }

    override fun getVersion(): String {
        return DMRP.getInstance().description.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null) {
            return null
        }

        if (params.equals("leashed", ignoreCase = true)) {
            return if (hasSession(player)) {
                languageConfiguration.getString("placeholder.leash.leashed")!!
            } else {
                languageConfiguration.getString("placeholder.leash.not_leashed")!!
            }
        }

        if (params.equals("lockinv", ignoreCase = true)) {
            return if (lockedPlayers.contains(player.name)) {
                languageConfiguration.getString("placeholder.lockinv.locked")!!
            } else {
                languageConfiguration.getString("placeholder.lockinv.unlocked")!!
            }
        }

        return null
    }
}