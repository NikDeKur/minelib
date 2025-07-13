package dev.nikdekur.minelib.v1_12_R1.nms.track

import dev.nikdekur.minelib.v1_12_R1.ext.nms
import dev.nikdekur.minelib.v1_12_R1.nms.entity.MineEntityType
import dev.nikdekur.minelib.v1_12_R1.nms.entity.MineEntityType.Companion.type
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.EntityTracker
import net.minecraft.server.v1_12_R1.EntityTrackerEntry
import org.bukkit.entity.Player
import org.spigotmc.TrackingRange

/**
 * Custom implementation of [EntityTrackerEntry] that allows tracking entities for a specific player.
 *
 * NMS implementation of [EntityTrackerEntry] only allows tracking entities for all players.
 * This implementation is created to support Personal Entities and make NMS tracking system automatically
 * handle their visibility for each player.
 *
 * @param viewer The player that will see the entity.
 * @param entity The entity that will be seen by the player.
 * @param renderDistance The render distance of the entity, it's the distance where the entity will be visible.
 * @param spigotViewDistance The spigot view distance from original [EntityTracker] (hardcoded).
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



    companion object {
        @JvmStatic
        fun new(
            viewer: EntityPlayer,
            entity: Entity,
            spigotViewDistance: Int,
            type: MineEntityType = entity.type
        ): PersonalTrackerEntry {
            val renderDistance = TrackingRange.getEntityTrackingRange(entity, type.defaultRenderDistance)
            return PersonalTrackerEntry(viewer, entity, renderDistance, spigotViewDistance, type.updateDelay, type.pushable)
        }

        @JvmStatic
        fun new(
            viewer: Player,
            entity: Entity,
            spigotViewDistance: Int,
            type: MineEntityType = entity.type
        ): PersonalTrackerEntry {
            return new(viewer.nms, entity, spigotViewDistance, type)
        }
    }
}