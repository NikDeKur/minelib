@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.hologram

import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityArmorStand
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import dev.nikdekur.minelib.ext.applyColors
import dev.nikdekur.minelib.nms.entity.MineEntity
import dev.nikdekur.minelib.nms.packet.PacketBuilder
import dev.nikdekur.minelib.nms.track.PersonalTrackerEntry
import dev.nikdekur.minelib.pentity.PersonalEntityImpl
import java.util.*

abstract class Hologram(world: World) : PersonalEntityImpl(world) {


    abstract fun getLocation(player: Player): Vector

    override fun newStack(player: Player): Collection<Entity> {
        val location = getLocation(player)
        val text = getFinalText(player)
        val list = LinkedList<EntityArmorStand>()


        for (line in text) {
            val entity = EntityArmorStand(world)
            entity.customName = line
            entity.customNameVisible = true
            entity.isSmall = true
            entity.isInvisible = true
            entity.isMarker = true
            entity.isSilent = true
            entity.setInvulnerable(true)
            entity.isNoGravity = true
            list.add(entity)
        }

        locateArmorStands(location, list)
        return list
    }

    val trackerFactory = PersonalTrackerEntry.factory(MineEntity.ARMOR_STAND)
    override fun newTrackerEntry(player: Player, entity: Entity, spigotViewDistance: Int) = trackerFactory.new(player, entity, spigotViewDistance)

    abstract fun getText(player: Player): Collection<String>

    inline fun getFinalText(player: Player) = getText(player).applyColors()


    fun locateArmorStands(location: Vector, armorStands: Collection<Entity>) {
        val locX = location.x
        var locY = location.y
        val locZ = location.z
        for (entity in armorStands.reversed()) {
            entity.setPosition(locX, locY, locZ)
            locY += 0.22
        }
    }


    fun update(player: Player) {
        val viewMap = tracker.viewMap[player] ?: return
        if (viewMap.isEmpty()) return
        val entities = viewMap.values

        val newName = getFinalText(player)

        // Respawn if text size changed
        if (entities.size != newName.size) {
            remove(player)
            spawn(player)
            return
        }

        entities.forEachIndexed { index, entity ->
            val line = newName[index]
            if (entity.customName != line) {
                entity.customName = line
                PacketBuilder.Entity.Data.updateShort(entity).send(player)
            }
        }
    }

    fun update() {
        viewers.forEach(this::update)
    }


    override fun toString(): String {
        return "Hologram(id=$id)"
    }
}