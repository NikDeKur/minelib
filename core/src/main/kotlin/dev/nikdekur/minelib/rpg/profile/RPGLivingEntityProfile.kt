package dev.nikdekur.minelib.rpg.profile

import org.bukkit.entity.LivingEntity

interface RPGLivingEntityProfile : RPGEntityProfile {
    override val entity: LivingEntity
}