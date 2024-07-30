@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.ndkore.ext.constructTyped
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityTypes
import net.minecraft.server.v1_12_R1.MinecraftKey
import net.minecraft.server.v1_12_R1.World
import org.bukkit.entity.EntityType

inline val EntityType.minecraftKey: MinecraftKey
    get() = MinecraftKey(this.name)

inline val EntityType.nmsClass: Class<out Entity>
    get() = EntityTypes.b[this.minecraftKey] ?: throw IllegalArgumentException("Unknown entity type: ${this.name}")

val ENTITY_DEFAULT_CONSTRUCTOR = arrayOf(World::class.java)
inline fun EntityType.newNMSInstance(world: World): Entity {
    return this.nmsClass.constructTyped(ENTITY_DEFAULT_CONSTRUCTOR, world)
}