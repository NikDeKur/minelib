package dev.nikdekur.minelib.v1_12_R1.pentity

import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.hologram.*
import dev.nikdekur.minelib.pentity.PersonalEntity
import dev.nikdekur.minelib.pentity.PersonalEntityData
import dev.nikdekur.minelib.pentity.PersonalEntityManager
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
        val entity = object : TrackerPersonalEntity() {
            override val world: World
                get() = this@PersonalEntityManagerImpl.world

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

            override fun shouldSpawn(player: Player): Boolean {
                return data.shouldSpawn(player)
            }

        }
        registerEntity(entity)

        return PersonalEntityDecorator(this, entity)
    }

    override fun newHologram(data: HologramData): Hologram {
        val hologram = HologramDecorator(this, TrackHologram(world, data))
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



    override fun updateAllEntitiesFor(player: Player) {
        entities.values.forEach { entity ->
            if (entity is Hologram) {
                bLogger.info("Update hologram for ${player.name} | is visible: ${entity.isVisibleFor(player)}")
            }

            if (entity is TrackerPersonalEntity) {
                entity.updateTracking(player)

            } else if (entity.shouldSpawn(player) && !entity.isVisibleFor(player)) {
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


    open class PersonalEntityDecorator(
        val manager: PersonalEntityManagerImpl,
        val entity: PersonalEntity,
    ) : PersonalEntity by entity {
        override fun spawn(player: Player): Iterable<Entity> {
            val stack = entity.spawn(player)
            stack.forEach { manager.registerPersonalEntity(entity, it) }
            return stack
        }

        override fun remove() {
            entity.remove()
            manager.unregisterEntity(id)
        }
    }



    open class HologramDecorator(
        manager: PersonalEntityManagerImpl,
        val hologram: Hologram,
    ) : PersonalEntityDecorator(manager, hologram), Hologram by hologram {
        override fun spawn(player: Player): Iterable<Entity> {
            val stack = hologram.spawn(player)
            stack.forEach { manager.registerPersonalEntity(hologram, it) }
            return stack
        }

        override fun remove() {
            hologram.remove()
            manager.unregisterEntity(id)
        }
    }
}