package dev.nikdekur.minelib.pentity

import org.bukkit.World
import org.bukkit.entity.Player

interface PersonalEntityManager {
    val world: World

    fun <C : PersonalEntityContext> createEntity(builder: PersonalEntityBuilder<C>): PersonalEntity<C>

    fun getEntity(bukkitEntityId: Int): PersonalEntity<*> ?

    fun removeAll(player: Player)
    fun removeAll()
}

interface PersonalEntityBuilder<C : PersonalEntityContext> {
    fun build(world: World): PersonalEntity<C>
}

