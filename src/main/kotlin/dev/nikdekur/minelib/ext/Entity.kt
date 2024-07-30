@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import net.minecraft.server.v1_12_R1.*
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

inline val Entity.nms: net.minecraft.server.v1_12_R1.Entity
    get() = (this as org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity).handle

inline val LivingEntity.nms: EntityLiving
    get() = (this as org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity).handle

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

inline val Entity.tracker: EntityTrackerEntry?
    get() = world.nms.tracker.trackedEntities[entityId]

inline val net.minecraft.server.v1_12_R1.Entity.tracker: EntityTrackerEntry?
    get() = (world as WorldServer).tracker.trackedEntities[id]



/**
 * Expand the entity bounding box to the axis values
 *
 * Height will increase entity bounding box only in the top.
 *
 * You can use negative values to shrink the entity bounding box.
 *
 * None of these methods change the entity's hitbox for clients. It's only for server-side calculations.
 * Resizing will take effect for example in [org.bukkit.World.getNearbyEntities].
 *
 * @param x the amount of blocks to expand the entity bounding box in the X axis
 * @param y the amount of blocks to expand the entity bounding box in the Y axis
 * @param z the amount of blocks to expand the entity bounding box in the Z axis
 */
inline fun net.minecraft.server.v1_12_R1.Entity.expandBB(x: Float, y: Float, z: Float) {
    val boundingBox = boundingBox

    val x0 = boundingBox.a
    val y0 = boundingBox.b
    val z0 = boundingBox.c
    val x1 = boundingBox.d
    val y1 = boundingBox.e
    val z1 = boundingBox.f
    val aabb = AxisAlignedBB(
        x0 - x,
        y0,
        z0 - z,

        x1 + x,
        y1 + y,
        z1 + z
    )
    a(aabb)
}

/**
 * Expand the entity bounding box to the axis values
 *
 * Height will increase entity bounding box only in the top.
 *
 * You can use negative values to shrink the entity bounding box.
 *
 * None of these methods change the entity's hitbox for clients. It's only for server-side calculations.
 * Resizing will take effect for example in [org.bukkit.World.getNearbyEntities].
 *
 * @param x the amount of blocks to expand the entity bounding box in the X axis
 * @param y the amount of blocks to expand the entity bounding box in the Y axis
 * @param z the amount of blocks to expand the entity bounding box in the Z axis
 */
inline fun Entity.expandBB(x: Float, y: Float, z: Float) {
    nms.expandBB(x, y, z)
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