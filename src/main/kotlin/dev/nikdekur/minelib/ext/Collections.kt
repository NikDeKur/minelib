@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

inline val Iterable<Player>.names: List<String>
    get() = map { it.name }
inline val Iterable<Player>.uuids: List<UUID>
    get() = map { it.uniqueId }


inline fun Collection<Entity>.onlyLiving() = filterIsInstance<LivingEntity>()