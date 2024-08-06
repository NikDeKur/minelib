@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import net.minecraft.server.v1_12_R1.EntityInsentient
import net.minecraft.server.v1_12_R1.EntityLiving
import net.minecraft.server.v1_12_R1.EntityTrackerEntry
import net.minecraft.server.v1_12_R1.EnumItemSlot
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import java.util.Arrays

inline val Entity.nms: net.minecraft.server.v1_12_R1.Entity
    get() = (this as org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity).handle

inline val LivingEntity.nms: EntityLiving
    get() = (this as org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity).handle

inline val Entity.tracker: EntityTrackerEntry?
    get() = world.nms.tracker.trackedEntities[entityId]

inline val net.minecraft.server.v1_12_R1.Entity.tracker: EntityTrackerEntry?
    get() = (world as WorldServer).tracker.trackedEntities[id]

inline val Entity.blockLocation: Location
    get() = location.asBlockLocation()

inline fun EntityInsentient.removeEquipmentDrop() {
    Arrays.fill(dropChanceHand, 0.0f)
    Arrays.fill(dropChanceArmor, 0.0f)
}

inline fun EntityInsentient.setArmor(helmet: ItemStack? = null, chestplate: ItemStack? = null, leggings: ItemStack? = null, boots: ItemStack? = null) {
    if (helmet != null) setSlot(EnumItemSlot.HEAD, helmet)
    if (chestplate != null) setSlot(EnumItemSlot.CHEST, chestplate)
    if (leggings != null) setSlot(EnumItemSlot.LEGS, leggings)
    if (boots != null) setSlot(EnumItemSlot.FEET, boots)
}

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
