package dev.nikdekur.minelib.nms

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.ndkore.service.manager.ServicesManager
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

interface VersionAdapter {

    val components: Collection<Any>
        get() = emptyList()

    suspend fun ServicesManager.registerServices() {}

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
        fun findAdapter(app: PluginApplication, version: String): VersionAdapter? {

            @Suppress("UNCHECKED_CAST", "kotlin:S6530")
            val adapterClazz = try {
                Class.forName("dev.nikdekur.minelib.$version.VersionAdapter_$version")
                        as? Class<VersionAdapter>
            } catch (_: ClassNotFoundException) {
                return null
            }

            return adapterClazz?.getConstructor(PluginApplication::class.java)?.newInstance(app)
        }
    }
}