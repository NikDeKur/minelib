package dev.nikdekur.minelib.rpg.profile

import org.bukkit.entity.Player

interface RPGPlayerProfile : RPGLivingEntityProfile {
    val player: Player

    override val entity
        get() = player
}