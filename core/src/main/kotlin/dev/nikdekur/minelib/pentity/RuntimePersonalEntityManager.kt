package dev.nikdekur.minelib.pentity

import dev.nikdekur.ndkore.ext.addById
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RuntimePersonalEntityManager(
    override val world: World
) : PersonalEntityManager {

    val entities: MutableMap<UUID, PersonalEntity<*>> = ConcurrentHashMap()
    val entityByEntityId: MutableMap<Int, PersonalEntity<*>> = ConcurrentHashMap()

    override fun <C : PersonalEntityContext> createEntity(builder: PersonalEntityBuilder<C>): PersonalEntity<C> {
        val entity = builder.build(world)

        val decorator = EntityDecorator(this, entity)

        return decorator
    }


    override fun getEntity(bukkitEntityId: Int): PersonalEntity<*>? {
        return entityByEntityId[bukkitEntityId]
    }


    class EntityDecorator<C : PersonalEntityContext>(
        val manager: RuntimePersonalEntityManager,
        val original: PersonalEntity<C>
    ) : PersonalEntity<C> by original {

        override fun spawn(context: C): Collection<Entity> {
            return original.spawn(context)
                .onEach { manager.entityByEntityId[it.entityId] = original }
                .also { manager.entities.addById(this) }
        }

        override fun remove() {
            original.remove()

            // Remove the entity from the entities map
            manager.entities.remove(original.id)

            // Remove the entities from the entityByEntityId map
            viewers.forEach {
                val entities = getEntities(it)
                entities.forEach { entity ->
                    manager.entityByEntityId.remove(entity.entityId)
                }
            }
        }
    }

    override fun removeAll(player: Player) {
        LinkedList(entities.values).forEach { entity ->
            if (entity.isVisibleFor(player)) {
                entity.remove(player)
            }
        }
    }

    override fun removeAll() {
        entities.values.forEach { it.remove() }
        entities.clear()
    }
}