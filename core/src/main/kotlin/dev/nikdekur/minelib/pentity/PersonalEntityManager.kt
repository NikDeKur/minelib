package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.hologram.EntityWithHologram
import dev.nikdekur.minelib.hologram.EntityWithHologramData
import dev.nikdekur.minelib.hologram.Hologram
import dev.nikdekur.minelib.hologram.HologramData
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.UUID

interface PersonalEntityManager {

    val world: World


    fun newEntity(data: PersonalEntityData): PersonalEntity
    fun newHologram(data: HologramData): Hologram
    fun newEntityWithHologram(data: EntityWithHologramData): EntityWithHologram

    fun registerEntity(npc: PersonalEntity)
    fun unregisterEntity(entityId: UUID)
    fun getEntity(npcId: UUID): PersonalEntity?




    fun registerPersonalEntity(entity: PersonalEntity, personalEntity: Entity)
    fun unregisterPersonalEntity(entityId: Int)
    fun getEntityByPersonalEntity(entityId: Int): PersonalEntity?

    fun update(player: Player)
    fun clear(player: Player)


    fun unload()
}


inline fun PersonalEntityManager.newEntity(builder: PersonalEntityBuilder.() -> Unit): PersonalEntity {
    val data = PersonalEntityBuilder().apply(builder).build()
    return newEntity(data)
}