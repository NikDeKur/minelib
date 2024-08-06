@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * Field representing entity's walking speed.
 *
 * The field works well for [LivingEntity] and [Player].
 *
 * When using with [Player], [Player.walkSpeed] will be used.
 * When using with [LivingEntity], [Attribute.GENERIC_MOVEMENT_SPEED] will be used.
 */
inline var LivingEntity.walkingSpeed: Float
    get() {
        return if (this is Player)
            walkSpeed
        else
            getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue.toFloat()
    }
    set(value) {
        if (this is Player)
            setHighWalkSpeed(value)
        else
            getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = value.toDouble()
    }