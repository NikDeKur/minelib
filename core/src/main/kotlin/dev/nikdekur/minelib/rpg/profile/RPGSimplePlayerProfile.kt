package dev.nikdekur.minelib.rpg.profile

import org.bukkit.entity.Player

open class RPGSimplePlayerProfile(
    override val player: Player
) : RPGSimpleOfflinePlayerProfile(player), RPGPlayerProfile