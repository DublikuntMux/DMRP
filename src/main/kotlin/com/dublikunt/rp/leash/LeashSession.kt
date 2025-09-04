package com.dublikunt.rp.leash

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import org.bukkit.entity.Player
import java.util.*

class LeashSession(
    val owner: Player,
    val leashed: Player,
    val slimeId: Int,
    val slimeUUID: UUID,
    val leashPacket: WrapperPlayServerAttachEntity,
    val dataPacket: WrapperPlayServerEntityMetadata,
    val viewers: MutableSet<UUID> = mutableSetOf()
)
