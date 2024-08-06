package dev.nikdekur.minelib.nms

import dev.nikdekur.minelib.nms.protocol.ProtocolModule
import dev.nikdekur.minelib.pentity.ServerPersonalEntityManager
import dev.nikdekur.ndkore.ext.getInstanceField
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface VersionAdapter {

    /**
     * Expand the entity bounding box to the axis values
     *
     * Height will increase entity bounding box only in the top.
     *
     * You can use negative values to shrink the entity bounding box.
     *
     * None of these methods change the entity's hitbox for clients. It's only for server-side calculations.
     * Resizing will take effect for example in [World.getNearbyEntities].
     *
     * @param x the amount of blocks to expand the entity bounding box in the X axis
     * @param y the amount of blocks to expand the entity bounding box in the Y axis
     * @param z the amount of blocks to expand the entity bounding box in the Z axis
     */
    fun expandBB(entity: Entity, x: Float, y: Float, z: Float)


    fun setTag(item: ItemStack, tag: String, value: Any)
    fun getTag(item: ItemStack, tag: String): Any?
    fun removeTag(item: ItemStack, tag: String)
    fun hasTag(item: ItemStack, tag: String): Boolean
    fun getTags(item: ItemStack): Collection<String>
    fun getTagsMap(item: ItemStack): Map<String, Any>


    fun setWalkSpeed(player: Player, speed: Float)
    fun setFlySpeed(player: Player, speed: Float)

    fun <T : Entity> createEntity(world: World, type: EntityType): T

    fun getServerPersonalEntityManager(): ServerPersonalEntityManager
    fun getProtocolModule(): ProtocolModule


    companion object {

        fun getForVersion(version: String): VersionAdapter {
            val clazz = Class.forName("dev.nikdekur.minelib.nms.VersionAdapter_$version")
            val instance = clazz.getInstanceField() as VersionAdapter
            return instance
        }
    }
}