package dev.nikdekur.minelib.nms.track

import dev.nikdekur.minelib.ext.nms
import dev.nikdekur.minelib.nms.entity.MineEntity
import dev.nikdekur.minelib.nms.entity.MineEntityType
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.EntityTrackerEntry
import org.bukkit.entity.Player
import org.spigotmc.TrackingRange

/**
 * @param viewer The player that will see the entity.
 * @param entity The entity that will be seen by the player.
 * @param renderDistance The render distance of the entity, it's the distance where the entity will be visible.
 * @param spigotViewDistance The spigot view distance from original [EntityTracker].
 * @param updateDelay The delay between each update of the entity.
 * @param pushable If the entity is pushable.
 */
open class PersonalTrackerEntry(
    val viewer: EntityPlayer,
    val entity: Entity,
    renderDistance: Int,
    spigotViewDistance: Int,
    updateDelay: Int,
    pushable: Boolean
) : EntityTrackerEntry(entity, renderDistance, spigotViewDistance, updateDelay, pushable) {


    fun scan() {
        super.updatePlayer(viewer)
    }

    override fun updatePlayer(player: EntityPlayer) {
        if (viewer.uniqueID != player.uniqueID) return
        super.updatePlayer(player)
    }



    class Factory(val type: MineEntity) {

        fun new(viewer: EntityPlayer, entity: Entity, spigotViewDistance: Int): PersonalTrackerEntry {
            val renderDistance = TrackingRange.getEntityTrackingRange(entity, type.defaultRenderDistance)
            return PersonalTrackerEntry(viewer, entity, renderDistance, spigotViewDistance, type.updateDelay, type.pushable)
        }

        fun new(viewer: Player, entity: Entity, spigotViewDistance: Int): PersonalTrackerEntry {
            return new(viewer.nms, entity, spigotViewDistance)
        }
    }


    companion object {
        @JvmStatic
        fun factory(type: MineEntityType) = Factory(type)
    }
}