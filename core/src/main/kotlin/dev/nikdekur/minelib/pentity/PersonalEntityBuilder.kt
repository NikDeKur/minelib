package dev.nikdekur.minelib.pentity;

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.Unit

class PersonalEntityBuilder {
    private var onLeftClick: (ClickContext.Left) -> Unit = {}
    private var onRightClick: (ClickContext.Right) -> Unit = {}
    private var onRightAtClick: (ClickContext.RightAt) -> Unit = {}

    private var shouldSpawn: (Player) -> Boolean = { false }

    private var entitiesBuilder: ((Player) -> Collection<Entity>)? = null

    @PEntityDSL
    fun onLeftClick(action: (ClickContext.Left) -> Unit) {
        onLeftClick = action
    }

    @PEntityDSL
    fun onRightClick(action: (ClickContext.Right) -> Unit) {
        onRightClick = action
    }

    @PEntityDSL
    fun onRightAtClick(action: (ClickContext.RightAt) -> Unit) {
        onRightAtClick = action
    }

    @PEntityDSL
    fun shouldSpawn(action: (Player) -> Boolean) {
        shouldSpawn = action
    }

    @PEntityDSL
    fun entities(action: (Player) -> Collection<Entity>) {
        entitiesBuilder = action
    }


    fun build(): PersonalEntityData {

        require(entitiesBuilder != null) { "Entities must be set" }

        return PersonalEntityData(
            onLeftClick = onLeftClick,
            onRightClick = onRightClick,
            onRightAtClick = onRightAtClick,
            shouldSpawn = shouldSpawn,
            entitiesBuilder = entitiesBuilder!!
        )
    }
}