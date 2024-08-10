@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.v1_12_R1.ext

import net.minecraft.server.v1_12_R1.EntityLiving
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

inline val Entity.nms: net.minecraft.server.v1_12_R1.Entity
    get() = (this as org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity).handle

inline val LivingEntity.nms: EntityLiving
    get() = (this as org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity).handle
/**
 * Set the entity location properly
 *
 * Calls [net.minecraft.server.v1_12_R1.Entity.setLocation] and [net.minecraft.server.v1_12_R1.Entity.setHeadRotation]
 * to update the position and head rotation of the entity.
 */
inline var net.minecraft.server.v1_12_R1.Entity.location: Location
    get() = Location(world.world, locX, locY, locZ, yaw, pitch)
    set(value) {
        setLocation(value.x, value.y, value.z, value.yaw, value.pitch)
        headRotation = value.yaw  // setLocation doesn't update head rotation
    }
