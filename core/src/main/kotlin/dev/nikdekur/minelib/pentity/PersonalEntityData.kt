package dev.nikdekur.minelib.pentity

import org.bukkit.entity.Entity
import org.bukkit.entity.Player

data class PersonalEntityData(
    val onLeftClick: (ClickContext.Left) -> Unit,
    val onRightClick: (ClickContext.Right) -> Unit,
    val onRightAtClick: (ClickContext.RightAt) -> Unit,
    val shouldSpawn: (Player) -> Boolean,
    val entitiesBuilder: (Player) -> Collection<Entity>
)