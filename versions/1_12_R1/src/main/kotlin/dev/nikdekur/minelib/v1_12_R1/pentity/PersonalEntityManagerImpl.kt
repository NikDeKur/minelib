package dev.nikdekur.minelib.v1_12_R1.pentity

import dev.nikdekur.minelib.hologram.EntityWithHologram
import dev.nikdekur.minelib.hologram.EntityWithHologramData
import dev.nikdekur.minelib.hologram.EntityWithHologramImpl
import dev.nikdekur.minelib.hologram.Hologram
import dev.nikdekur.minelib.hologram.HologramData
import dev.nikdekur.minelib.pentity.PersonalEntity
import dev.nikdekur.minelib.pentity.PersonalEntityData
import dev.nikdekur.minelib.pentity.PersonalEntityManager
import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.minelib.utils.Utils.debug
import dev.nikdekur.minelib.v1_12_R1.ext.nms
import dev.nikdekur.minelib.v1_12_R1.hologram.TrackHologram
import dev.nikdekur.minelib.v1_12_R1.nms.entity.MineEntityType
import dev.nikdekur.minelib.v1_12_R1.nms.track.PersonalTrackerEntry
import dev.nikdekur.ndkore.ext.addById
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

class PersonalEntityManagerImpl(override val world: World) : PersonalEntityManager {

    val entities = HashMap<UUID, PersonalEntity>()
    val entityByEntityId = HashMap<Int, PersonalEntity>()

    override fun newEntity(data: PersonalEntityData): PersonalEntity {
        val entity = object : TrackerPersonalEntity(this) {
            override fun newStack(player: Player): Collection<Entity> {
                return data.entitiesBuilder(player)
            }

            override fun newTrackerEntry(
                player: Player,
                entity: net.minecraft.server.v1_12_R1.Entity,
                spigotViewDistance: Int,
            ): PersonalTrackerEntry {
                val type = MineEntityType.BY_NMS_CLASS[entity.javaClass.name] ?: MineEntityType.UNKNOWN
                return PersonalTrackerEntry.factory(type).new(player, entity, spigotViewDistance)
            }

        }
        registerEntity(entity)
        return entity
    }

    override fun newHologram(data: HologramData): Hologram {
        val hologram = object : TrackHologram(this) {
            override fun getLocation(player: Player): AbstractLocation {
                return data.getLocation(player)
            }

            override fun getText(player: Player): Collection<String> {
                return data.getText(player)
            }

            override fun shouldSpawn(player: Player): Boolean {
                return data.shouldSpawn(player)
            }
        }
        registerEntity(hologram)
        return hologram
    }

    override fun newEntityWithHologram(data: EntityWithHologramData): EntityWithHologram {
        val entity = newEntity(data.entity)
        val hologram = newHologram(data.hologram)
        return EntityWithHologramImpl(entity, hologram, data.hologramOffset)
    }

    override fun registerEntity(npc: PersonalEntity) {
        entities.addById(npc)
    }

    override fun unregisterEntity(entityId: UUID) {
        entities.remove(entityId)
    }

    override fun getEntity(npcId: UUID): PersonalEntity? {
        return entities[npcId]
    }




    override fun registerPersonalEntity(entity: PersonalEntity, personalEntity: Entity) {
        entityByEntityId[personalEntity.nms.id] = entity
    }

    override fun unregisterPersonalEntity(entityId: Int) {
        entityByEntityId.remove(entityId)
    }

    override fun getEntityByPersonalEntity(entityId: Int): PersonalEntity? {
        return entityByEntityId[entityId]
    }



    override fun update(player: Player) {
        entities.values.forEach { entity ->
            if (entity is Hologram) {
                debug("Update hologram for ${player.name} | is visible: ${entity.isVisibleFor(player)}")
            }
            if (entity is TrackerPersonalEntity && entity.isVisibleFor(player)) {
                entity.updateTracking(player)
            } else if (entity.shouldSpawn(player)) {
                entity.spawn(player)
            }
        }
    }

    override fun clear(player: Player) {
        LinkedList(entities.values).forEach { entity ->
            if (entity.isVisibleFor(player)) {
                entity.remove(player)
            }
        }
    }






    override fun unload() {
        LinkedList(entities.values).forEach(PersonalEntity::remove)
        entities.clear()
        entityByEntityId.clear()
    }


}