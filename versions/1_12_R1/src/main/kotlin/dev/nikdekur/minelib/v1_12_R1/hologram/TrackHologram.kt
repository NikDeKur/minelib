@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.v1_12_R1.hologram

import dev.nikdekur.minelib.ext.applyColors
import dev.nikdekur.minelib.hologram.Hologram
import dev.nikdekur.minelib.pentity.PersonalEntityManager
import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.minelib.v1_12_R1.nms.entity.MineEntityType
import dev.nikdekur.minelib.v1_12_R1.nms.packet.PacketBuilder
import dev.nikdekur.minelib.v1_12_R1.nms.track.PersonalTrackerEntry
import dev.nikdekur.minelib.v1_12_R1.pentity.TrackerPersonalEntity
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

abstract class TrackHologram(manager: PersonalEntityManager) : TrackerPersonalEntity(manager), Hologram {


    abstract override fun getLocation(player: Player): AbstractLocation

    override fun newStack(player: Player): Collection<Entity> {
        val location = getLocation(player)
        val text = getFinalText(player)
        val list = LinkedList<net.minecraft.server.v1_12_R1.EntityArmorStand>()


        for (line in text) {
            val entity = net.minecraft.server.v1_12_R1.EntityArmorStand(worldNMS)
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
        return list.map { it.bukkitEntity as ArmorStand }
    }

    val trackerFactory = PersonalTrackerEntry.factory(MineEntityType.ARMOR_STAND)
    override fun newTrackerEntry(player: Player, entity: net.minecraft.server.v1_12_R1.Entity, spigotViewDistance: Int) =
        trackerFactory.new(player, entity, spigotViewDistance)

    abstract override fun getText(player: Player): Collection<String>

    inline fun getFinalText(player: Player) = getText(player).applyColors()


    fun locateArmorStands(location: AbstractLocation, armorStands: Collection<net.minecraft.server.v1_12_R1.EntityArmorStand>) {
        val locX = location.x
        var locY = location.y
        val locZ = location.z
        for (entity in armorStands.reversed()) {
            entity.setPositionRotation(locX, locY, locZ, location.yaw, location.pitch)
            locY += 0.22
        }
    }


    override fun update(player: Player) {
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

    override fun update() {
        viewers.forEach(this::update)
    }


    override fun toString(): String {
        return "Hologram(id=$id)"
    }
}