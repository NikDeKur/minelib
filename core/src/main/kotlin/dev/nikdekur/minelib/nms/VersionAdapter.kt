package dev.nikdekur.minelib.nms

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.ndkore.ext.getInstanceField
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

interface VersionAdapter {

    fun init(plugin: MineLib)

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

    fun setHighWalkSpeed(player: Player, speed: Float)
    fun setHighFlySpeed(player: Player, speed: Float)


    companion object {

        fun findAdapter(version: String): VersionAdapter? {
            val adapterClazz = try {
                Class.forName("dev.nikdekur.minelib.$version.VersionAdapter")
            } catch (_: ClassNotFoundException) {
                return null
            }
            val instance = adapterClazz.getInstanceField() as? VersionAdapter ?: return null
            return instance
        }
    }
}