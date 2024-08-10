package dev.nikdekur.minelib.rpg.profile

import org.bukkit.entity.Entity

interface RPGEntityProfile : RPGProfile {
    val entity: Entity
}