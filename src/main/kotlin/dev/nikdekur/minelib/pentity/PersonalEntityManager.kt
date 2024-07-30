package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.hologram.HologramEntity
import dev.nikdekur.minelib.utils.Utils.debug
import dev.nikdekur.ndkore.ext.addById
import net.minecraft.server.v1_12_R1.Entity
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

class PersonalEntityManager(val world: World) {

    val entities = HashMap<UUID, PersonalEntity>()
    val entityByEntityId = HashMap<Int, PersonalEntity>()


    fun registerEntity(npc: PersonalEntity) {
        entities.addById(npc)
    }

    fun unregisterEntity(entityId: UUID) {
        entities.remove(entityId)
    }

    fun getEntity(npcId: UUID): PersonalEntity? {
        return entities[npcId]
    }




    fun registerPersonalEntity(entity: PersonalEntity, personalEntity: Entity) {
        entityByEntityId[personalEntity.id] = entity
    }

    fun unregisterPersonalEntity(entityId: Int) {
        entityByEntityId.remove(entityId)
    }

    fun getEntityByPersonalEntity(entityId: Int): PersonalEntity? {
        return entityByEntityId[entityId]
    }



    fun updateTracking(player: Player) {
        entities.values.forEach { entity ->
            if (entity is HologramEntity) {
                debug("Update hologram for ${player.name} | is visible: ${entity.isVisibleFor(player)}")
            }
            if (entity.isVisibleFor(player)) {
                entity.updateTracking(player)
            } else if (entity.shouldSpawn(player)) {
                entity.spawn(player)
            }
        }
    }

    fun removeTracking(player: Player) {
        LinkedList(entities.values).forEach { entity ->
            if (entity.isVisibleFor(player)) {
                entity.remove(player)
            }
        }
    }






    fun onUnload() {
        LinkedList(entities.values).forEach(PersonalEntity::remove)
        entities.clear()
        entityByEntityId.clear()
    }


}